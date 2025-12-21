package cinema.ui;

import cinema.model.people.Customer;
import cinema.service.AuthService;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
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

    // Premium Renk Paleti
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    public RegisterFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 720); // Boyut biraz optimize edildi
        setLocationRelativeTo(null);
        // Oval Köşeler
        setShape(new RoundRectangle2D.Double(0, 0, 480, 720, 30, 30));

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        initHeader();
        initTitle();
        initForm();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBounds(0, 0, 480, 50);
        contentPane.add(headerPanel);

        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        // Minimize Butonu
        JButton btnMin = new JButton("_");
        btnMin.setBounds(390, 10, 35, 35);
        btnMin.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        headerPanel.add(btnMin);

        // Kapatma Butonu
        JButton btnClose = new JButton("X");
        btnClose.setBounds(430, 10, 35, 35);
        btnClose.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> System.exit(0));
        headerPanel.add(btnClose);
    }

    private void initTitle() {
        JLabel lblLogo = new JLabel("SİNEMA");
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setForeground(COLOR_ACCENT);
        lblLogo.setFont(new Font("Segoe UI Black", Font.BOLD, 32));
        lblLogo.setBounds(0, 60, 480, 40);
        contentPane.add(lblLogo);

        JLabel lblTitle = new JLabel("YENİ HESAP OLUŞTUR");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(COLOR_TEXT_MAIN);
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblTitle.setBounds(0, 100, 480, 20);
        contentPane.add(lblTitle);
    }

    private void initForm() {
        int startY = 150;
        int spacing = 75;
        int fieldX = 75;
        int fieldW = 330;

        txtFirstName = addLabeledField("AD", startY, fieldX, fieldW);
        txtLastName = addLabeledField("SOYAD", startY + spacing, fieldX, fieldW);
        txtEmail = addLabeledField("E-POSTA ADRESİ", startY + (spacing * 2), fieldX, fieldW);
        txtBirthDate = addLabeledField("DOĞUM TARİHİ (YYYY-MM-DD)", startY + (spacing * 3), fieldX, fieldW);

        // Şifre Alanı
        JLabel lblPass = new JLabel("ŞİFRE");
        lblPass.setForeground(COLOR_ACCENT);
        lblPass.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lblPass.setBounds(fieldX, startY + (spacing * 4), fieldW, 20);
        contentPane.add(lblPass);

        txtPassword = new JPasswordField();
        styleInput(txtPassword);
        txtPassword.setBounds(fieldX, startY + (spacing * 4) + 22, fieldW, 40);
        contentPane.add(txtPassword);

        // Kayıt Butonu
        JButton btnRegister = new JButton("KAYIT OL");
        btnRegister.setBounds(fieldX, 580, fieldW, 48);
        btnRegister.setBackground(COLOR_ACCENT);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI Bold", Font.PLAIN, 15));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(this::handleRegister);
        contentPane.add(btnRegister);

        // Geri Dön Linki
        JLabel lblLogin = new JLabel("Zaten bir hesabın var mı? Giriş Yap");
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogin.setForeground(COLOR_TEXT_SUB);
        lblLogin.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        lblLogin.setBounds(0, 640, 480, 30);
        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new LoginFrame(authService, ticketService).setVisible(true);
                dispose();
            }
            public void mouseEntered(MouseEvent e) { lblLogin.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e) { lblLogin.setForeground(COLOR_TEXT_SUB); }
        });
        contentPane.add(lblLogin);
    }

    private JTextField addLabeledField(String labelText, int yPos, int x, int w) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(COLOR_ACCENT);
        lbl.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lbl.setBounds(x, yPos, w, 20);
        contentPane.add(lbl);

        JTextField field = new JTextField();
        styleInput(field);
        field.setBounds(x, yPos + 22, w, 40);
        contentPane.add(field);
        return field;
    }

    private void styleInput(JTextField field) {
        field.setBackground(COLOR_CARD);
        field.setForeground(Color.WHITE);
        field.setCaretColor(COLOR_ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.setBorder(new LineBorder(COLOR_ACCENT, 1, true)); }
            public void focusLost(FocusEvent e) { field.setBorder(new LineBorder(COLOR_BORDER, 1, true)); }
        });
    }

    private void styleControlBtn(JButton btn, Color hover) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setForeground(new Color(100, 100, 100));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(hover); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(100, 100, 100)); }
        });
    }

    private void handleRegister(ActionEvent e) {
        try {
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            String birthDateStr = txtBirthDate.getText();

            if (firstName.isBlank() || email.isBlank() || password.isBlank() || birthDateStr.isBlank()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer newCustomer = new Customer(firstName, lastName, email, LocalDate.parse(birthDateStr), password);
            newCustomer.setId(UUID.randomUUID().toString());
            authService.register(newCustomer);

            JOptionPane.showMessageDialog(this, "Kaydınız başarıyla tamamlandı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame(authService, ticketService).setVisible(true);
            this.dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Tarih formatı YYYY-MM-DD olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}