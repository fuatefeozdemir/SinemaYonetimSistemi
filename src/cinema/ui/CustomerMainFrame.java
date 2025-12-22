package cinema.ui;

import cinema.model.content.Media;
import cinema.model.content.Film;
import cinema.model.Session;
import cinema.service.AuthService;
import cinema.service.SessionService;
import cinema.util.ServiceContainer;

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
    private final ServiceContainer serviceContainer;
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

    public CustomerMainFrame(ServiceContainer serviceContainer) {
        this.serviceContainer = serviceContainer;

        // Pencere ayarları
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 700, 30, 30));

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
        header.setPreferredSize(new Dimension(1100, 90));
        header.setBackground(COLOR_BG);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        // Sinema yazısı
        JLabel lblTitle = new JLabel("SİNEMA");
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setBounds(35, 25, 150, 40);
        header.add(lblTitle);

        // Arama çubuğu
        JTextField txtSearch = new JTextField("Film ara...");
        txtSearch.setBounds(200, 28, 300, 34);
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
        String userName = (serviceContainer.getAuthService().getCurrentUser() != null) ? serviceContainer.getAuthService().getCurrentUser().getFirstName() : "Misafir";
        JLabel lblName = new JLabel(userName.toUpperCase());
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblName.setBounds(510, 28, 160, 35);
        header.add(lblName);

        // Profil butonu
        JButton btnProfile = new JButton("PROFİL");
        btnProfile.setBounds(680, 30, 85, 30);
        styleHeaderButton(btnProfile, Color.WHITE);
        btnProfile.addActionListener(e -> new ProfileFrame(serviceContainer).setVisible(true));
        header.add(btnProfile);

        // Çıkış butonu
        JButton btnLogout = new JButton("ÇIKIŞ");
        btnLogout.setBounds(775, 30, 85, 30);
        styleHeaderButton(btnLogout, COLOR_ACCENT);
        btnLogout.addActionListener(e -> {
            serviceContainer.getAuthService().logout();
            new LoginFrame(serviceContainer).setVisible(true);
            dispose();
        });
        header.add(btnLogout);

        // Kapatma butonu
        JButton btnMin = new JButton("_");
        btnMin.setBounds(990, 15, 45, 35);
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        header.add(btnMin);

        // Küçültme butonu
        JButton btnClose = new JButton("X");
        btnClose.setBounds(1040, 15, 45, 35);
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> System.exit(0));
        header.add(btnClose);

        contentPane.add(header, BorderLayout.NORTH);
    }

    // Filmlerin kaydırılabilir olması
    private void initCarousel() {
        moviesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        moviesPanel.setBackground(COLOR_BG);
        moviesPanel.setBorder(new EmptyBorder(10, 40, 20, 40));

        scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(COLOR_BG);
        mainWrapper.add(createArrowButton("‹", -400), BorderLayout.WEST);
        mainWrapper.add(scrollPane, BorderLayout.CENTER);
        mainWrapper.add(createArrowButton("›", 400), BorderLayout.EAST);
        contentPane.add(mainWrapper, BorderLayout.CENTER);
    }

    // Film kartları
    private void displayMovies(List<Media> list) {
        moviesPanel.removeAll();
        for (Media m : list) { if (m instanceof Film f) moviesPanel.add(createMovieCard(f)); }

        int totalWidth = (list.size() * 310) + 100;
        moviesPanel.setPreferredSize(new Dimension(Math.max(totalWidth, 1000), 550));
        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    // Filmleri veritabanından çeker
    private void loadMoviesFromDatabase() {
        try {
            allMovies = serviceContainer.getMediaService().getAllFilms();
            displayMovies(allMovies);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Kaydırma efekti
    private JButton createArrowButton(String text, int amount) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 60));
        btn.setPreferredSize(new Dimension(60, 0));
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
            int step = diff / 8;

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
        card.setPreferredSize(new Dimension(280, 520));
        card.setBackground(COLOR_CARD_BG);
        card.setOpaque(false);

        // Kart üstünde durunca gözüken IMDB yazısı
        JPanel infoOverlay = new JPanel(new GridBagLayout());
        infoOverlay.setBackground(new Color(0, 0, 0, 200));
        infoOverlay.setBounds(0, 0, 280, 300);
        infoOverlay.setVisible(false);
        JLabel lblImdb = new JLabel("IMDB: " + film.getImdbRating());
        lblImdb.setForeground(Color.YELLOW);
        lblImdb.setFont(new Font("Segoe UI Bold", Font.PLAIN, 16));
        infoOverlay.add(lblImdb);
        card.add(infoOverlay);

        JLabel lblIcon = new JLabel("🎬");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 70));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setBounds(0, 30, 280, 180);
        card.add(lblIcon);

        JLabel lblTitle = new JLabel(film.getName().toUpperCase());
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI Bold", Font.PLAIN, 18));
        lblTitle.setBounds(20, 310, 240, 25);
        card.add(lblTitle);

        JLabel lblSub = new JLabel(film.getDirector() + " | " + film.getDurationMinutes() + "dk");
        lblSub.setForeground(COLOR_TEXT_SUB);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setBounds(20, 335, 240, 20);
        card.add(lblSub);

        // Seansların listelendiği alt bölüm
        JPanel sessionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        sessionContainer.setOpaque(false);
        sessionContainer.setBounds(15, 380, 250, 120);

        List<Session> sessions = serviceContainer.getSessionService().getSessionsByMediaName(film.getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        for (Session s : sessions) {
            JButton b = new JButton(s.getStartTime().format(dtf));
            b.setPreferredSize(new Dimension(60, 28));
            b.setBackground(new Color(40, 40, 40));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 10));
            b.setBorder(new LineBorder(new Color(60, 60, 60), 1, true));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.addActionListener(e -> new SeatSelectionFrame(film.getName(), serviceContainer).setVisible(true));
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
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 10));
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