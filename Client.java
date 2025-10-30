package network1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Client {
    private JFrame loginFrame, selectionFrame, reservationFrame;
    private JTextField usernameField, dateField, roomNameField;
    private JPasswordField passwordField;
    private JComboBox<String> roomTypeBox;
    private JList<String> availableRoomsList;
    private JButton loginButton, showButton, reserveButton, backButton, exitButton;
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
    loginFrame = new JFrame("Sakura Hotel - Login");
    loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    loginFrame.setSize(700, 500);
    loginFrame.setLocationRelativeTo(null);
    loginFrame.setLayout(new GridLayout(1, 2)); // قسمين: يسار + يمين

    // ================== القسم الأيسر (نموذج الدخول) ==================
    JPanel leftPanel = new JPanel(new GridBagLayout());
    leftPanel.setBackground(sakuraPink);
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 10, 10, 10);
    c.fill = GridBagConstraints.HORIZONTAL;

    JLabel title = new JLabel("<html><center><b>Welcome to Sakura Hotel!<br>ようこそサクラホテルへ!</b></center></html>");
    title.setForeground(darkBrown);
    title.setFont(new Font("Poppins", Font.BOLD, 24));

    usernameField = new JTextField(14);
    passwordField = new JPasswordField(14);

    JLabel userLabel = new JLabel("Username:");
    JLabel passLabel = new JLabel("Password:");
    userLabel.setForeground(darkBrown);
    passLabel.setForeground(darkBrown);
    userLabel.setFont(new Font("Poppins", Font.BOLD, 14));
    passLabel.setFont(new Font("Poppins", Font.BOLD, 14));

    loginButton = createStyledButton("Join");
    exitButton = createStyledButton("Exit");

    // ترتيب المكونات
    c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
    leftPanel.add(title, c);

    c.gridwidth = 1;
    c.gridy = 1; c.gridx = 0; leftPanel.add(userLabel, c);
    c.gridx = 1; leftPanel.add(usernameField, c);

    c.gridy = 2; c.gridx = 0; leftPanel.add(passLabel, c);
    c.gridx = 1; leftPanel.add(passwordField, c);

    c.gridy = 3; c.gridx = 0; leftPanel.add(loginButton, c);
    c.gridx = 1; leftPanel.add(exitButton, c);

    loginButton.addActionListener(e -> connectAndLogin());
    exitButton.addActionListener(e -> System.exit(0));

    // ================== القسم الأيمن (صورة ساكورا) ==================
    

// ================== القسم الأيمن (صورة ساكورا) ==================
ImageIcon original = new ImageIcon(getClass().getResource("/network1/img/hotel1.jpeg"));

// احسبي الارتفاع الحالي للفريم (تقريباً 500) وخلّي العرض متناسب
Image scaled = original.getImage().getScaledInstance(400, 500, Image.SCALE_SMOOTH);

JLabel rightImage = new JLabel(new ImageIcon(scaled));
rightImage.setHorizontalAlignment(SwingConstants.CENTER);
rightImage.setVerticalAlignment(SwingConstants.CENTER);

// خلي الصورة تاخذ العمود الأيمن كامل بدون تمديد زيادة
rightImage.setPreferredSize(new Dimension(400, 500));
rightImage.setOpaque(true);
rightImage.setBackground(Color.WHITE);

    // ================== دمج القسمين ==================
    loginFrame.add(leftPanel);
    loginFrame.add(rightImage);

    loginFrame.setVisible(true);
}

