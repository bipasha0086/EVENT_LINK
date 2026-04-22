package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@WebServlet("/chatbot")
public class ChatbotServlet extends HttpServlet {
    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Set<String> EVENT_BOOKING_KEYWORDS = new HashSet<>(Arrays.asList(
        "event", "events", "book", "booking", "seat", "seats", "pay", "payment", "ticket", "tickets",
        "theatre", "theater", "cinema", "movie", "schedule", "timing", "show", "alert", "notification",
        "dashboard", "register", "login", "admin", "user", "allocate", "deallocate", "threat area"
    ));

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            String apiKey = firstNonBlank(
                System.getenv("CHATBOT_API_KEY"),
                System.getenv("GROQ_API_KEY"),
                System.getenv("XAI_API_KEY"),
                System.getProperty("chatbot.api.key"),
                System.getProperty("groq.api.key"),
                System.getProperty("xai.api.key")
            );
            if (apiKey == null) {
                resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                out.print("{\"status\":\"error\",\"message\":\"Missing Groq API key. Set CHATBOT_API_KEY or GROQ_API_KEY on the backend and restart the server.\"}");
                return;
            }

            String body = readBody(req);
            JsonObject input = GSON.fromJson(body, JsonObject.class);
            if (input == null || !input.has("message")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"message is required\"}");
                return;
            }

            String message = input.get("message").getAsString().trim();
            if (message.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"message is required\"}");
                return;
            }

            if (!isEventBookingQuestion(message)) {
                out.print("{\"status\":\"success\",\"reply\":\"I can only answer questions related to this Event Booking system. Ask about login/register, role dashboards, theatre schedules, seat booking, payments, allocation/deallocation, or notifications.\",\"model\":\"local-filter\"}");
                return;
            }

            String role = input.has("role") && !input.get("role").isJsonNull() ? input.get("role").getAsString() : "guest";
            String context = input.has("context") && !input.get("context").isJsonNull() ? input.get("context").getAsString() : "";
            List<String> models = buildModelCandidates();
            String lastFailure = null;

            for (String model : models) {
                JsonObject requestBody = buildRequestBody(model, message, context, role);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(requestBody), StandardCharsets.UTF_8))
                    .build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    JsonObject responseJson = GSON.fromJson(response.body(), JsonObject.class);
                    String reply = extractReply(responseJson);
                    out.print("{\"status\":\"success\",\"reply\":" + JsonUtil.string(reply) + ",\"model\":" + JsonUtil.string(model) + "}");
                    return;
                }

                lastFailure = response.body();
                if (isApiKeyRejected(response.body())) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"status\":\"error\",\"message\":\"Invalid Groq API key. Set a valid CHATBOT_API_KEY or GROQ_API_KEY and restart the backend.\"}");
                    return;
                }
                if (!isModelMissing(response.body())) {
                    break;
                }
            }

            resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            out.print("{\"status\":\"error\",\"message\":\"Groq API request failed\",\"details\":" + JsonUtil.string(lastFailure == null ? "Unknown error" : lastFailure) + "}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }

    private static JsonObject buildRequestBody(String model, String message, String context, String role) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);

        JsonArray messages = new JsonArray();
        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("content",
            "You are the EventBooking assistant for this specific application only. " +
            "Answer only questions related to this app's features and workflows: login/register, role dashboards, theatre schedules, seat booking, payments, allocation/deallocation, and notifications. " +
            "If a question is outside this scope, refuse briefly and ask the user to ask an Event Booking system question. " +
            "Keep answers concise, practical, and app-specific. Context: " + context + " Role: " + role
        );
        messages.add(system);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", message);
        messages.add(user);

        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.3);
        return requestBody;
    }

    private static String extractReply(JsonObject responseJson) {
        String reply = "I could not generate a response right now.";
        if (responseJson != null && responseJson.has("choices")) {
            JsonArray choices = responseJson.getAsJsonArray("choices");
            if (choices.size() > 0) {
                JsonObject choice = choices.get(0).getAsJsonObject();
                if (choice.has("message")) {
                    JsonObject choiceMessage = choice.getAsJsonObject("message");
                    if (choiceMessage.has("content")) {
                        reply = choiceMessage.get("content").getAsString().trim();
                    }
                }
            }
        }
        return reply;
    }

    private static boolean isModelMissing(String responseBody) {
        if (responseBody == null) {
            return false;
        }
        String lower = responseBody.toLowerCase(java.util.Locale.ROOT);
        return lower.contains("model not found") || lower.contains("invalid argument") || lower.contains("unknown model") || lower.contains("model does not exist");
    }

    private static boolean isApiKeyRejected(String responseBody) {
        if (responseBody == null) {
            return false;
        }
        String lower = responseBody.toLowerCase(java.util.Locale.ROOT);
        return lower.contains("incorrect api key") || lower.contains("invalid api key") || lower.contains("unauthorized");
    }

    private static List<String> buildModelCandidates() {
        List<String> candidates = new ArrayList<>();

        String configuredModels = firstNonBlank(System.getenv("CHATBOT_MODELS"), System.getenv("GROQ_MODELS"), System.getProperty("chatbot.models"), System.getProperty("groq.models"));
        if (configuredModels != null) {
            for (String candidate : configuredModels.split(",")) {
                String trimmed = candidate.trim();
                if (!trimmed.isEmpty()) {
                    candidates.add(trimmed);
                }
            }
        }

        String configuredModel = firstNonBlank(System.getenv("CHATBOT_MODEL"), System.getenv("GROQ_MODEL"), System.getProperty("chatbot.model"), System.getProperty("groq.model"));
        if (configuredModel != null) {
            candidates.add(configuredModel);
        }

        candidates.addAll(Arrays.asList(
            "llama-3.3-70b-versatile",
            "llama-3.1-70b-versatile",
            "llama-3.1-8b-instant",
            "mixtral-8x7b-32768",
            "gemma2-9b-it"
        ));

        return candidates;
    }

    private static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
        }
        return sb.toString();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    private static boolean isEventBookingQuestion(String message) {
        String lower = message == null ? "" : message.toLowerCase(Locale.ROOT);
        for (String keyword : EVENT_BOOKING_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}