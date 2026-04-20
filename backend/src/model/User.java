package model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String role; // USER, ADMIN, THEATRE
    private String threatArea;
    private Integer theatreId;

    public User() {}

    public User(int userId, String username, String password, String email, String role, String threatArea, Integer theatreId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.threatArea = threatArea;
        this.theatreId = theatreId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getThreatArea() { return threatArea; }
    public void setThreatArea(String threatArea) { this.threatArea = threatArea; }
    public Integer getTheatreId() { return theatreId; }
    public void setTheatreId(Integer theatreId) { this.theatreId = theatreId; }
}
