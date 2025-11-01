package network1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private JFrame loginFrame, selectionFrame, reservationFrame;
    private JTextField usernameField, roomNameField;
    private JPasswordField passwordField;
    private JComboBox<String> roomTypeBox, dateBox;
    private JList<String> availableRoomsList;
    private JButton joinButton, showButton, reserveButton, backButton, exitButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9090;

    private final Color sakuraPink = new Color(0xFDEEF4);
    private final Color darkBrown = new Color(0x4B2E2B);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    public Client() {
        setupLoginFrame();
    }

    //================= LOGIN FRAME =================
    private void setupLoginFrame() {
        loginFrame = new JFrame("Join - Sakura Hotel");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(900, 500);
        loginFrame.setLayout(new GridLayout(1, 2));
        loginFrame.setLocationRelativeTo(null);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(sakuraPink);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("<html><center><b>Join Sakura Hotel</b><br>さくらホテル</center></html>");
        title.setFont(new Font("Poppins", Font.BOLD, 24));
        title.setForeground(darkBrown);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userLabel.setForeground(darkBrown);
        passLabel.setForeground(darkBrown);
        userLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        passLabel.setFont(new Font("Poppins", Font.BOLD, 16));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        joinButton = createStyledButton("Join");
        exitButton = createStyledButton("Exit");

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        leftPanel.add(title, c);

        c.gridy = 1; c.gridwidth = 1;
        leftPanel.add(userLabel, c);
        c.gridx = 1; leftPanel.add(usernameField, c);

        c.gridy = 2; c.gridx = 0;
        leftPanel.add(passLabel, c);
        c.gridx = 1; leftPanel.add(passwordField, c);

        c.gridy = 3; c.gridx = 0;
        leftPanel.add(joinButton, c);
        c.gridx = 1; leftPanel.add(exitButton, c);

        joinButton.addActionListener(e -> connectAndLogin());
        exitButton.addActionListener(e -> System.exit(0));

        ImageIcon original = new ImageIcon(getClass().getResource("/network1/img/hotel1.jpeg"));
        Image scaled = original.getImage().getScaledInstance(400, 500, Image.SCALE_SMOOTH);
        JLabel rightImage = new JLabel(new ImageIcon(scaled));
        rightImage.setHorizontalAlignment(SwingConstants.CENTER);
        rightImage.setVerticalAlignment(SwingConstants.CENTER);
        rightImage.setPreferredSize(new Dimension(400, 500));
        rightImage.setOpaque(true);
        rightImage.setBackground(Color.WHITE);

        loginFrame.add(leftPanel);
        loginFrame.add(rightImage);
        loginFrame.setVisible(true);
    }

    //================= ROOM SELECTION FRAME =================
    private void setupSelectionFrame() {
        selectionFrame = new JFrame("Select Room - Sakura Hotel");
        selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectionFrame.setSize(900, 600);
        selectionFrame.setLayout(new GridLayout(1, 2));
        selectionFrame.setLocationRelativeTo(null);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(sakuraPink);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("<html><center><b>Select Your Room</b><br>お部屋をお選びください。</center></html>");
        title.setFont(new Font("Poppins", Font.BOLD, 26));
        title.setForeground(darkBrown);

        JLabel roomTypeLabel = new JLabel("Room Type:");
        JLabel dateLabel = new JLabel("Date:");
        roomTypeLabel.setForeground(darkBrown);
        dateLabel.setForeground(darkBrown);
        roomTypeLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        dateLabel.setFont(new Font("Poppins", Font.BOLD, 16));

        roomTypeBox = new JComboBox<>(new String[]{"standard", "premium", "suite"});
        roomTypeBox.setFont(new Font("Poppins", Font.PLAIN, 14));

        String[] availableDates = {
                "2025-10-16", "2025-10-17", "2025-10-18",
                "2025-10-19", "2025-10-20", "2025-10-21", "2025-10-22"
        };
        dateBox = new JComboBox<>(availableDates);
        dateBox.setFont(new Font("Poppins", Font.PLAIN, 14));

        showButton = createStyledButton("Show");
        backButton = createStyledButton("Back");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        leftPanel.add(title, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        leftPanel.add(roomTypeLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(roomTypeBox, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        leftPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(dateBox, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        leftPanel.add(showButton, gbc);

        gbc.gridy = 4; gbc.gridwidth = 2;
        leftPanel.add(backButton, gbc);

        showButton.addActionListener(e -> sendRoomSelection());
        backButton.addActionListener(e -> {
            closeConnection();
            selectionFrame.dispose();
            setupLoginFrame();
        });

        JLabel rightPanel = new JLabel();
        ImageIcon original = new ImageIcon(getClass().getResource("/network1/img/hotel2.png"));
        Image scaled = original.getImage().getScaledInstance(450, 600, Image.SCALE_SMOOTH);
        rightPanel.setIcon(new ImageIcon(scaled));
        rightPanel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.setVerticalAlignment(SwingConstants.CENTER);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setOpaque(true);

        selectionFrame.add(leftPanel);
        selectionFrame.add(rightPanel);
        selectionFrame.setVisible(true);
    }

    //================= RESERVATION FRAME =================
    private void setupReservationFrame(String availableList) {
        reservationFrame = new JFrame("Reserve Room - Sakura Hotel");
        reservationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        reservationFrame.setSize(900, 650);
        reservationFrame.setLocationRelativeTo(null);
        reservationFrame.setLayout(null);

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/network1/img/hotel4.png"));
        Image bgScaled = bgIcon.getImage().getScaledInstance(900, 650, Image.SCALE_SMOOTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(sakuraPink);
        contentPanel.setBounds(200, 120, 500, 380);
        contentPanel.setBorder(BorderFactory.createLineBorder(darkBrown, 3));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Available Rooms");
        title.setFont(new Font("Poppins", Font.BOLD, 20));
        title.setForeground(darkBrown);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String room : availableList.split("\\s+")) {
            if (!room.equalsIgnoreCase("null") && !room.isBlank()) listModel.addElement(room);
        }

        availableRoomsList = new JList<>(listModel);
        availableRoomsList.setFont(new Font("Poppins", Font.PLAIN, 15));
        availableRoomsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                roomNameField.setText(availableRoomsList.getSelectedValue());
            }
        });

        JScrollPane scrollPane = new JScrollPane(availableRoomsList);
        scrollPane.setPreferredSize(new Dimension(300, 100));

        JLabel roomLabel = new JLabel("Room Name:");
        roomLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        roomLabel.setForeground(darkBrown);

        roomNameField = new JTextField(20);
        roomNameField.setFont(new Font("Poppins", Font.PLAIN, 15));

        reserveButton = createStyledButton("Reserve");
        backButton = createStyledButton("Back");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(sakuraPink);
        buttonPanel.add(reserveButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(title, gbc);
        gbc.gridy = 1; contentPanel.add(scrollPane, gbc);
        gbc.gridy = 2; contentPanel.add(roomLabel, gbc);
        gbc.gridy = 3; contentPanel.add(roomNameField, gbc);
        gbc.gridy = 4; contentPanel.add(buttonPanel, gbc);

        reserveButton.addActionListener(e -> sendReservationRequest());
       backButton.addActionListener(e -> {
    try {
        // نغلق الاتصال الحالي فقط
        if (socket != null && !socket.isClosed()) socket.close();

        // نرجع للصفحة السابقة
        reservationFrame.dispose();

        // نبدأ اتصال جديد للسيرفر (بدون ما نرجع لصفحة اللوق إن)
        socket = new Socket(SERVER_IP, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // نطلب اسم المستخدم والباسورد من السيرفر مؤقتًا لتفعيل الجلسة من جديد
        in.readLine(); out.println(usernameField.getText());
        in.readLine(); out.println(new String(passwordField.getPassword()));
        in.readLine(); // LOGIN_SUCCESS or REGISTERED_SUCCESSFULLY

        setupSelectionFrame(); // ✅ يرجع صفحة الاختيار نظيفة بدون تعليق

    } catch (IOException ex) {
        JOptionPane.showMessageDialog(reservationFrame, "Connection reset error!");
    }
});


        reservationFrame.setContentPane(new JLabel(new ImageIcon(bgScaled)));
        reservationFrame.getContentPane().setLayout(null);
        reservationFrame.getContentPane().add(contentPanel);
        reservationFrame.setVisible(true);
    }

   //================= SERVER CONNECTION & LOGIN =================
    private void connectAndLogin() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println(in.readLine());
            out.println(usernameField.getText());
            System.out.println(in.readLine());
            out.println(new String(passwordField.getPassword()));

            String response = in.readLine();
            if ("WRONG_PASSWORD".equals(response)) {
                JOptionPane.showMessageDialog(loginFrame, "Wrong password!");
                socket.close();
                return;
            } else if ("REGISTERED_SUCCESSFULLY".equals(response)) {
                JOptionPane.showMessageDialog(loginFrame, "Registered successfully! Welcome to our hotel!");
            } else if ("LOGIN_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(loginFrame, "Login successful! Welcome back again!");
            }

            loginFrame.dispose();
            setupSelectionFrame();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(loginFrame, "Failed to connect to server!");
            ex.printStackTrace();
        }
    }

    //================= SEND ROOM SELECTION =================
    private void sendRoomSelection() {
        try {
            String roomType = (String) roomTypeBox.getSelectedItem();
            String date = (String) dateBox.getSelectedItem();

            String prompt1 = in.readLine();
            if (prompt1 != null && prompt1.contains("type")) out.println(roomType);

            String prompt2 = in.readLine();
            if (prompt2 != null && prompt2.contains("date")) out.println(date);

            in.readLine(); // AVAILABLE:
            String availableList = in.readLine();

            selectionFrame.dispose();
            setupReservationFrame(availableList);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(selectionFrame, "Error loading available rooms.");
        }
    }

    //================= SEND RESERVATION =================
    private void sendReservationRequest() {
        try {
            String roomName = roomNameField.getText().trim();
            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(reservationFrame, "Please enter a room name before reserving.");
                return;
            }

            in.readLine();
            out.println(roomName);

            String response = in.readLine();
            JOptionPane.showMessageDialog(reservationFrame, response);

            closeConnection(); // ✅ يغلق الاتصال بعد الحجز
            reservationFrame.dispose();
            setupLoginFrame();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(reservationFrame, "Error sending reservation request.");
        }
    }

    //================= SAFE CLOSE =================
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    //================= STYLED BUTTON =================
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(darkBrown);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }
}
