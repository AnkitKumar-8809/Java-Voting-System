package com.mycompany.votingsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VotingSystem {

    public static void main(String[] args) {
        VotingGUI gui = new VotingGUI(); // Create and display the GUI
    }

    // Method to authenticate the user
    public boolean authenticateUser(String username, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // true if user exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to get the user ID based on username
    public int getUserId(String username) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT user_id FROM Users WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("user_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user not found
    }

    // Method to check if user has already voted
    public boolean hasUserVoted(int userId) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM Votes WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // true if vote exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume already voted on error
        }
    }

    // Method to get all candidates from the database
    public List<String> getCandidates() {
        List<String> candidates = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM Candidates";
            try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String candidateName = rs.getString("name");
                    candidates.add(candidateName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidates;
    }

    // Method to submit a vote
    public boolean submitVote(int userId, int candidateId) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "INSERT INTO Votes (user_id, candidate_id, vote_time) VALUES (?, ?, NOW())";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, candidateId);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to reset all votes in the database
    public boolean resetVotes() {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "DELETE FROM Votes"; // Delete all votes
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0; // Return true if rows are deleted
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to get live votes for each candidate
    public List<String> getLiveVotes() {
        List<String> liveVotes = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT c.name, COUNT(v.vote_id) AS vote_count FROM Candidates c "
                    + "LEFT JOIN Votes v ON c.candidate_id = v.candidate_id GROUP BY c.candidate_id";
            try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String candidateName = rs.getString("name");
                    int voteCount = rs.getInt("vote_count");
                    liveVotes.add(candidateName + ": " + voteCount + " votes");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liveVotes;
    }
}
