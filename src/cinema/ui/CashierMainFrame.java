package cinema.ui;

import cinema.model.Session;
import cinema.model.content.Film;
import cinema.model.content.Media;
import cinema.service.AuthService;
import cinema.service.MediaService;
import cinema.service.SessionService;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CashierMainFrame extends JFrame {

    private JPanel contentPane;
    private int mouseX, mouseY;
    private JComboBox<String> cmbFilm;
    private JComboBox<String> cmbSession;
    private JTextField txtCustomerEmail;

    private final AuthService authService;
    private final TicketService ticketService;
    private final MediaService mediaService;
    private final SessionService sessionService = new SessionService();

    // Renk Paleti (Diğer Frame'ler ile Tam Uyumlu)
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    public CashierMainFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;
        this.mediaService = new MediaService();

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 700, 30, 30));

        contentPane = new JPanel(null);
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(contentPane);

        initHeader();
        initBody();

        refreshFilms();
    }

    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1000, 90);
        header.setBackground(COLOR_BG);

        // Logo ve Başlık
        JLabel lblLogo = new JLabel("SİNEMA");
        lblLogo.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
        lblLogo.setForeground(COLOR_ACCENT);
        lblLogo.setBounds(40, 25, 150, 40);
        header.add(lblLogo);

        JLabel lblSub = new JLabel("KASİYER PANELİ");
        lblSub.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXT_SUB);
        lblSub.setBounds(195, 32, 200, 30);
        header.add(lblSub);

        // Sağ Üst Grup
        String name = (authService.getCurrentUser() != null) ? authService.getCurrentUser().getFirstName() : "Kasiyer";
        JLabel lblName = new JLabel(name.toUpperCase());
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblName.setBounds(450, 33, 200, 35);
        header.add(lblName);

        JButton btnProfile = new JButton("PROFİL");
        btnProfile.setBounds(660, 34, 90, 32);
        styleHeaderButton(btnProfile, Color.WHITE);
        btnProfile.addActionListener(e -> new ProfileFrame(authService, ticketService).setVisible(true));
        header.add(btnProfile);

        JButton btnLogout = new JButton("ÇIKIŞ");
        btnLogout.setBounds(760, 34, 90, 32);
        styleHeaderButton(btnLogout, COLOR_ACCENT);
        btnLogout.addActionListener(e -> {
            authService.logout();
            new LoginFrame(authService, ticketService).setVisible(true);
            dispose();
        });
        header.add(btnLogout);

        // Pencere Kontrolleri
        JButton btnMin = new JButton("_");
        btnMin.setBounds(900, 15, 40, 35);
        btnMin.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        header.add(btnMin);

        JButton btnClose = new JButton("X");
        btnClose.setBounds(945, 15, 40, 35);
        btnClose.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> System.exit(0));
        header.add(btnClose);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        contentPane.add(header);
    }

    private void initBody() {
        JPanel formCard = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        formCard.setBounds(200, 130, 600, 480);
        formCard.setOpaque(false);
        contentPane.add(formCard);

        JLabel lblTitle = new JLabel("HIZLI BİLET SATIŞI");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblTitle.setBounds(50, 40, 300, 30);
        formCard.add(lblTitle);

        // Film Seçimi
        addLabel(formCard, "FİLM SEÇİN", 100);
        cmbFilm = new JComboBox<>();
        cmbFilm.setBounds(50, 125, 500, 42);
        styleComboBox(cmbFilm);
        cmbFilm.addActionListener(e -> refreshSessions());
        formCard.add(cmbFilm);

        // Seans Seçimi
        addLabel(formCard, "SEANS SEÇİN", 190);
        cmbSession = new JComboBox<>();
        cmbSession.setBounds(50, 215, 500, 42);
        styleComboBox(cmbSession);
        formCard.add(cmbSession);

        // Müşteri E-posta
        addLabel(formCard, "MÜŞTERİ E-POSTA ADRESİ", 280);
        txtCustomerEmail = new JTextField();
        txtCustomerEmail.setBounds(50, 305, 500, 42);
        styleTextField(txtCustomerEmail);
        formCard.add(txtCustomerEmail);

        // Satış Butonu
        JButton btnSell = new JButton("KOLTUK SEÇİMİNE GİT →");
        btnSell.setBounds(50, 385, 500, 55);
        btnSell.setBackground(COLOR_ACCENT);
        btnSell.setForeground(Color.WHITE);
        btnSell.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        btnSell.setFocusPainted(false);
        btnSell.setBorderPainted(false);
        btnSell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSell.addActionListener(e -> {
            String selectedMovie = (String) cmbFilm.getSelectedItem();
            String selectedSessionTime = (String) cmbSession.getSelectedItem();
            String customerEmail = txtCustomerEmail.getText().trim();

            if (selectedMovie == null || customerEmail.isEmpty() || selectedSessionTime == null || selectedSessionTime.contains("aktif seans yok")) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları eksiksiz doldurun!", "Hata", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new SeatSelectionFrame(selectedMovie, selectedSessionTime, customerEmail, authService, ticketService).setVisible(true);
        });
        formCard.add(btnSell);
    }

    // --- YARDIMCI METOTLAR ---

    private void addLabel(JPanel p, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(COLOR_ACCENT);
        lbl.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lbl.setBounds(50, y, 300, 20);
        p.add(lbl);
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(COLOR_BG);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        // ComboBox'ın açılır menüsünü de stilize etmek gerekebilir (UI Manager üzerinden)
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(COLOR_BG);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));
    }

    private void styleHeaderButton(JButton btn, Color hoverColor) {
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(Color.LIGHT_GRAY);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(55, 55, 55), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(hoverColor); btn.setBorder(new LineBorder(hoverColor)); }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.LIGHT_GRAY); btn.setBorder(new LineBorder(new Color(55, 55, 55))); }
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

    private void refreshFilms() {
        cmbFilm.removeAllItems();
        try {
            List<Media> mediaList = mediaService.getAllFilms();
            for (Media m : mediaList) {
                if (m instanceof Film f) cmbFilm.addItem(f.getName());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshSessions() {
        cmbSession.removeAllItems();
        String selectedFilm = (String) cmbFilm.getSelectedItem();
        if (selectedFilm == null) return;

        List<Session> dbSessions = sessionService.getSessionsByMediaName(selectedFilm);

        if (dbSessions.isEmpty()) {
            cmbSession.addItem("Bu film için aktif seans yok");
        } else {
            for (Session s : dbSessions) {
                String formatted = s.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                cmbSession.addItem(formatted);
            }
        }
    }
}