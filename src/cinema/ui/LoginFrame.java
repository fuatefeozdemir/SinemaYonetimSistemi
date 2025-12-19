package cinema.ui;

import cinema.service.AuthService;
import cinema.model.people.User;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private int mouseX, mouseY;

    // Renk Paleti
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_ACCENT = new Color(229, 9, 20); // Netflix Kırmızısı
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_INPUT_BORDER = Color.GRAY;


    // --- CONSTRUCTOR ---
    public LoginFrame(AuthService authService, TicketService ticketService) {
        // null kontrolü: Dışarıdan gelmezse yeni yarat
        this.authService = authService;
        this.ticketService = ticketService;
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
        JLabel lblUser = new JLabel("E-posta Adresi");
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

        getRootPane().setDefaultButton(btnLogin);
        btnLogin.addActionListener(this::handleLogin);
        contentPane.add(btnLogin);

        // Kayıt Linki - GÜNCELLENDİ
        JLabel lblRegister = new JLabel("Hesabın yok mu? Kayıt Ol");
        lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegister.setHorizontalAlignment(SwingConstants.CENTER);
        lblRegister.setForeground(Color.GRAY);
        lblRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRegister.setBounds(0, 410, 450, 30);

        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // RegisterFrame'e geçiş yap ve mevcut authService'i aktar
                RegisterFrame registerFrame = new RegisterFrame(authService, ticketService);
                registerFrame.setVisible(true);
                dispose(); // Login ekranını kapat
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

    private void handleLogin(ActionEvent e) {
        String email = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. AuthService aracılığıyla veritabanından kullanıcıyı doğrula
            User loggedInUser = authService.login(email, password);

            if (loggedInUser != null) {
                // 2. Rol Kontrolü ve Yönlendirme
                if (loggedInUser instanceof cinema.model.people.Customer) {
                    // Kullanıcı Customer tipindeyse CustomerMainFrame'e yönlendir
                    SwingUtilities.invokeLater(() -> {
                        try {
                            // CustomerMainFrame'in constructor'ı mevcut kullanıcıyı veya servisi bekleyebilir
                            new CustomerMainFrame(authService, ticketService).setVisible(true);
                            this.dispose(); // Giriş ekranını kapat
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Müşteri paneli açılırken hata: " + ex.getMessage());
                        }
                    });
                }
                else if (loggedInUser instanceof cinema.model.people.Cashier) {
                    // Eğer Kasiyer ise ilgili ekranı aç (Henüz yoksa mesaj göster)
                    JOptionPane.showMessageDialog(this, "Kasiyer Paneli Yükleniyor...");
                    // new CashierFrame().setVisible(true);
                    this.dispose();
                }
                else {
                    // Admin veya diğer roller için
                    JOptionPane.showMessageDialog(this, "Hoşgeldiniz: " + loggedInUser.getFirstName());
                }
            }

        } catch (Exception ex) {
            // Veritabanında kullanıcı bulunamazsa veya şifre yanlışsa burası çalışır
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Giriş Başarısız", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(String title) {
        JOptionPane.showMessageDialog(this, title + " Yükleniyor...");
        this.dispose();
    }
}