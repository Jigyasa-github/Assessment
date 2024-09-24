package Assessment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentInfoApp {
    private JFrame frame;
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField mobileField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentInfoApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Student Information Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("ID:"), gbc);
        
        idField = new JTextField();
        gbc.gridx = 1;
        frame.add(idField, gbc);

        // First Name Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("First Name:"), gbc);

        firstNameField = new JTextField();
        gbc.gridx = 1;
        frame.add(firstNameField, gbc);

        // Last Name Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(new JLabel("Last Name:"), gbc);

        lastNameField = new JTextField();
        gbc.gridx = 1;
        frame.add(lastNameField, gbc);

        // Email Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(new JLabel("Email:"), gbc);

        emailField = new JTextField();
        gbc.gridx = 1;
        frame.add(emailField, gbc);

        // Mobile Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(new JLabel("Mobile:"), gbc);

        mobileField = new JTextField();
        gbc.gridx = 1;
        frame.add(mobileField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new InsertAction());
        buttonPanel.add(insertButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new UpdateAction());
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new DeleteAction());
        buttonPanel.add(deleteButton);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchAction());
        buttonPanel.add(searchButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        frame.add(buttonPanel, gbc);

        frame.setVisible(true);
    }

    private class InsertAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String mobile = mobileField.getText();
            insertStudentInfo(firstName, lastName, email, mobile);
        }
    }

    private class UpdateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String mobile = mobileField.getText();
            updateStudentInfo(id, firstName, lastName, email, mobile);
        }
    }

    private class DeleteAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            deleteStudentInfo(id);
        }
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            searchStudentInfo(id);
        }
    }

    private void insertStudentInfo(String firstName, String lastName, String email, String mobile) {
        String query = "INSERT INTO students (firstname, lastname, email, mobile) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, mobile);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Student information saved successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving student information: " + ex.getMessage());
        }
    }

    private void updateStudentInfo(String id, String firstName, String lastName, String email, String mobile) {
        String query = "UPDATE students SET firstname = ?, lastname = ?, email = ?, mobile = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, mobile);
            stmt.setInt(5, Integer.parseInt(id));
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Student information updated successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "No student found with that ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating student information: " + ex.getMessage());
        }
    }

    private void deleteStudentInfo(String id) {
        String query = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(id));
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(frame, "Student information deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "No student found with that ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting student information: " + ex.getMessage());
        }
    }

    private void searchStudentInfo(String id) {
        String query = "SELECT firstname, lastname, email, mobile FROM students WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                firstNameField.setText(rs.getString("firstname"));
                lastNameField.setText(rs.getString("lastname"));
                emailField.setText(rs.getString("email"));
                mobileField.setText(rs.getString("mobile"));
            } else {
                JOptionPane.showMessageDialog(frame, "No student found with that ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error searching for student information: " + ex.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/demodb";
        String user = "root"; // Replace with your MySQL username
        String password = "root"; // Replace with your MySQL password
        return DriverManager.getConnection(url, user, password);
    }
}
