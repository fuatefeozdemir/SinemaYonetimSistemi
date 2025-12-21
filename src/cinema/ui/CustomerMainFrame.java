package cinema.ui;

import cinema.model.content.Media;
import cinema.model.content.Film;
import cinema.model.Session;
import cinema.service.AuthService;
import cinema.service.MediaService;
import cinema.service.SessionService;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerMainFrame extends JFrame {

    // Servis bağlantıları
    private final AuthService authService;
    private final TicketService ticketService;
    private final MediaService mediaService;
    private List<Media> allMovies = new ArrayList<>();

    private JPanel contentPane;
    private JPanel moviesPanel;
    private JScrollPane scrollPane;
    private int mouseX, mouseY;

    // Animasyon ve kaydırma ayarları
    private Timer scrollTimer;
    private int targetScrollValue = 0;

    // Renk değişkenleri
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD_BG = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);

    public CustomerMainFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;
        this.mediaService = new MediaService();

        // Pencere ayarları
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1280, 820, 30, 30));

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_BG);
        setContentPane(contentPane);

        initHeader();
        initCarousel();
        loadMoviesFromDatabase();
    }

    // Profil ve pencere ayarlarını içeren üstteki bar
    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1280, 100));
        header.setBackground(COLOR_BG);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        // Sinema yazısı
        JLabel lblTitle = new JLabel("SİNEMA");
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 32));
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setBounds(40, 30, 200, 40);
        header.add(lblTitle);

        // Arama çubuğu
        JTextField txtSearch = new JTextField("Film ara...");
        txtSearch.setBounds(260, 32, 350, 36);
        txtSearch.setBackground(new Color(25, 25, 25));
        txtSearch.setForeground(COLOR_TEXT_SUB);
        txtSearch.setCaretColor(COLOR_ACCENT);
        txtSearch.setBorder(new LineBorder(new Color(50, 50, 50), 1, true));

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Film ara...")) { txtSearch.setText(""); txtSearch.setForeground(Color.WHITE); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setText("Film ara..."); txtSearch.setForeground(COLOR_TEXT_SUB); }
            }
        });

        // Arama çubuğu ile filtreleme
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String query = txtSearch.getText().toLowerCase().trim();
                if (query.equals("film ara...")) { displayMovies(allMovies); }
                else {
                    displayMovies(allMovies.stream()
                            .filter(m -> m.getName().toLowerCase().contains(query))
                            .collect(Collectors.toList()));
                }
            }
        });
        header.add(txtSearch);

        // Kullanıcı adı
        String userName = (authService.getCurrentUser() != null) ? authService.getCurrentUser().getFirstName() : "Misafir";
        JLabel lblName = new JLabel(userName.toUpperCase());
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblName.setBounds(630, 33, 200, 35);
        header.add(lblName);

        // Profil butonu
        JButton btnProfile = new JButton("PROFİL");
        btnProfile.setBounds(840, 34, 90, 32);
        styleHeaderButton(btnProfile, Color.WHITE);
        btnProfile.addActionListener(e -> new ProfileFrame(authService, ticketService).setVisible(true));
        header.add(btnProfile);

        // Çıkış butonu
        JButton btnLogout = new JButton("ÇIKIŞ");
        btnLogout.setBounds(940, 34, 90, 32);
        styleHeaderButton(btnLogout, COLOR_ACCENT);
        btnLogout.addActionListener(e -> {
            authService.logout();
            new LoginFrame(authService, ticketService).setVisible(true);
            dispose();
        });
        header.add(btnLogout);

        // Kapatma butonu
        JButton btnMin = new JButton("_");
        btnMin.setBounds(1170, 15, 45, 35);
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        header.add(btnMin);

        // Küçültme butonu
        JButton btnClose = new JButton("X");
        btnClose.setBounds(1220, 15, 45, 35);
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> System.exit(0));
        header.add(btnClose);

        contentPane.add(header, BorderLayout.NORTH);
    }

    // Filmlerin kaydırılabilir olması
    private void initCarousel() {
        moviesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));
        moviesPanel.setBackground(COLOR_BG);
        moviesPanel.setBorder(new EmptyBorder(20, 50, 40, 50));

        scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(COLOR_BG);
        mainWrapper.add(createArrowButton("‹", -450), BorderLayout.WEST);
        mainWrapper.add(scrollPane, BorderLayout.CENTER);
        mainWrapper.add(createArrowButton("›", 450), BorderLayout.EAST);
        contentPane.add(mainWrapper, BorderLayout.CENTER);
    }

    // Film kartları
    private void displayMovies(List<Media> list) {
        moviesPanel.removeAll();
        for (Media m : list) { if (m instanceof Film f) moviesPanel.add(createMovieCard(f)); }

        int totalWidth = (list.size() * 360) + 100;
        moviesPanel.setPreferredSize(new Dimension(Math.max(totalWidth, 1200), 650));
        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    // Filmleri veritabanından çeker
    private void loadMoviesFromDatabase() {
        try {
            allMovies = mediaService.getAllFilms();
            displayMovies(allMovies);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Kaydırma efekti
    private JButton createArrowButton(String text, int amount) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 80));
        btn.setPreferredSize(new Dimension(80, 0));
        btn.setForeground(new Color(40, 40, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> smoothScroll(amount));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(40, 40, 40)); }
        });
        return btn;
    }

    // Kaydırma efekti
    private void smoothScroll(int amount) {
        if (scrollTimer != null && scrollTimer.isRunning()) return;

        targetScrollValue = scrollPane.getHorizontalScrollBar().getValue() + amount;
        int max = scrollPane.getHorizontalScrollBar().getMaximum() - scrollPane.getViewport().getWidth();

        targetScrollValue = Math.max(0, Math.min(targetScrollValue, max));

        scrollTimer = new Timer(10, e -> {
            int current = scrollPane.getHorizontalScrollBar().getValue();
            int diff = (targetScrollValue - current);
            int step = diff / 10;

            if (Math.abs(step) < 1) step = (diff > 0) ? 1 : -1;

            if (Math.abs(diff) <= 2) {
                scrollPane.getHorizontalScrollBar().setValue(targetScrollValue);
                scrollTimer.stop();
            } else {
                scrollPane.getHorizontalScrollBar().setValue(current + step);
            }
        });
        scrollTimer.start();
    }

    // Film kartı oluşturan metot
    private JPanel createMovieCard(Film film) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(320, 600));
        card.setBackground(COLOR_CARD_BG);
        card.setOpaque(false);

        // Kart üstünde durunca gözüken IMDB yazısı
        JPanel infoOverlay = new JPanel(new GridBagLayout());
        infoOverlay.setBackground(new Color(0, 0, 0, 200));
        infoOverlay.setBounds(0, 0, 320, 340);
        infoOverlay.setVisible(false);
        JLabel lblImdb = new JLabel("IMDB: " + film.getImdbRating());
        lblImdb.setForeground(Color.YELLOW);
        lblImdb.setFont(new Font("Segoe UI Bold", Font.PLAIN, 18));
        infoOverlay.add(lblImdb);
        card.add(infoOverlay);

        JLabel lblIcon = new JLabel("🎬");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setBounds(0, 40, 320, 200);
        card.add(lblIcon);

        JLabel lblTitle = new JLabel(film.getName().toUpperCase());
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI Bold", Font.PLAIN, 20));
        lblTitle.setBounds(25, 360, 270, 30);
        card.add(lblTitle);

        JLabel lblSub = new JLabel(film.getDirector() + " | " + film.getDurationMinutes() + "dk");
        lblSub.setForeground(COLOR_TEXT_SUB);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setBounds(25, 390, 270, 20);
        card.add(lblSub);

        // Seansların listelendiği alt bölüm
        JPanel sessionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        sessionContainer.setOpaque(false);
        sessionContainer.setBounds(20, 440, 280, 140);

        List<Session> sessions = SessionService.getSessionsByMediaName(film.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        for (Session s : sessions) {
            JButton b = new JButton(s.getStartTime().format(dtf));
            b.setPreferredSize(new Dimension(65, 30));
            b.setBackground(new Color(40, 40, 40));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
            b.setBorder(new LineBorder(new Color(60, 60, 60), 1, true));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.addActionListener(e -> new SeatSelectionFrame(film.getName(), authService, ticketService).setVisible(true));
            sessionContainer.add(b);
        }
        card.add(sessionContainer);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                infoOverlay.setVisible(true);
                card.setBorder(new LineBorder(COLOR_ACCENT, 1, true));
            }
            @Override public void mouseExited(MouseEvent e) {
                infoOverlay.setVisible(false);
                card.setBorder(null);
            }
        });
        return card;
    }

    private void styleHeaderButton(JButton btn, Color hoverColor) {
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(Color.LIGHT_GRAY);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(55, 55, 55), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(hoverColor); btn.setBorder(new LineBorder(hoverColor, 1, true)); }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.LIGHT_GRAY); btn.setBorder(new LineBorder(new Color(55, 55, 55), 1, true)); }
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
}