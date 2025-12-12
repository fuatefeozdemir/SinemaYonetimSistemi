package cinema.ui;

import cinema.service.AuthService;
// Eğer User entity'niz varsa buraya import etmelisiniz, örn: import cinema.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private final AuthService authService;

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private int mouseX, mouseY;

    // Renk Paleti
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_ACCENT = new Color(229, 9, 20); // Netflix Kırmızısı
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_INPUT_BORDER = Color.GRAY;

    // --- MAIN METODU (TEST İÇİN) ---
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // TEST İÇİN: Null yerine geçici bir anonim sınıf veya mock gönderebiliriz.
                // Şimdilik null bırakıyorum ama gerçek entegrasyonda burası ApplicationContext'ten gelecek.
                LoginFrame frame = new LoginFrame(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // --- CONSTRUCTOR ---
    public LoginFrame(AuthService authService) {
        this.authService = authService;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        initHeader();
        initTitle();
        initForm();
        initFooter();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBounds(0, 0, 450, 40);
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);

        // Pencere Sürükleme Mantığı
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
            }
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
        JLabel lblTitle = new JLabel("SİNEMA");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setBounds(0, 70, 450, 40);
        contentPane.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Hoşgeldiniz, lütfen giriş yapın.");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setBounds(0, 110, 450, 20);
        contentPane.add(lblSubtitle);
    }

    private void initForm() {
        // Kullanıcı Adı
        JLabel lblUser = new JLabel("Kullanıcı Adı");
        lblUser.setForeground(COLOR_TEXT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setBounds(75, 170, 300, 20);
        contentPane.add(lblUser);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        txtUsername.setBounds(75, 195, 300, 35);
        contentPane.add(txtUsername);

        // Şifre
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setForeground(COLOR_TEXT);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPass.setBounds(75, 250, 300, 20);
        contentPane.add(lblPass);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setBounds(75, 275, 300, 35);
        contentPane.add(txtPassword);

        // Giriş Butonu
        JButton btnLogin = new JButton("GİRİŞ YAP");
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(75, 350, 300, 45);
        btnLogin.setBackground(COLOR_ACCENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);

        // Enter tuşu ile giriş yapabilmek için:
        getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(this::handleLogin);
        contentPane.add(btnLogin);

        // Kayıt Linki
        JLabel lblRegister = new JLabel("Hesabın yok mu? Kayıt Ol");
        lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegister.setHorizontalAlignment(SwingConstants.CENTER);
        lblRegister.setForeground(Color.GRAY);
        lblRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRegister.setBounds(0, 410, 450, 30);

        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO: RegisterFrame entegrasyonu
                JOptionPane.showMessageDialog(null, "Kayıt ekranı yapım aşamasında.");
                // new RegisterFrame(authService).setVisible(true);
                // dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) { lblRegister.setForeground(COLOR_ACCENT); }
            @Override
            public void mouseExited(MouseEvent e) { lblRegister.setForeground(Color.GRAY); }
        });
        contentPane.add(lblRegister);
    }

    private void initFooter() {
        JLabel lblFooter = new JLabel("© 2025 Sinema Yönetim Yazılımı");
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setForeground(Color.DARK_GRAY);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setBounds(0, 560, 450, 20);
        contentPane.add(lblFooter);
    }

    private void styleTextField(JTextField field) {
        field.setBackground(COLOR_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(COLOR_ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_INPUT_BORDER));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_ACCENT)); }
            @Override
            public void focusLost(FocusEvent e) { field.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_INPUT_BORDER)); }
        });
    }

    // --- GİRİŞ MANTIĞI ---
    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- HIBERNATE ENTEGRASYONUNDA BURASI DEĞİŞECEK ---
        // Gerçek senaryoda: User user = authService.login(username, password);
        // if (user != null) { role kontrolü yap }

        // Şimdilik simülasyon:
        if ("admin".equals(username) && "123".equals(password)) {
            openDashboard("Admin Dashboard");
            // new AdminDashboard().setVisible(true);
        }
        else if ("kasiyer".equals(username) && "123".equals(password)) {
            openDashboard("Kasiyer Ekranı");
            // new CashierFrame().setVisible(true);
        }
        else if ("musteri".equals(username) && "123".equals(password)) {
            // Müşteri Paneli Açılışı
            SwingUtilities.invokeLater(() -> {
                try {
                    new CustomerMainFrame().setVisible(true);
                    this.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Arayüz yüklenemedi: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        }
        else {
            JOptionPane.showMessageDialog(this, "Hatalı Kullanıcı Adı veya Şifre", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Geçici yardımcı metod
    private void openDashboard(String title) {
        JOptionPane.showMessageDialog(this, title + " Yükleniyor...");
        this.dispose();
    }
}