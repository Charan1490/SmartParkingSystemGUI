import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileManagementMain extends JFrame implements ActionListener {
    private Connection connection;
    private JPanel contentPanel;
    private JFrame window;




    public void actionPerformed(ActionEvent e) {
        // Handle actions if needed
    }
    public ProfileManagementMain() {
        createWindow();
        profile();
    }
    private void createWindow() {
        window = new JFrame("Smart Parking System");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);
    }


    private void profile() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Set background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("images/login_background.jpg"));
        JLabel backgroundLabel = new JLabel(backgroundImage);
        contentPanel.add(backgroundLabel, BorderLayout.CENTER);

        JPanel loginButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        loginButtonPanel.setOpaque(false);

        // Create New Profile button
        JButton createProfileButton = createIconButton("src/main/images/new_user1.jpg", "Create New Profile");
        createProfileButton.addActionListener(e -> createNewProfile());
        loginButtonPanel.add(createProfileButton);

        // Existing Profile button
        JButton existingProfileButton = createIconButton("src/main/images/existing_profile_icon.png", "Existing Profile");
        existingProfileButton.addActionListener(e -> viewExistingProfile());
        loginButtonPanel.add(existingProfileButton);

        JButton deleteProfileButton = createIconButton("src/main/images/delete_profile.jpg", "Delete Profile");
        deleteProfileButton.addActionListener(e -> deleteProfile());
        loginButtonPanel.add(deleteProfileButton);

        // Back to Main Page button
        JButton backButton = createIconButton("src/main/images/back to main.png", "Back to Main Page");
        backButton.addActionListener(e -> {
            // Close the current window
            window.dispose();

            // Open the SmartParkingSystem login page window
            SwingUtilities.invokeLater(SmartParkingSystem::new);
        });
        loginButtonPanel.add(backButton); // Add the Back to Main Page button to the panel

        contentPanel.add(loginButtonPanel, BorderLayout.SOUTH);
        window.add(contentPanel);
        window.setVisible(true);

        connectToDatabase(); // Connect to the database upon GUI initialization
    }


    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/charan", "root", "Charan@123");
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }
    private void createNewProfile() {
        JTextField nameField = new JTextField(20);
        JTextField mobileField = new JTextField(10);
        JTextField vehicleNumberField = new JTextField(10);
        JTextField vehicleTypeField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Mobile Number:"));
        panel.add(mobileField);
        panel.add(new JLabel("Vehicle Number:"));
        panel.add(vehicleNumberField);
        panel.add(new JLabel("Vehicle Type:"));
        panel.add(vehicleTypeField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Create New Profile", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            // Validate input
            String name = nameField.getText().trim();
            String mobileNumber = mobileField.getText().trim();
            String vehicleNumber = vehicleNumberField.getText().trim();
            String vehicleType = vehicleTypeField.getText().trim();

            if (name.isEmpty() || mobileNumber.isEmpty() || vehicleNumber.isEmpty() || vehicleType.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert data into the database
            try {
                String query = "INSERT INTO user_profiles (name, mobile_number, vehicle_number, vehicle_type) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, mobileNumber);
                statement.setString(3, vehicleNumber);
                statement.setString(4, vehicleType);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Your details have been stored in the database successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to store your details in the database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error storing data in the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Set the location of the dialog to the center of the screen
        Window dialogWindow = SwingUtilities.windowForComponent(panel);
        if (dialogWindow != null) {
            dialogWindow.setLocationRelativeTo(null);
        }
    }



    private void viewExistingProfile() {
        String mobileNumber = JOptionPane.showInputDialog(this, "Enter your mobile number:");
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            try {
                String query = "SELECT * FROM user_profiles WHERE mobile_number = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, mobileNumber);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String vehicleNumber = resultSet.getString("vehicle_number");
                    String vehicleType = resultSet.getString("vehicle_type");

                    JOptionPane.showMessageDialog(this, "Name: " + name + "\nMobile Number: " + mobileNumber + "\nVehicle Number: " + vehicleNumber + "\nVehicle Type: " + vehicleType, "Profile Details", JOptionPane.INFORMATION_MESSAGE);

                    // You can add logic here to retrieve parking history and display it
                } else {
                    JOptionPane.showMessageDialog(this, "No profile found for the provided mobile number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error retrieving profile data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteProfile() {
        String mobileNumber = JOptionPane.showInputDialog(this, "Enter your mobile number to delete profile:");
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            try {
                String deleteQuery = "DELETE FROM user_profiles WHERE mobile_number = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setString(1, mobileNumber);
                int rowsAffected = deleteStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Your profile has been deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No profile found for the provided mobile number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    // Method to create IconButton with error handling
    private JButton createIconButton(String imagePath, String buttonText) {
        ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
        JButton button = new JButton(buttonText, icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProfileManagementMain::new);
    }
}
