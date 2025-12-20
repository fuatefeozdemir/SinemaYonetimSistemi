package cinema.ui;

import cinema.service.AuthService;
import cinema.service.FilmStore;
import javax.swing.DefaultComboBoxModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CashierFrame extends JFrame {

    private JPanel contentPane;
    private int mouseX, mouseY;
    private JComboBox<String> cmbFilm;
    private JComboBox<String> cmbSession;
    private final AuthService authService;

    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_PANEL = new Color(45, 45, 45);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_TEXT_MUTED = new Color(150, 150, 150);

    public CashierFrame(AuthService authService) {
        this.authService = authService;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        createHeader();
        createBody();
    }

    private void createHeader() {
        JPanel header = new JPanel();
        header.setBounds(0, 0, 900, 45);
        header.setBackground(COLOR_BG);
        header.setLayout(null);
        contentPane.add(header);

        JLabel title = new JLabel("KASİYER PANELİ");
        title.setForeground(COLOR_TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBounds(20, 0, 300, 45);
        header.add(title);

        JLabel close = new JLabel("X");
        close.setForeground(Color.WHITE);
        close.setFont(new Font("Segoe UI", Font.BOLD, 18));
        close.setHorizontalAlignment(SwingConstants.CENTER);
        close.setBounds(860, 0, 40, 45);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
            @Override public void mouseEntered(MouseEvent e) { close.setForeground(COLOR_ACCENT); }
            @Override public void mouseExited(MouseEvent e) { close.setForeground(Color.WHITE); }
        });
        header.add(close);

        // sürükle
        header.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
            }
        });

        // alt çizgi
        JPanel line = new JPanel();
        line.setBackground(new Color(60, 60, 60));
        line.setBounds(0, 44, 900, 1);
        contentPane.add(line);
    }

    private void createBody() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 70, 860, 500);
        panel.setBackground(COLOR_PANEL);
        panel.setLayout(null);
        contentPane.add(panel);

        JLabel info = new JLabel("Kasiyer işlemleri: gişeden bilet satışı / iptal / seans seçimi (yakında).");
        info.setForeground(COLOR_TEXT_MUTED);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setBounds(20, 20, 800, 20);
        panel.add(info);

        // --- Film + Seans Seçimi (Kasiyer) ---
        JLabel lblFilm = new JLabel("Film:");
        lblFilm.setBounds(20, 20, 80, 20);
        lblFilm.setForeground(Color.LIGHT_GRAY);
        panel.add(lblFilm);
        cmbFilm = new JComboBox<>();
        cmbFilm.setBounds(80, 15, 200, 30);
        panel.add(cmbFilm);

        JLabel lblSession = new JLabel("Seans:");
        lblSession.setBounds(320, 20, 80, 20);
        lblSession.setForeground(Color.LIGHT_GRAY);
        panel.add(lblSession);

        String[] sessions = {"Bugün - 18:00", "Bugün - 20:00", "Bugün - 22:00", "Yarın - 18:00", "Yarın - 20:00", "Yarın - 22:00"};
        cmbSession = new JComboBox<>(sessions);
        cmbSession.setBounds(380, 15, 280, 30);
        panel.add(cmbSession);

        JButton btnSell = new JButton("BİLET SAT");
        btnSell.setBounds(20, 70, 200, 40);
        stylePrimary(btnSell);
        btnSell.addActionListener(e -> {
            String selectedMovie = (String) cmbFilm.getSelectedItem();
            String selectedSession = (String) cmbSession.getSelectedItem();

            // Koltuk ekranını aç
            SeatSelectionFrame seatFrame = new SeatSelectionFrame(selectedMovie, selectedSession);
            seatFrame.setVisible(true);

            // İstersen kasiyer ekranını kapat (zorunlu değil)
            // dispose();
        });
        panel.add(btnSell);

        JButton btnCancel = new JButton("BİLET İPTAL");
        btnCancel.setBounds(240, 70, 200, 40);
        styleSecondary(btnCancel);
        btnCancel.addActionListener(e -> JOptionPane.showMessageDialog(this, "Bilet iptal ekranı henüz eklenmedi."));
        panel.add(btnCancel);

        JButton btnSessions = new JButton("SEANS / SALON");
        btnSessions.setBounds(460, 70, 200, 40);
        styleSecondary(btnSessions);
        btnSessions.addActionListener(e -> JOptionPane.showMessageDialog(this, "Seans yönetimi henüz eklenmedi."));
        panel.add(btnSessions);

        refreshFilms();
    }

    private void stylePrimary(JButton b) {
        b.setBackground(COLOR_ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondary(JButton b) {
        b.setBackground(new Color(70, 70, 70));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void refreshFilms() {
        cmbFilm.removeAllItems();

        for (String film : FilmStore.getFilms()) {
            cmbFilm.addItem(film);
        }
    }
}