//================= ROOM SELECTION FRAME =================
private void setupSelectionFrame() {
    selectionFrame = new JFrame("Select Room - Sakura Hotel");
    selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    selectionFrame.setSize(900, 600);
    selectionFrame.setLayout(new GridLayout(1, 2)); // قسمين: يسار + يمين
    selectionFrame.setLocationRelativeTo(null);

    // 🎀 ألوان ساكورا
    Color sakuraPink = new Color(0xFDEEF4);
    Color darkBrown = new Color(0x4B2E2B);

    // ================== القسم الأيسر (الاختيار) ==================
    JPanel leftPanel = new JPanel(new GridBagLayout());
    leftPanel.setBackground(sakuraPink);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel title = new JLabel("<html><center><b>Select Your Room</b><br>お部屋をお選びください。</center></html>");
    title.setFont(new Font("Poppins", Font.BOLD, 26));
    title.setForeground(darkBrown);

    JLabel roomTypeLabel = new JLabel("Room Type:");
    JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
    roomTypeLabel.setForeground(darkBrown);
    dateLabel.setForeground(darkBrown);
    roomTypeLabel.setFont(new Font("Poppins", Font.BOLD, 16));
    dateLabel.setFont(new Font("Poppins", Font.BOLD, 16));

    roomTypeBox = new JComboBox<>(new String[]{"standard", "premium", "suite"});
    roomTypeBox.setFont(new Font("Poppins", Font.PLAIN, 14));

    dateField = new JTextField(14);
    dateField.setFont(new Font("Poppins", Font.PLAIN, 14));

    showButton = createStyledButton("Show Rooms");
    backButton = createStyledButton("Back");

    // ترتيب المكونات
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    leftPanel.add(title, gbc);

    gbc.gridy = 1; gbc.gridwidth = 1;
    leftPanel.add(roomTypeLabel, gbc);
    gbc.gridx = 1;
    leftPanel.add(roomTypeBox, gbc);

    gbc.gridy = 2; gbc.gridx = 0;
    leftPanel.add(dateLabel, gbc);
    gbc.gridx = 1;
    leftPanel.add(dateField, gbc);

    gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
    leftPanel.add(showButton, gbc);

    gbc.gridy = 4; gbc.gridwidth = 2;
    leftPanel.add(backButton, gbc);

    // الأزرار
    showButton.addActionListener(e -> validateDateAndShow());
    backButton.addActionListener(e -> {
        selectionFrame.dispose();
        setupLoginFrame();
    });

    // ================== القسم الأيمن (صورة البنت) ==================
    JLabel rightPanel = new JLabel();
    ImageIcon original = new ImageIcon(getClass().getResource("/network1/img/hotel2.png"));
    Image scaled = original.getImage().getScaledInstance(450, 600, Image.SCALE_SMOOTH);
    rightPanel.setIcon(new ImageIcon(scaled));
    rightPanel.setHorizontalAlignment(SwingConstants.CENTER);
    rightPanel.setVerticalAlignment(SwingConstants.CENTER);
    rightPanel.setBackground(Color.WHITE);
    rightPanel.setOpaque(true);

    // ================== دمج القسمين ==================
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

    // 🎀 ألوان ساكورا
    Color sakuraPink = new Color(0xFDEEF4);
    Color darkBrown = new Color(0x4B2E2B);

    // ================== الخلفية (الصورة) ==================
    ImageIcon bgIcon = new ImageIcon(getClass().getResource("/network1/img/hotel4.png"));
    Image bgScaled = bgIcon.getImage().getScaledInstance(900, 650, Image.SCALE_SMOOTH);
    JLabel background = new JLabel(new ImageIcon(bgScaled));
    background.setBounds(0, 0, 900, 650);

    // ================== المربع الوردي (panel) ==================
    JPanel contentPanel = new JPanel(new GridBagLayout());
    contentPanel.setBackground(sakuraPink);
    contentPanel.setBounds(200, 120, 500, 380);
    contentPanel.setBorder(BorderFactory.createLineBorder(darkBrown, 3));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.CENTER;

    JLabel title = new JLabel("Available Rooms");
    title.setFont(new Font("Poppins", Font.BOLD, 20));
    title.setForeground(darkBrown);

    // ===== قائمة الغرف =====
    DefaultListModel<String> listModel = new DefaultListModel<>();
    for (String room : availableList.split("\\s+")) {
        listModel.addElement(room);
    }

    availableRoomsList = new JList<>(listModel);
    availableRoomsList.setFont(new Font("Poppins", Font.PLAIN, 15));
    availableRoomsList.setVisibleRowCount(4);
    availableRoomsList.setFixedCellWidth(250);

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

    // ترتيب العناصر داخل البانل مثل الكود الأصلي
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    contentPanel.add(title, gbc);

    gbc.gridy = 1;
    contentPanel.add(scrollPane, gbc);

    gbc.gridy = 2;
    contentPanel.add(roomLabel, gbc);

    gbc.gridy = 3;
    contentPanel.add(roomNameField, gbc);

    gbc.gridy = 4;
    contentPanel.add(buttonPanel, gbc);

    // الأحداث
    reserveButton.addActionListener(e -> sendReservationRequest());
    backButton.addActionListener(e -> {
        reservationFrame.dispose();
        setupLoginFrame();
    });

    // إضافة الخلفية والبانل للفريم
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

    //================= DATE VALIDATION + SHOW ROOMS =================
    private void validateDateAndShow() {
        String dateInput = dateField.getText().trim();
        if (dateInput.isEmpty()) {
            JOptionPane.showMessageDialog(selectionFrame, "Please enter a date first.");
            return;
        }

        try {
            LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(selectionFrame, "Please enter a valid date (YYYY-MM-DD).");
            return;
        }

        sendRoomSelection();
    }

    //================= SEND ROOM SELECTION =================
    private void sendRoomSelection() {
        try {
            String roomType = (String) roomTypeBox.getSelectedItem();
            String date = dateField.getText();

            System.out.println(in.readLine());
            out.println(roomType);

            System.out.println(in.readLine());
            out.println(date);

            String header = in.readLine();
            String availableList = in.readLine();

            selectionFrame.dispose();
            setupReservationFrame(availableList);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(selectionFrame, "Error loading available rooms.");
            e.printStackTrace();
        }
    }

    //================= RESERVATION =================
    private void sendReservationRequest() {
        try {
            String roomName = roomNameField.getText().trim();
            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(reservationFrame, "Please enter a room name before reserving.");
                return;
            }

            System.out.println(in.readLine());
            out.println(roomName);

            String result = in.readLine();
            JOptionPane.showMessageDialog(reservationFrame, result);

            String finalMessage = in.readLine();
            if (finalMessage != null) System.out.println(finalMessage);

            if (result.startsWith("OK,ReservationDone")) {
                int choice = JOptionPane.showConfirmDialog(
                        reservationFrame,
                        "Reservation successful! Would you like to make another booking?",
                        "Booking Complete",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    reservationFrame.dispose();
                    setupSelectionFrame();
                } else {
                    JOptionPane.showMessageDialog(reservationFrame, "Thank you for choosing Sakura Hotel!");
                    System.exit(0);
                }
            }

            socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(reservationFrame, "Reservation failed.");
            e.printStackTrace();
        }
    }

    //================= BUTTON STYLE HELPER =================
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(darkBrown);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
