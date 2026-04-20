package dao;

import model.Theatre;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TheatreDAO {
    public List<Theatre> getAllTheatres() throws SQLException {
        List<Theatre> theatres = new ArrayList<>();
        String sql = "SELECT * FROM theatres ORDER BY theatre_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                theatres.add(mapTheatre(rs));
            }
        }
        return theatres;
    }

    public Theatre getTheatreById(int theatreId) throws SQLException {
        String sql = "SELECT * FROM theatres WHERE theatre_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theatreId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapTheatre(rs);
            }
        }
        return null;
    }

    private Theatre mapTheatre(ResultSet rs) throws SQLException {
        return new Theatre(
            rs.getInt("theatre_id"),
            rs.getString("name"),
            rs.getString("area"),
            rs.getString("map_query")
        );
    }
}
