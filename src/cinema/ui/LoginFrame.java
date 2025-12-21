package cinema.ui;

import cinema.service.AuthService;
import cinema.model.people.User;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private int mouseX, mouseY;

    // Renk Paleti (Diğer ekranlarla tam uyumlu)
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);

    public LoginFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        // Oval Köşeler
        setShape(new RoundRectangle2D.Double(0, 0, 450, 600, 30, 30));

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(new Color(35, 35, 35), 1));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        initHeader();
        initTitle();
        initForm();
        initFooter();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBounds(0, 0, 450, 50);
        contentPane.add(headerPanel);

        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        // Minimize Butonu (_)
        JButton btnMin = new JButton("_");
        btnMin.setBounds(370, 10, 35, 35);
        btnMin.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        headerPanel.add(btnMin);

        // Kapatma Butonu (X)
        JButton btnClose = new JButton("X");
        btnClose.setBounds(410, 10, 35, 35);
        btnClose.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> System.exit(0));
        headerPanel.add(btnClose);
    }

    private void initTitle() {
        JLabel lblTitle = new JLabel("SİNEMA");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 36));
        lblTitle.setBounds(0, 70, 450, 50);
        contentPane.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Hoş geldiniz, lütfen giriş yapın.");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(COLOR_TEXT_SUB);
        lblSubtitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblSubtitle.setBounds(0, 120, 450, 20);
        contentPane.add(lblSubtitle);
    }

    private void initForm() {
        int fieldX = 75;

        JLabel lblUser = new JLabel("E-POSTA ADRESİ");
        lblUser.setForeground(COLOR_ACCENT);
        lblUser.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lblUser.setBounds(fieldX, 180, 300, 20);
        contentPane.add(lblUser);

        txtUsername = new JTextField();
        styleTextField(txtUsername);
        txtUsername.setBounds(fieldX, 205, 300, 42);
        contentPane.add(txtUsername);

        JLabel lblPass = new JLabel("ŞİFRE");
        lblPass.setForeground(COLOR_ACCENT);
        lblPass.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lblPass.setBounds(fieldX, 265, 300, 20);
        contentPane.add(lblPass);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setBounds(fieldX, 290, 300, 42);
        contentPane.add(txtPassword);

        JButton btnLogin = new JButton("GİRİŞ YAP");
        btnLogin.setBounds(fieldX, 370, 300, 48);
        btnLogin.setBackground(COLOR_ACCENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI Bold", Font.PLAIN, 15));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(this::handleLogin);
        contentPane.add(btnLogin);

        JLabel lblRegister = new JLabel("Hesabın yok mu? Kayıt Ol");
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.setHorizontalAlignment(SwingConstants.CENTER);
        lblRegister.setForeground(COLOR_TEXT_SUB);
        lblRegister.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        lblRegister.setBounds(0, 440, 450, 30);

        lblRegister.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RegisterFrame(authService, ticketService).setVisible(true);
                dispose();
            }
            public void mouseEntered(MouseEvent e) { lblRegister.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e) { lblRegister.setForeground(COLOR_TEXT_SUB); }
        });
        contentPane.add(lblRegister);

        getRootPane().setDefaultButton(btnLogin);
    }

    private void initFooter() {
        JLabel lblFooter = new JLabel("© 2025 SİNEMA YÖNETİM SİSTEMİ");
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setForeground(new Color(60, 60, 60));
        lblFooter.setFont(new Font("Segoe UI Bold", Font.PLAIN, 10));
        lblFooter.setBounds(0, 550, 450, 20);
        contentPane.add(lblFooter);
    }

    private void styleTextField(JTextField field) {
        field.setBackground(COLOR_CARD);
        field.setForeground(Color.WHITE);
        field.setCaretColor(COLOR_ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(40, 40, 40), 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.setBorder(new LineBorder(COLOR_ACCENT, 1, true)); }
            @Override
            public void focusLost(FocusEvent e) { field.setBorder(new LineBorder(new Color(40, 40, 40), 1, true)); }
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

    private void handleLogin(ActionEvent e) {
        String email = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User loggedInUser = authService.login(email, password);

            if (loggedInUser != null) {
                // HATA BURADAYDI: ManagerMainFrame artık ticketService istiyor.
                if (loggedInUser instanceof cinema.model.people.Manager) {
                    new ManagerMainFrame(authService, ticketService).setVisible(true);
                    this.dispose();
                }
                else if (loggedInUser instanceof cinema.model.people.Customer) {
                    new CustomerMainFrame(authService, ticketService).setVisible(true);
                    this.dispose();
                }
                else if (loggedInUser instanceof cinema.model.people.Cashier) {
                    new CashierMainFrame(authService, ticketService).setVisible(true);
                    this.dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hatalı e-posta veya şifre!", "Giriş Başarısız", JOptionPane.ERROR_MESSAGE);
        }
    }
}