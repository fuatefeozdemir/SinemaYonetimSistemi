package cinema.ui;

import cinema.model.content.Film;
import cinema.model.content.Media;
import cinema.service.MediaService;
import cinema.service.HallService;
import cinema.service.SessionService;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;


public class SessionFormDialog extends JDialog {

    // Servis Katmanları
    private final SessionService sessionService;
    private final MediaService mediaService;
    private final HallService hallService;

    // UI Bileşenleri
    private JComboBox<String> cbFilms;
    private JComboBox<String> cbHalls;
    private JTextField txtDateTime;

    // Renk değişkenleri
    private final Color COLOR_BG = new Color(15, 15, 15);
    private final Color COLOR_CARD = new Color(25, 25, 25);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_BORDER = new Color(40, 40, 40);

    public SessionFormDialog(Frame owner, SessionService ss, MediaService ms, HallService hs) {
        super(owner, "Yeni Seans Oluştur", true);
        this.sessionService = ss;
        this.mediaService = ms;
        this.hallService = hs;

        setUndecorated(true);
        setSize(400, 500);
        setLocationRelativeTo(owner);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 500, 30, 30));

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(mainPanel);

        initComponents(mainPanel);
        loadData(); // Form açıldığında verileri yükle
    }

    private void initComponents(JPanel panel) {
        JLabel lblTitle = new JLabel("SEANS TANIMLA");
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setBounds(30, 30, 300, 30);
        panel.add(lblTitle);

        createLabel(panel, "Film Seçiniz:", 80);
        cbFilms = new JComboBox<>();
        styleComboBox(cbFilms);
        cbFilms.setBounds(30, 110, 340, 40);
        panel.add(cbFilms);

        createLabel(panel, "Salon Seçiniz:", 170);
        cbHalls = new JComboBox<>();
        styleComboBox(cbHalls);
        cbHalls.setBounds(30, 200, 340, 40);
        panel.add(cbHalls);

        createLabel(panel, "Tarih ve Saat (YYYY-MM-DD HH:MM):", 260);
        txtDateTime = new JTextField("2025-12-25 20:00");
        styleTextField(txtDateTime);
        txtDateTime.setBounds(30, 290, 340, 40);
        panel.add(txtDateTime);

        JButton btnSave = new JButton("KAYDET");
        btnSave.setBounds(30, 380, 160, 45);
        styleButton(btnSave, COLOR_ACCENT, Color.WHITE);
        btnSave.addActionListener(e -> onSave());
        panel.add(btnSave);

        JButton btnCancel = new JButton("İPTAL");
        btnCancel.setBounds(210, 380, 160, 45);
        styleButton(btnCancel, COLOR_CARD, Color.LIGHT_GRAY);
        btnCancel.addActionListener(e -> dispose());
        panel.add(btnCancel);
    }

    // Veritabanındaki film ve salon listelerini getirir
    private void loadData() {
        try {
            List<Media> films = mediaService.getAllFilms();
            films.stream()
                    .filter(m -> m instanceof Film)
                    .forEach(f -> cbFilms.addItem(f.getName()));

            List<String> hallNames = hallService.getAllHallNames();
            hallNames.forEach(cbHalls::addItem);
        } catch (Exception e) {
            System.err.println("Veri yükleme hatası: " + e.getMessage());
        }
    }

    // Seçimleri kaydeder
    private void onSave() {
        String selectedFilm = (String) cbFilms.getSelectedItem();
        String selectedHall = (String) cbHalls.getSelectedItem();
        String dateTime = txtDateTime.getText().trim();

        if (selectedFilm == null || selectedHall == null || dateTime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm seçimleri yapın ve tarih alanını doldurun.");
            return;
        }

        try {
            sessionService.addSession(selectedFilm, selectedHall, dateTime);
            JOptionPane.showMessageDialog(this, "Seans başarıyla sisteme kaydedildi.");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kayıt Hatası: " + e.getMessage());
        }
    }

    // Görsel metotlar

    private void createLabel(JPanel p, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setBounds(30, y, 300, 20);
        p.add(lbl);
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setBackground(COLOR_CARD);
        cb.setForeground(COLOR_TEXT);
        cb.setBorder(new LineBorder(COLOR_BORDER));
        cb.setFocusable(false);
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(COLOR_CARD);
        tf.setForeground(COLOR_TEXT);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setBorder(new LineBorder(COLOR_BORDER));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}