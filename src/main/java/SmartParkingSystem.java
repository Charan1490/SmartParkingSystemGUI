import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
public class SmartParkingSystem {

    private enum MenuChoice {
        CHECK_AVAILABILITY,
        OCCUPY_PARKING_SPOT,
        VACATE_PARKING_SPOT,
        FIND_NEAREST_VACANT_SPOT,
        AUTOMATED_VEHICLE_STORAGE,
        AUTOMATIC_VEHICLE_WITHDRAWAL,
        RESERVATION,
        DISPLAY_PARKING_STATUS,
        EXIT
    }

    private static final int RESERVATION_TIME_LIMIT_MINUTES = 15;
    private JFrame window;
    private JPanel contentPanel;

    // Parking System Data
    private int totalRows = 0;
    private int totalColumns = 0;
    private int[][] parkingSpots;
    private String[][] reservedSpots;
    private String[] vehicleNumbers;
    private Map<String, LocalDateTime> entryTimes = new HashMap<>();
    private Map<String, LocalDateTime> reservationTimes = new HashMap<>();

    // Constructor
    public SmartParkingSystem() {
        createWindow();
        setupLoginPage();
    }

    private void createWindow() {
        window = new JFrame("Smart Parking System");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);
    }




    private void setupLoginPage() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Load and set login image
        ImageIcon loginBackground = new ImageIcon(getClass().getResource("images/logincanva.png"));
        JLabel loginBackgroundLabel = new JLabel(loginBackground);
        contentPanel.add(loginBackgroundLabel, BorderLayout.CENTER);

        // Panel to hold login buttons
        JPanel loginButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        loginButtonPanel.setOpaque(false);

        // Organizer Login Icon
        JButton organizerLoginButton = createIconButton("src/main/images/organizer icon.png", "Organizer Login");
        organizerLoginButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(window, "Enter username:");
            String password = JOptionPane.showInputDialog(window, "Enter password:");
            if ("admin".equals(username) && "1234".equals(password)) {
                setupOrganizerMainPage();
            } else {
                JOptionPane.showMessageDialog(window, "Invalid username or password.");
            }
        });

        loginButtonPanel.add(organizerLoginButton);

        // Guest Login Icon
        JButton guestLoginButton = createIconButton("src/main/images/guest icon.jpg", "Guest Login");
        guestLoginButton.addActionListener(e -> setupGuestMainPage());
        loginButtonPanel.add(guestLoginButton);

        // Profile Management Icon and Button
        JButton profileManagementButton = createIconButton("src/main/images/profile_icon.png", "Profile Management");
        profileManagementButton.addActionListener(e -> {
            // Open the Profile Management window only when the button is clicked
            ProfileManagementMain profileWindow = new ProfileManagementMain();
            profileWindow.setVisible(true);
        });
        loginButtonPanel.add(profileManagementButton);

        JButton loginInfoButton = createInfoButton("src/main/images/info_icon.png", "Login Instructions",
                "Please select 'Organizer Login' if you are the organizer and want to allocate parking space else choose  'Guest Login' to proceed.");
        loginButtonPanel.add(loginInfoButton);

        contentPanel.add(loginButtonPanel, BorderLayout.SOUTH);
        window.add(contentPanel);
        window.setVisible(true);
    }


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
    private JButton createInfoButton(String imagePath, String buttonText, String description) {
        ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
        JButton button = new JButton(buttonText, icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding

        // Show description when clicked
        button.addActionListener(e -> JOptionPane.showMessageDialog(window, description));

        return button;
    }


    private void setupOrganizerMainPage() {
        // Load background image for organizer page
        ImageIcon organizerBackground = new ImageIcon(getClass().getResource("/images/or.jpg"));
        JLabel organizerBackgroundLabel = new JLabel(organizerBackground);

        // Create a panel to hold the buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1));
        buttonsPanel.setOpaque(false); // Make panel transparent

        // Organizer Login Icon
        JButton checkAvailabilityButton = createIconButton("src/main/images/availablity.jpg", "Check Availability");
        checkAvailabilityButton.addActionListener(e -> checkParkingAvailability());

        JButton backButton = createIconButton("src/main/images/back to main.png", "Back to Main Page");
        backButton.addActionListener(e -> {
            // Clear any existing content and set up the initial login page

            window.getContentPane().removeAll();
            setupLoginPage();
            window.revalidate();
            window.repaint();
        });

        buttonsPanel.add(checkAvailabilityButton);
        buttonsPanel.add(backButton);

        // Create a panel to hold the background image and buttons panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(organizerBackgroundLabel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);
        JButton organizerInfoButton = createInfoButton("src/main/images/info_icon.png", "Organizer Page Instructions",
                "You can check availability of place and allocate number of parking slots.");
        buttonsPanel.add(organizerInfoButton, BorderLayout.WEST);

        window.getContentPane().removeAll();
        window.getContentPane().add(panel);
        window.revalidate();
        window.repaint();
    }

    private void setupGuestMainPage() {
        // Load background image for guest page
        ImageIcon guestBackground =  new ImageIcon(getClass().getResource("/images/gu.jpg"));
        JLabel guestBackgroundLabel = new JLabel(guestBackground);

        // Create a panel to hold the buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1));
        buttonsPanel.setOpaque(false); // Make panel transparent

        JButton normalParkingButton = createIconButton("src/main/images/normal.png", "Normal Parking");
        normalParkingButton.addActionListener(e -> setupNormalParkingSubPage());

        JButton findNearestVacantSpotButton = createIconButton("src/main/images/find nearest.png", "Find Nearest Vacant Spot");
        findNearestVacantSpotButton.addActionListener(e -> findNearestVacantSpot());

        JButton reservationButton = createIconButton("src/main/images/reservation.png", "Reservation");
        reservationButton.addActionListener(e -> reservation());

        JButton automaticParkingButton = createIconButton("src/main/images/autopark.png", "Automatic Parking");
        automaticParkingButton.addActionListener(e -> setupAutomaticParkingSubPage());

        JButton displayParkingStatusButton = createIconButton("src/main/images/display.png", "Display Parking Status");
        displayParkingStatusButton.addActionListener(e -> displayParkingStatus());

        JButton exitButton = createIconButton("src/main/images/exit.png", "Exit");
        exitButton.addActionListener(e -> window.dispose());
        JButton guestInfoButton = createInfoButton("src/main/images/info_icon.png", "Guest Page Instructions",
                "You can perform various operations as a guest.\n1.Normal parking-you can place your vehicle by giving your vehicle number \n2.Find nearest vacant spot-you can choose this get nearby vacant spots for parking.\n3.Reservation-you can give your mobile number and password and reserve a parking spot for max 15 minutes.\n4.Automatic parking-you can choose valet parking services here.\n5.Display-you can view the parking status of vehicles ");
        buttonsPanel.add(guestInfoButton, BorderLayout.WEST);

        buttonsPanel.add(normalParkingButton);
        buttonsPanel.add(findNearestVacantSpotButton);
        buttonsPanel.add(reservationButton);
        buttonsPanel.add(automaticParkingButton);
        buttonsPanel.add(displayParkingStatusButton);
        buttonsPanel.add(exitButton);

        // Create a panel to hold the background image and buttons panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(guestBackgroundLabel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);

        window.getContentPane().removeAll();
        window.getContentPane().add(panel);
        window.revalidate();
        window.repaint();
    }



    private void setupNormalParkingSubPage() {
        // Load background image for normal parking subpage
        ImageIcon normalParkingBackground =  new ImageIcon(getClass().getResource("images/normal parking.png"));
        JLabel normalParkingBackgroundLabel = new JLabel(normalParkingBackground);

        // Create a panel to hold the buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1));
        buttonsPanel.setOpaque(false); // Make panel transparent

        // Occupy Parking Spot Icon
        JButton occupyParkingSpotButton = createIconButton("src/main/images/occupy.jpg", "Occupy Parking Spot");
        occupyParkingSpotButton.addActionListener(e -> occupyParkingSpot());

        // Vacate Parking Spot Icon
        JButton vacateParkingSpotButton = createIconButton("src/main/images/vacate.png", "Vacate Parking Spot");
        vacateParkingSpotButton.addActionListener(e -> vacateParkingSpot());

        // Back to Main Page Icon
        JButton backButton = createIconButton("src/main/images/back to main.png", "Back to Main Page");
        backButton.addActionListener(e -> setupGuestMainPage());

        buttonsPanel.add(occupyParkingSpotButton);
        buttonsPanel.add(vacateParkingSpotButton);
        buttonsPanel.add(backButton);

        // Create a panel to hold the background image and buttons panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(normalParkingBackgroundLabel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);
        JButton normalParkingInfoButton = createInfoButton("src/main/images/info_icon.png", "Normal Parking Instructions",
                "You can occupy or vacate parking spots manually here.");
        buttonsPanel.add(normalParkingInfoButton, BorderLayout.WEST);

        window.getContentPane().removeAll();
        window.getContentPane().add(panel);
        window.revalidate();
        window.repaint();
    }

    private void setupAutomaticParkingSubPage() {
        // Load background image for automatic parking subpage
        ImageIcon automaticParkingBackground = new ImageIcon(getClass().getResource("images/velvetparking.png"));
        JLabel automaticParkingBackgroundLabel = new JLabel(automaticParkingBackground);

        // Create a panel to hold the buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1));
        buttonsPanel.setOpaque(false); // Make panel transparent

        // Automatic Vehicle Storage Icon
        JButton automaticVehicleStorageButton = createIconButton("src/main/images/autooccupy.png", "Automatic Vehicle Storage");
        automaticVehicleStorageButton.addActionListener(e -> automatedVehicleStorage());

        // Automatic Vehicle Withdrawal Icon
        JButton automaticVehicleWithdrawalButton = createIconButton("src/main/images/autovacate.jpg", "Automatic Vehicle Withdrawal");
        automaticVehicleWithdrawalButton.addActionListener(e -> automaticVehicleWithdrawal());

        // Back to Main Page Icon
        JButton backButton = createIconButton("src/main/images/back to main.png", "Back to Main Page");
        backButton.addActionListener(e -> setupGuestMainPage());

        buttonsPanel.add(automaticVehicleStorageButton);
        buttonsPanel.add(automaticVehicleWithdrawalButton);
        buttonsPanel.add(backButton);

        // Create a panel to hold the background image and buttons panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(automaticParkingBackgroundLabel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);
        JButton automaticParkingInfoButton = createInfoButton("src/main/images/info_icon.png", "Automatic Parking Instructions",
                "You can perform automated valet parking and withdrawal operations here.");
        buttonsPanel.add(automaticParkingInfoButton, BorderLayout.WEST);

        window.getContentPane().removeAll();
        window.getContentPane().add(panel);
        window.revalidate();
        window.repaint();
    }

    private void handleMenuChoice(MenuChoice choice) {
        switch (choice) {
            case CHECK_AVAILABILITY:
                checkParkingAvailability();
                break;
            case OCCUPY_PARKING_SPOT:
                occupyParkingSpot();
                break;
            case VACATE_PARKING_SPOT:
                vacateParkingSpot();
                break;
            case FIND_NEAREST_VACANT_SPOT:
                findNearestVacantSpot();
                break;
            case AUTOMATED_VEHICLE_STORAGE:
                automatedVehicleStorage();
                break;
            case AUTOMATIC_VEHICLE_WITHDRAWAL:
                automaticVehicleWithdrawal();
                break;
            case RESERVATION:
                reservation();
                break;
            case DISPLAY_PARKING_STATUS:
                displayParkingStatus();
                break;
            case EXIT:
                window.dispose();
                break;
        }
    }

    private void checkParkingAvailability() {
        int result = JOptionPane.showConfirmDialog(window, "Is the area designated for parking?", "Designated Parking", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            totalRows = Integer.parseInt(JOptionPane.showInputDialog(window, "Enter the number of rows:"));
            totalColumns = Integer.parseInt(JOptionPane.showInputDialog(window, "Enter the number of columns:"));

            parkingSpots = new int[totalRows][totalColumns];
            reservedSpots = new String[totalRows][totalColumns];
            vehicleNumbers = new String[totalRows * totalColumns];

            JOptionPane.showMessageDialog(window, "Number of available parking spots: " + getAvailableParkingSpots());
        } else {
            JOptionPane.showMessageDialog(window, "This area is not designated for parking.");
        }
    }

    private int getAvailableParkingSpots() {
        int count = 0;
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (parkingSpots[i][j] == 0 && reservedSpots[i][j] == null) {
                    count++;
                }
            }
        }
        return count;
    }

    private void occupyParkingSpot() {
        int result = JOptionPane.showConfirmDialog(window, "Do you have a reserved spot?", "Reserved Spot", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            String mobileNumber = JOptionPane.showInputDialog(window, "Enter your mobile number:");
            String password = JOptionPane.showInputDialog(window, "Enter your password:");
            if (mobileNumber != null && validateMobileAndPassword(mobileNumber, password)) {
                String reservedSpotsInfo = displayReservedParkingSpots();
                String spotChoice = JOptionPane.showInputDialog(window, "Reserved Parking Spots:\n" + reservedSpotsInfo + "\nEnter the spot index (row and column):");
                String[] parts = spotChoice.split("\\s+");
                int row = Integer.parseInt(parts[0]);
                int column = Integer.parseInt(parts[1]);
                if (row >= 0 && row < totalRows && column >= 0 && column < totalColumns) {
                    String vehicleNumber = reservedSpots[row][column];
                    if (vehicleNumber != null && vehicleNumber.equals(vehicleNumber) && parkingSpots[row][column] == 0) {
                        performParkingActions(vehicleNumber, row, column);
                    } else {
                        JOptionPane.showMessageDialog(window, "Invalid mobile number or spot is already occupied. Parking failed.");
                    }
                } else {
                    JOptionPane.showMessageDialog(window, "Invalid spot index. Parking failed.");
                }
            } else {
                JOptionPane.showMessageDialog(window, "Invalid mobile number or password. Parking failed.");
            }
        } else {
            occupyAvailableSpot();
        }
    }

    private boolean validateMobileAndPassword(String mobileNumber, String password) {
        // Simple validation: Check if the mobile number is a 10-digit number
        return mobileNumber.matches("\\d{10}") && password != null && !password.isEmpty();
    }

    private void occupyAvailableSpot() {
        if (getAvailableParkingSpots() > 0) {
            String availableSpotsInfo = displayAvailableParkingSpots();
            String spotChoice = JOptionPane.showInputDialog(window, "Available Parking Spots:\n" + availableSpotsInfo + "\nChoose a spot to park (enter row and column separated by space):");
            String[] parts = spotChoice.split("\\s+");
            int row = Integer.parseInt(parts[0]);
            int column = Integer.parseInt(parts[1]);
            if (row >= 0 && row < totalRows && column >= 0 && column < totalColumns && parkingSpots[row][column] == 0 && reservedSpots[row][column] == null) {
                String vehicleNumber = JOptionPane.showInputDialog(window, "Enter your vehicle number:");
                performParkingActions(vehicleNumber, row, column);
            } else {
                JOptionPane.showMessageDialog(window, "Invalid spot. Please choose a valid and unoccupied spot.");
            }
        } else {
            JOptionPane.showMessageDialog(window, "No available parking spots. Parking is full.");
        }
    }

    private String displayReservedParkingSpots() {
        StringBuilder reservedSpotsInfo = new StringBuilder();
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (reservedSpots[i][j] != null) {
                    reservedSpotsInfo.append("Spot (").append(i).append(", ").append(j).append(") - Reserved for ").append(reservedSpots[i][j]).append("\n");
                }
            }
        }
        return reservedSpotsInfo.toString();
    }

    private String displayAvailableParkingSpots() {
        StringBuilder availableSpotsInfo = new StringBuilder();
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (parkingSpots[i][j] == 0 && reservedSpots[i][j] == null) {
                    availableSpotsInfo.append("Spot (").append(i).append(", ").append(j).append(") - Vacant\n");
                }
            }
        }
        return availableSpotsInfo.toString();
    }

    private void performParkingActions(String vehicleNumber, int row, int column) {
        JOptionPane.showMessageDialog(window, "Vehicle parked successfully in spot (" + row + ", " + column + ")");
        parkingSpots[row][column] = 1;
        vehicleNumbers[row * totalColumns + column] = vehicleNumber;
        entryTimes.put(vehicleNumber, LocalDateTime.now());
        reservationTimes.put(vehicleNumber, null);
    }

    private boolean findReservedSpot(String mobileNumber, int row, int column) {
        return reservedSpots[row][column] != null && reservedSpots[row][column].equals(mobileNumber);
    }

    private void vacateParkingSpot() {
        String vehicleNumber = JOptionPane.showInputDialog(window, "Enter your vehicle number:");
        int spotIndex = findVehicleInParkingSpots(vehicleNumber);
        if (spotIndex != -1) {
            int row = spotIndex / totalColumns;
            int column = spotIndex % totalColumns;
            LocalDateTime entryTime = entryTimes.get(vehicleNumber);
            LocalDateTime exitTime = LocalDateTime.now();
            long duration = Duration.between(entryTime, exitTime).toMinutes();
            double parkingFee = calculateParkingFee(duration);

            // Generate QR code content
            String qrContent = "Vehicle Number: " + vehicleNumber + "\n" +
                    "Spot Vacated: (" + row + ", " + column + ")\n" +
                    "Occupying Time: " + entryTime.toString() + "\n" +
                    "Vacating Time: " + exitTime.toString() + "\n" +
                    "Total Time Parked: " + duration + " minutes\n" +
                    "Parking Fee: $" + parkingFee;

            // Generate QR code
            generateQRCode(qrContent);

            // Remove vehicle from parking spot
            JOptionPane.showMessageDialog(window, "Vehicle vacated from spot (" + row + ", " + column + ")");
            parkingSpots[row][column] = 0;
            reservedSpots[row][column] = null;
            JOptionPane.showMessageDialog(window, "Parking fee: $" + parkingFee);
            reservationTimes.put(vehicleNumber, null);
        } else {
            JOptionPane.showMessageDialog(window, "Vehicle not found in the parking spots.");
        }
    }



    // Generate QR Code
    private void generateQRCode(String content) {
        int size = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage qrImage = toBufferedImage(bitMatrix);
            ImageIcon qrCodeIcon = new ImageIcon(qrImage);
            JOptionPane.showMessageDialog(window, qrCodeIcon, "QR Code", JOptionPane.PLAIN_MESSAGE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }

    private int findVehicleInParkingSpots(String vehicleNumber) {
        for (int i = 0; i < vehicleNumbers.length; i++) {
            if (vehicleNumbers[i] != null && vehicleNumbers[i].equals(vehicleNumber)) {
                return i;
            }
        }
        return -1;
    }

    private double calculateParkingFee(long durationInMinutes) {
        double ratePerMinute = 1.0 / 30;
        return Math.round(durationInMinutes * ratePerMinute * 100) / 100.0;
    }

    private void findNearestVacantSpot() {
        String userLocation = JOptionPane.showInputDialog(window, "Are you inside or outside the parking system? (inside/outside):");
        if ("inside".equals(userLocation)) {
            findNearestVacantSpotInside();
        } else if ("outside".equals(userLocation)) {
            findNearestVacantSpotOutside();
        } else {
            JOptionPane.showMessageDialog(window, "Invalid input. Please enter 'inside' or 'outside'.");
        }
    }

    private void findNearestVacantSpotInside() {
        if (getAvailableParkingSpots() > 0) {
            String userLocation = JOptionPane.showInputDialog(window, "Enter your current location (row and column separated by space):");
            String[] parts = userLocation.split("\\s+");
            int userRow = Integer.parseInt(parts[0]);
            int userColumn = Integer.parseInt(parts[1]);
            if (userRow >= 0 && userRow < totalRows && userColumn >= 0 && userColumn < totalColumns) {
                int[] nearestSpot = findNearestSpot(userRow, userColumn);
                JOptionPane.showMessageDialog(window, "Nearest vacant spot is at (" + nearestSpot[0] + ", " + nearestSpot[1] + ")");
            } else {
                JOptionPane.showMessageDialog(window, "Invalid user location.");
            }
        } else {
            JOptionPane.showMessageDialog(window, "No available parking spots. Parking is full.");
        }
    }

    private void findNearestVacantSpotOutside() {
        if (getAvailableParkingSpots() > 0) {
            int entranceRow = 0;
            int[] nearestSpot = findNearestSpot(entranceRow, 0);
            JOptionPane.showMessageDialog(window, "Nearest vacant spot from the entrance is at (" + nearestSpot[0] + ", " + nearestSpot[1] + ")");
        } else {
            JOptionPane.showMessageDialog(window, "No available parking spots. Parking is full.");
        }
    }

    private int[] findNearestSpot(int userRow, int userColumn) {
        int[] nearestSpot = {-1, -1};
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (parkingSpots[i][j] == 0 && reservedSpots[i][j] == null) {
                    double distance = calculateDistance(userRow, userColumn, i, j);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestSpot[0] = i;
                        nearestSpot[1] = j;
                    }
                }
            }
        }
        return nearestSpot;
    }

    private double calculateDistance(int userRow, int userColumn, int spotRow, int spotColumn) {
        return Math.sqrt(Math.pow(userRow - spotRow, 2) + Math.pow(userColumn - spotColumn, 2));
    }

    private void automatedVehicleStorage() {
        int result = JOptionPane.showConfirmDialog(window, "Do you have a reserved parking spot?", "Reserved Spot", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            String mobileNumber = JOptionPane.showInputDialog(window, "Enter your mobile number:");
            String password = JOptionPane.showInputDialog(window, "Enter your password:");
            String vehicleNumber = JOptionPane.showInputDialog(window, "Enter your vehicle number:");

            if (mobileNumber != null && validateMobileAndPassword(mobileNumber, password)) {
                String reservedSpotsInfo = displayReservedParkingSpots();
                String spotChoice = JOptionPane.showInputDialog(window, "Reserved Parking Spots:\n" + reservedSpotsInfo + "\nEnter the spot index (row and column):");
                String[] parts = spotChoice.split("\\s+");
                int row = Integer.parseInt(parts[0]);
                int column = Integer.parseInt(parts[1]);
                if (row >= 0 && row < totalRows && column >= 0 && column < totalColumns) {
                    vehicleNumber = reservedSpots[row][column];
                    if (vehicleNumber != null && vehicleNumber.equals(vehicleNumber) && parkingSpots[row][column] == 0) {
                        performParkingActions(vehicleNumber, row, column);
                    } else {
                        JOptionPane.showMessageDialog(window, "Invalid mobile number or spot is already occupied. Parking failed.");
                    }
                } else {
                    JOptionPane.showMessageDialog(window, "Invalid spot index. Parking failed.");
                }
            } else {
                JOptionPane.showMessageDialog(window, "Invalid mobile number or password. Parking failed.");
            }
        } else {
            automatedVehicleStorageWithoutReservation();
        }
    }
    private int[] findReservedSpotIndex(String mobileNumber, String password) {
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (reservedSpots[i][j] != null && reservedSpots[i][j].equals(mobileNumber) && validateMobileAndPassword(mobileNumber, password)) {
                    return new int[]{i, j}; // Return the reserved spot index if found
                }
            }
        }
        return null; // Return null if the reserved spot is not found for the provided credentials
    }



    private void automatedVehicleStorageWithoutReservation() {
        int emptyStorageSpot = findEmptyStorageSpot();
        if (emptyStorageSpot != -1 && parkingSpots[emptyStorageSpot / totalColumns][emptyStorageSpot % totalColumns] == 0) {
            String vehicleNumber = JOptionPane.showInputDialog(window, "Enter your vehicle number:");
            performParkingActions(vehicleNumber, emptyStorageSpot / totalColumns, emptyStorageSpot % totalColumns);
        } else if (emptyStorageSpot == -1) {
            JOptionPane.showMessageDialog(window, "No empty storage spots available. Storage is full.");
        } else {
            JOptionPane.showMessageDialog(window, "Invalid storage spot. Storage failed.");
        }
    }

    private int findEmptyStorageSpot() {
        for (int i = 0; i < totalRows * totalColumns; i++) {
            if (vehicleNumbers[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private void automaticVehicleWithdrawal() {
        String vehicleNumber = JOptionPane.showInputDialog(window, "Enter your vehicle number:");
        int spotIndex = findVehicleInParkingSpots(vehicleNumber);
        if (spotIndex != -1) {
            int row = spotIndex / totalColumns;
            int column = spotIndex % totalColumns;
            JOptionPane.showMessageDialog(window, "Vehicle withdrawn successfully from spot (" + row + ", " + column + ")");
            parkingSpots[row][column] = 0;
            vehicleNumbers[spotIndex] = null;
            entryTimes.put(vehicleNumber, null);
        } else {
            JOptionPane.showMessageDialog(window, "Vehicle not found in the parking spots.");
        }
    }

    private void reservation() {
        String mobileNumber = JOptionPane.showInputDialog(window, "Enter your mobile number:");
        String password = JOptionPane.showInputDialog(window, "Enter your password:");
        if (mobileNumber != null && validateMobileAndPassword(mobileNumber, password)) {
            String vehicleNumber = JOptionPane.showInputDialog(window, "Enter your vehicle number:"); // Prompt for vehicle number
            if (vehicleNumber != null) {
                if (getAvailableParkingSpots() > 0) {
                    String availableSpotsInfo = displayAvailableParkingSpots();
                    String spotChoice = JOptionPane.showInputDialog(window, "Available Parking Spots:\n" + availableSpotsInfo + "\nChoose a spot to reserve (enter row and column separated by space):");
                    String[] parts = spotChoice.split("\\s+");
                    int row = Integer.parseInt(parts[0]);
                    int column = Integer.parseInt(parts[1]);
                    if (row >= 0 && row < totalRows && column >= 0 && column < totalColumns && parkingSpots[row][column] == 0 && reservedSpots[row][column] == null) {
                        reservedSpots[row][column] = vehicleNumber; // Associate reservation with vehicle number
                        LocalDateTime reservationTime = LocalDateTime.now();
                        reservationTimes.put(vehicleNumber, reservationTime); // Use vehicle number as key

                        // Generate QR code
                        String qrCodeData = "Mobile Number: " + mobileNumber + "\nVehicle Number: " + vehicleNumber + "\nReserved Spot: (" + row + ", " + column + ")\nReserved Time: " + reservationTime + "\nValid Till: " + reservationTime.plusMinutes(RESERVATION_TIME_LIMIT_MINUTES);
                        BufferedImage qrCodeImage = generateQRCodeImage(qrCodeData);
                        if (qrCodeImage != null) {
                            JLabel qrCodeLabel = new JLabel(new ImageIcon(qrCodeImage));
                            JPanel panel = new JPanel(new BorderLayout());
                            panel.add(new JLabel("Spot (" + row + ", " + column + ") reserved successfully for " + RESERVATION_TIME_LIMIT_MINUTES + " minutes."), BorderLayout.NORTH);
                            panel.add(qrCodeLabel, BorderLayout.CENTER);
                            JOptionPane.showMessageDialog(window, panel, "Reservation Successful", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(window, "Failed to generate QR code.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(window, "Invalid spot or spot is already reserved. Reservation failed.");
                    }
                } else {
                    JOptionPane.showMessageDialog(window, "No available parking spots. Parking is full.");
                }
            } else {
                JOptionPane.showMessageDialog(window, "Invalid vehicle number. Reservation failed.");
            }
        } else {
            JOptionPane.showMessageDialog(window, "Invalid mobile number or password. Reservation failed.");
        }
    }




    private BufferedImage generateQRCodeImage(String qrCodeData) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return qrImage;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void generateAnalyticsReport() {
        int totalVehiclesParked = 0;
        double totalFeeCollected = 0;
        double totalDuration = 0;

        for (Map.Entry<String, LocalDateTime> entry : entryTimes.entrySet()) {
            String vehicleNumber = entry.getKey();
            LocalDateTime entryTime = entry.getValue();

            if (entryTime != null) {
                totalVehiclesParked++;
                LocalDateTime exitTime = LocalDateTime.now();
                long duration = Duration.between(entryTime, exitTime).toMinutes();
                double parkingFee = calculateParkingFee(duration);
                totalFeeCollected += parkingFee;
                totalDuration += duration;
            }
        }

        double averageDuration = totalDuration / totalVehiclesParked;

        StringBuilder report = new StringBuilder();
        report.append("Analytics Report:\n");
        report.append("Total Vehicles Parked: ").append(totalVehiclesParked).append("\n");
        report.append("Total Parking Fee Collected: $").append(String.format("%.2f", totalFeeCollected)).append("\n");
        report.append("Average Parking Duration: ").append(String.format("%.2f", averageDuration)).append(" minutes\n");

        JOptionPane.showMessageDialog(window, report.toString());
    }

    private void displayParkingStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Parking Status:\n");
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                status.append("(").append(i).append(",").append(j).append("): ");
                if (parkingSpots[i][j] == 1) {
                    status.append("Occupied by Vehicle ").append(vehicleNumbers[i * totalColumns + j]).append("\n");
                } else if (reservedSpots[i][j] != null) {
                    status.append("Reserved for ").append(reservedSpots[i][j]).append("\n");
                } else {
                    status.append("Vacant\n");
                }
            }
        }
        JOptionPane.showMessageDialog(window, status.toString());
        generateAnalyticsReport();
    }


    public static void main(String[] args) {
        
        new SmartParkingSystem();
    }
}

