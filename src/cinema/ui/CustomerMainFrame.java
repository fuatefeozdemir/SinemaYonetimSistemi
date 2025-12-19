package cinema.ui;

import cinema.model.content.Media;
import cinema.model.content.Film;
import cinema.H2.MediaRepository;
import cinema.service.AuthService;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.List;

public class CustomerMainFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;
    private final MediaRepository mediaRepository;
    private JPanel contentPane;
    private JPanel moviesPanel;
    private int mouseX, mouseY;

    // Renk Paleti
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_CARD_BG = new Color(45, 45, 45);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_BORDER = new Color(60, 60, 60);

    public CustomerMainFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;
        this.mediaRepository = new MediaRepository();

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1020, 720);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        initHeader();
        initMovieGrid();
        loadMoviesFromDatabase();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setLayout(null);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Pencere Sürükleme Mantığı
        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        JLabel lblTitle = new JLabel("SİNEMA DÜNYASI");
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 250, 30);
        headerPanel.add(lblTitle);

        // Kullanıcı Karşılama Metni
        String userName = (authService.getCurrentUser() != null) ? authService.getCurrentUser().getFirstName() : "Misafir";
        JLabel lblUser = new JLabel("Hoşgeldin, " + userName);
        lblUser.setForeground(Color.GRAY);
        lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setBounds(520, 15, 150, 30); // Profil butonu için yer açmak adına X koordinatını sola kaydırdık
        headerPanel.add(lblUser);

        // --- PROFİLİM BUTONU ---
        JButton btnProfile = new JButton("PROFİLİM");
        btnProfile.setBounds(690, 15, 120, 30);
        btnProfile.setBackground(new Color(60, 60, 60));
        btnProfile.setForeground(Color.WHITE);
        btnProfile.setFocusPainted(false);
        btnProfile.setBorder(new LineBorder(COLOR_BORDER));
        btnProfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProfile.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnProfile.addActionListener(e -> {
            // ProfileFrame'i açıyoruz (authService parametresi ile)
            new ProfileFrame(authService).setVisible(true);
        });
        headerPanel.add(btnProfile);

        // --- ÇIKIŞ BUTONU ---
        JButton btnLogout = new JButton("ÇIKIŞ");
        btnLogout.setBounds(820, 15, 100, 30);
        btnLogout.setBackground(COLOR_ACCENT); // Çıkış butonunu daha belirgin yaptık
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnLogout.addActionListener(e -> {
            authService.logout();
            new LoginFrame(authService, ticketService).setVisible(true);
            dispose();
        });
        headerPanel.add(btnLogout);

        // Kapatma (X) İkonu
        JLabel lblClose = new JLabel("✕");
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.setForeground(Color.WHITE);
        lblClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClose.setBounds(980, 10, 30, 40);
        lblClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
        });
        headerPanel.add(lblClose);
    }

    private void initMovieGrid() {
        // Grid yapısını detaylar sığsın diye biraz daha esnetebiliriz
        moviesPanel = new JPanel(new GridLayout(0, 4, 25, 25));
        moviesPanel.setBackground(COLOR_BG);
        moviesPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollBar(scrollPane);

        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadMoviesFromDatabase() {
        try {
            // MediaRepository içinde yazdığımız yeni metodu çağırıyoruz
            List<Media> mediaList = MediaRepository.getAllFilm();
            moviesPanel.removeAll();

            for (Media media : mediaList) {
                if (media instanceof Film film) {
                    moviesPanel.add(createMovieCard(film));
                }
            }
            moviesPanel.revalidate();
            moviesPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Filmler yüklenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createMovieCard(Film film) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(230, 420)); // Detaylar için yükseklik artırıldı
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new LineBorder(COLOR_BORDER, 1));

        // Poster Alanı
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBounds(0, 0, 230, 190);
        posterPanel.setBackground(new Color(25, 25, 25));

        String fileName = film.getName().toLowerCase().replace(" ", "_") + ".jpg";
        URL imgUrl = getClass().getResource("/posters/" + fileName);

        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(230, 190, Image.SCALE_SMOOTH));
            posterPanel.add(new JLabel(icon), BorderLayout.CENTER);
        } else {
            JLabel noPoster = new JLabel("🎬");
            noPoster.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            noPoster.setHorizontalAlignment(SwingConstants.CENTER);
            noPoster.setForeground(Color.DARK_GRAY);
            posterPanel.add(noPoster, BorderLayout.CENTER);
        }
        card.add(posterPanel);

        // IMDb Puanı (Poster üzerinde küçük bir etiket)
        JLabel lblRating = new JLabel("⭐ " + film.getImdbRating());
        lblRating.setOpaque(true);
        lblRating.setBackground(new Color(0,0,0,150));
        lblRating.setForeground(Color.YELLOW);
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRating.setBounds(165, 10, 55, 25);
        lblRating.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblRating);

        // Başlık
        JLabel lblTitle = new JLabel(film.getName());
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBounds(10, 200, 210, 25);
        card.add(lblTitle);

        // Tür ve Süre
        JLabel lblGenre = new JLabel(film.getGenre() + " • " + film.getDurationMinutes() + " dk");
        lblGenre.setForeground(COLOR_ACCENT);
        lblGenre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGenre.setBounds(10, 225, 210, 20);
        card.add(lblGenre);

        // DETAYLAR (Yönetmen, Dil, Yaş)
        String details = "<html><body style='width: 180px;'>" +
                "<b>Yönetmen:</b> " + film.getDirector() + "<br>" +
                "<b>Dil:</b> " + film.getLanguage() + " | " + "<b>Sınıf:</b> " + film.getAgeRestriction() +
                "</body></html>";

        JLabel lblDetails = new JLabel(details);
        lblDetails.setForeground(new Color(170, 170, 170));
        lblDetails.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetails.setBounds(10, 250, 210, 60);
        lblDetails.setVerticalAlignment(SwingConstants.TOP);
        card.add(lblDetails);

        // Satın Al Butonu
        JButton btnBuy = new JButton("BİLET AL");
        btnBuy.setBounds(10, 360, 210, 40);
        btnBuy.setBackground(COLOR_ACCENT);
        btnBuy.setForeground(Color.WHITE);
        btnBuy.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuy.setFocusPainted(false);
        btnBuy.setBorderPainted(false);
        btnBuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBuy.addActionListener(e -> new SeatSelectionFrame(film.getName(), authService, ticketService).setVisible(true));
        card.add(btnBuy);

        // Hover Efekti
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new LineBorder(COLOR_ACCENT, 2));
                card.setBackground(new Color(50, 50, 50));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(new LineBorder(COLOR_BORDER, 1));
                card.setBackground(COLOR_CARD_BG);
            }
        });

        return card;
    }

    private void styleScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(70, 70, 70);
                this.trackColor = COLOR_BG;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        });
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }
}