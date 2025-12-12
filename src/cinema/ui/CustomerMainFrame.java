package cinema.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;

public class CustomerMainFrame extends JFrame {

    private JPanel contentPane;
    private JPanel moviesPanel;
    private int mouseX, mouseY;

    // Renk Paleti
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_CARD_BG = new Color(45, 45, 45);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_BORDER = new Color(60, 60, 60);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                CustomerMainFrame frame = new CustomerMainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CustomerMainFrame() {
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
        loadDummyMovies();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setLayout(null);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Sürükleme
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        JLabel lblTitle = new JLabel("SİNEMA DÜNYASI");
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(20, 15, 250, 30);
        headerPanel.add(lblTitle);

        JLabel lblUser = new JLabel("Hoşgeldin, Müşteri");
        lblUser.setForeground(Color.GRAY);
        lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setBounds(680, 15, 150, 30);
        headerPanel.add(lblUser);

        JButton btnLogout = new JButton("Çıkış");
        btnLogout.setBounds(850, 15, 80, 30);
        btnLogout.setBackground(COLOR_BORDER);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            new LoginFrame(null).setVisible(true);
            dispose();
        });
        headerPanel.add(btnLogout);

        JLabel lblClose = new JLabel("X");
        lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblClose.setForeground(Color.WHITE);
        lblClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClose.setBounds(980, 10, 30, 40);
        lblClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
            public void mouseEntered(MouseEvent e) { lblClose.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e) { lblClose.setForeground(Color.WHITE); }
        });
        headerPanel.add(lblClose);
    }

    private void initMovieGrid() {
        moviesPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        moviesPanel.setBackground(COLOR_BG);
        moviesPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollBar(scrollPane);

        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDummyMovies() {
        // Geçici veri dizisi (Model sınıfı olmadan)
        String[][] dummyData = {
                {"Inception", "Bilim Kurgu", "148 dk"},
                {"The Dark Knight", "Aksiyon", "152 dk"},
                {"Interstellar", "Bilim Kurgu", "169 dk"},
                {"Titanic", "Romantik", "195 dk"},
                {"Avatar 2", "Macera", "192 dk"},
                {"Joker", "Dram", "122 dk"},
                {"Avengers", "Aksiyon", "181 dk"},
                {"Matrix", "Bilim Kurgu", "136 dk"}
        };

        for (String[] data : dummyData) {
            moviesPanel.add(createMovieCard(data[0], data[1], data[2]));
        }
    }

    private JPanel createMovieCard(String title, String genre, String duration) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(200, 340));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new LineBorder(COLOR_BORDER, 1));

        JPanel posterPanel = new JPanel();
        posterPanel.setBackground(new Color(60, 60, 60));
        posterPanel.setBounds(0, 0, 230, 190);
        posterPanel.setLayout(new GridBagLayout());

        JLabel lblIcon = new JLabel(title.substring(0, 1));
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 60));
        lblIcon.setForeground(new Color(90, 90, 90));
        posterPanel.add(lblIcon);
        card.add(posterPanel);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setBounds(10, 200, 200, 25);
        card.add(lblTitle);

        JLabel lblGenre = new JLabel(genre + " • " + duration);
        lblGenre.setForeground(Color.GRAY);
        lblGenre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGenre.setBounds(10, 225, 200, 20);
        card.add(lblGenre);

        JButton btnBuy = new JButton("BİLET AL");
        btnBuy.setBounds(10, 280, 200, 40);
        btnBuy.setBackground(COLOR_ACCENT);
        btnBuy.setForeground(Color.WHITE);
        btnBuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBuy.setFocusPainted(false);
        btnBuy.setBorderPainted(false);
        btnBuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBuy.addActionListener(e -> {
            try {
                // Koltuk seçimi ekranına sadece film adını gönderiyoruz
                SeatSelectionFrame seatFrame = new SeatSelectionFrame(title);
                seatFrame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Koltuk Seçim Ekranı (SeatSelectionFrame) bulunamadı!");
            }
        });
        card.add(btnBuy);

        return card;
    }

    private void styleScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(80, 80, 80);
                this.trackColor = COLOR_BG;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        });
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }
}