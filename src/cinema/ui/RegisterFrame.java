package cinema.ui;

import cinema.model.people.Customer;
import cinema.service.AuthService;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class RegisterFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;
    private JPanel contentPane;
    private JTextField txtFirstName, txtLastName, txtEmail, txtBirthDate;
    private JPasswordField txtPassword;
    private int mouseX, mouseY;

    // LoginFrame ile birebir aynı Renk Paleti
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_ACCENT = new Color(229, 9, 20); // Netflix Kırmızısı
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_INPUT_BORDER = Color.GRAY;

    public RegisterFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;

        setUndecorated(true); // Üst barı kaldırır
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 650);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        initHeader();
        initTitle();
        initForm();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBounds(0, 0, 450, 40);
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);

        // Pencere Sürükleme
        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        // Kapatma Butonu
        JLabel lblClose = new JLabel("X");
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.setForeground(Color.WHITE);
        lblClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClose.setBounds(415, 0, 35, 40);
        lblClose.setHorizontalAlignment(SwingConstants.CENTER);
        lblClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(Color.WHITE); }
        });
        headerPanel.add(lblClose);
    }

    private void initTitle() {
        JLabel lblTitle = new JLabel("HESAP OLUŞTUR");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setBounds(0, 50, 450, 40);
        contentPane.add(lblTitle);
    }

    private void initForm() {
        int startY = 110;
        int spacing = 70;

        txtFirstName = addLabeledField("Ad", startY);
        txtLastName = addLabeledField("Soyad", startY + spacing);
        txtEmail = addLabeledField("E-posta", startY + (spacing * 2));
        txtBirthDate = addLabeledField("Doğum Tarihi (YYYY-MM-DD)", startY + (spacing * 3));

        // Şifre Alanı
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setForeground(COLOR_TEXT);
        lblPass.setBounds(75, startY + (spacing * 4), 300, 20);
        contentPane.add(lblPass);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setBounds(75, startY + (spacing * 4) + 25, 300, 35);
        contentPane.add(txtPassword);

        // Kayıt Butonu
        JButton btnRegister = new JButton("KAYIT OL");
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setBounds(75, 520, 300, 45);
        btnRegister.setBackground(COLOR_ACCENT);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.addActionListener(this::handleRegister);
        contentPane.add(btnRegister);

        // Geri Dön Linki
        JLabel lblLogin = new JLabel("Zaten bir hesabın var mı? Giriş Yap");
        lblLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogin.setForeground(Color.GRAY);
        lblLogin.setBounds(0, 580, 450, 30);
        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new LoginFrame(authService, ticketService).setVisible(true);
                dispose();
            }
            public void mouseEntered(MouseEvent e) { lblLogin.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e) { lblLogin.setForeground(Color.GRAY); }
        });
        contentPane.add(lblLogin);
    }

    private JTextField addLabeledField(String labelText, int yPos) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(COLOR_TEXT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setBounds(75, yPos, 300, 20);
        contentPane.add(lbl);

        JTextField field = new JTextField();
        styleTextField(field);
        field.setBounds(75, yPos + 25, 300, 35);
        contentPane.add(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(COLOR_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(COLOR_ACCENT);
        field.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_INPUT_BORDER));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_ACCENT)); }
            public void focusLost(FocusEvent e) { field.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_INPUT_BORDER)); }
        });
    }

    private void handleRegister(ActionEvent e) {
        try {
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            String birthDateStr = txtBirthDate.getText();

            // 1. Temel Validation
            if (firstName.isBlank() || email.isBlank() || password.isBlank() || birthDateStr.isBlank()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Nesne Hazırlama
            Customer newCustomer = new Customer(firstName, lastName, email, LocalDate.parse(birthDateStr), password);
            newCustomer.setId(UUID.randomUUID().toString());

            // 3. Service Üzerinden Kayıt (UI -> AuthService -> UserRepository)
            authService.register(newCustomer);

            JOptionPane.showMessageDialog(this, "Kaydınız başarıyla tamamlandı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame(authService, ticketService).setVisible(true);
            this.dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Tarih formatı YYYY-MM-DD olmalıdır!", "Format Hatası", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kayıt Hatası", JOptionPane.ERROR_MESSAGE);
        }
    }
}