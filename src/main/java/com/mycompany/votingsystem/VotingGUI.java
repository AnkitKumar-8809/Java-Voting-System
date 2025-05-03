package com.mycompany.votingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class VotingGUI {

    private JFrame frame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnVote;
    private JComboBox<String> candidateComboBox;
    private VotingSystem votingSystem;
    private int loggedInUserId;
    private boolean isAdmin; // To track if the logged-in user is an admin

    public VotingGUI() {
        votingSystem = new VotingSystem();
        frame = new JFrame("Voting System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showLoginScreen();
        frame.setVisible(true);
    }

    // Show the login screen
    private void showLoginScreen() {
        frame.getContentPane().removeAll();
        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Username:"));
        txtUsername = new JTextField(20);
        frame.add(txtUsername);
        frame.add(new JLabel("Password:"));
        txtPassword = new JPasswordField(20);
        frame.add(txtPassword);
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                if (votingSystem.authenticateUser(username, password)) {
                    loggedInUserId = votingSystem.getUserId(username);
                    if (loggedInUserId == -1) {
                        JOptionPane.showMessageDialog(frame, "Error retrieving user ID.");
                        return;
                    }
                    // Check if the user is an admin
                    isAdmin = username.equals("admin"); // Change this as needed
                    if (isAdmin) {
                        JOptionPane.showMessageDialog(frame, "Admin login successful");
                        showAdminPanel();
                    } else {
                        if (votingSystem.hasUserVoted(loggedInUserId)) {
                            JOptionPane.showMessageDialog(frame, "You have already voted.");
                            return;
                        }
                        JOptionPane.showMessageDialog(frame, "Login successful");
                        showVoteScreen();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid login");
                }
            }
        });
        frame.add(btnLogin);
        frame.setSize(300, 200);
        frame.revalidate();
        frame.repaint();
    }

    // Show the vote screen for regular users
    private void showVoteScreen() {
        frame.getContentPane().removeAll();
        frame.setLayout(new FlowLayout());
        candidateComboBox = new JComboBox<>();
        for (String candidate : votingSystem.getCandidates()) {
            candidateComboBox.addItem(candidate);
        }
        frame.add(new JLabel("Select Candidate:"));
        frame.add(candidateComboBox);
        btnVote = new JButton("Vote");
        btnVote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedCandidateId = candidateComboBox.getSelectedIndex() + 1;
                boolean voteSuccess = votingSystem.submitVote(loggedInUserId, selectedCandidateId);
                if (voteSuccess) {
                    JOptionPane.showMessageDialog(frame, "Vote submitted successfully");
                    showLoginScreen(); // Go back to login screen
                } else {
                    JOptionPane.showMessageDialog(frame, "Error submitting vote");
                }
            }
        });
        frame.add(btnVote);
        frame.setSize(300, 200);
        frame.revalidate();
        frame.repaint();
    }

    // Show the admin panel (reset votes and show live votes)
    private void showAdminPanel() {
        frame.getContentPane().removeAll();
        frame.setLayout(new FlowLayout());

        // Button to reset all votes
        JButton btnResetVotes = new JButton("Reset Votes");
        btnResetVotes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to reset all votes?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean resetSuccess = votingSystem.resetVotes();
                    if (resetSuccess) {
                        JOptionPane.showMessageDialog(frame, "Votes have been reset.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error resetting votes.");
                    }
                }
            }
        });
        frame.add(btnResetVotes);

        // Button to show live votes
        JButton btnShowLiveVotes = new JButton("Show Live Votes");
        btnShowLiveVotes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<String> liveVotes = votingSystem.getLiveVotes();
                StringBuilder liveVotesDisplay = new StringBuilder();
                for (String vote : liveVotes) {
                    liveVotesDisplay.append(vote).append("\n");
                }
                JOptionPane.showMessageDialog(frame, liveVotesDisplay.toString());
            }
        });
        frame.add(btnShowLiveVotes);

        // Button to log out (return to login screen)
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginScreen();
            }
        });
        frame.add(btnLogout);

        frame.setSize(300, 200);
        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
        new VotingGUI(); // Launch the GUI
    }
}
