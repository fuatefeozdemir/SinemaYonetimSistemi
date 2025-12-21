package cinema.ui;

import cinema.model.content.*;
import cinema.service.MediaService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;

public class FilmFormDialog extends JDialog {

    private final MediaService mediaService;
    private final Film existingFilm;

    private JTextField txtName, txtDuration, txtReleaseDate, txtDirector, txtAge, txtGenre, txtLanguage, txtImdb;
    private JComboBox<String> cmbType;
    private JCheckBox chkVisible;

    // Renk değişkenleri
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    public FilmFormDialog(JFrame owner, String title, Film existingFilm, MediaService mediaService) {
        super(owner, title, true);
        this.existingFilm = existingFilm;
        this.mediaService = mediaService;

        // Form ayarları
        setUndecorated(true);
        setSize(450, 650);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);
        root.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(root);

        initHeader(title);
        initForm();
        initFooter();

        if (existingFilm != null) {
            fillFields();
        }
    }

    // Başlık ve kapatma butonunun olduğu bölüm
    private void initHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG);
        header.setPreferredSize(new Dimension(450, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 10));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        header.add(lblTitle, BorderLayout.WEST);

        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        btnClose.setForeground(new Color(150, 150, 150));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // Veri giriş alanlarının bulunduğu kısım
    private void initForm() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(COLOR_BG);
        formPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        cmbType = new JComboBox<>(new String[]{"Standard 2D", "Premium 3D", "Animasyon"});
        styleCombo(cmbType);
        formPanel.add(cmbType);

        // Form alanları
        txtName = addStyledField(formPanel, "FİLM ADI");
        txtGenre = addStyledField(formPanel, "TÜR");
        txtDuration = addStyledField(formPanel, "SÜRE (DAKİKA)");
        txtDirector = addStyledField(formPanel, "YÖNETMEN");
        txtImdb = addStyledField(formPanel, "IMDB PUANI (Örn: 8.5)");
        txtAge = addStyledField(formPanel, "YAŞ SINIRI");
        txtLanguage = addStyledField(formPanel, "DİL");
        txtReleaseDate = addStyledField(formPanel, "VİZYON TARİHİ (YYYY-MM-DD)");

        chkVisible = new JCheckBox("Vizyonda (Aktif)");
        chkVisible.setForeground(COLOR_TEXT_MAIN);
        chkVisible.setBackground(COLOR_BG);
        chkVisible.setFocusPainted(false);
        formPanel.add(chkVisible);

        // Eğer ekrana sığmazsa kaydırma butonu
        JScrollPane sp = new JScrollPane(formPanel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        sp.setBackground(COLOR_BG);
        add(sp, BorderLayout.CENTER);
    }

    // Kaydet butonunun olduğu alt bölüm
    private void initFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footer.setBackground(COLOR_BG);

        JButton btnSave = new JButton("DEĞİŞİKLİKLERİ KAYDET");
        btnSave.setPreferredSize(new Dimension(390, 45));
        btnSave.setBackground(COLOR_ACCENT);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI Bold", Font.PLAIN, 14));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> onSave());
        footer.add(btnSave);

        add(footer, BorderLayout.SOUTH);
    }

    // Film detaylarını düzenleme modunda kutulara yazan metot
    private void fillFields() {
        txtName.setText(existingFilm.getName());
        txtGenre.setText(existingFilm.getGenre());
        txtDuration.setText(String.valueOf(existingFilm.getDurationMinutes()));
        txtDirector.setText(existingFilm.getDirector());
        txtImdb.setText(String.valueOf(existingFilm.getImdbRating()));
        txtAge.setText(existingFilm.getAgeRestriction());
        txtLanguage.setText(existingFilm.getLanguage());
        txtReleaseDate.setText(existingFilm.getReleaseDate().toString());
        chkVisible.setSelected(existingFilm.isVisible());

        if (existingFilm instanceof Premium3D) cmbType.setSelectedItem("Premium 3D");
        else if (existingFilm instanceof Animation) cmbType.setSelectedItem("Animasyon");
        else cmbType.setSelectedItem("Standard 2D");
    }

    // Kaydetme işlemi
    private void onSave() {
        try {
            String name = txtName.getText().trim();
            int duration = Integer.parseInt(txtDuration.getText().trim());
            LocalDate date = LocalDate.parse(txtReleaseDate.getText().trim());
            float imdb = Float.parseFloat(txtImdb.getText().trim());
            String type = (String) cmbType.getSelectedItem();

            // Seçilen tipe göre uygun classtan nesne oluşturuluyor (Polimorfizm)
            Film film;
            if ("Premium 3D".equals(type)) {
                film = new Premium3D(name, duration, chkVisible.isSelected(), date, txtDirector.getText(), txtAge.getText(), txtGenre.getText(), txtLanguage.getText(), imdb);
            } else if ("Animasyon".equals(type)) {
                film = new Animation(name, duration, chkVisible.isSelected(), date, txtDirector.getText(), txtAge.getText(), txtGenre.getText(), txtLanguage.getText(), imdb);
            } else {
                film = new Standard2D(name, duration, chkVisible.isSelected(), date, txtDirector.getText(), txtAge.getText(), txtGenre.getText(), txtLanguage.getText(), imdb);
            }

            if (existingFilm == null) {
                mediaService.addMedia(film);
                JOptionPane.showMessageDialog(this, "Film sisteme eklendi.");
            } else {
                mediaService.updateMedia(film);
                JOptionPane.showMessageDialog(this, "Film bilgileri güncellendi.");
            }
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Giriş Hatası: Lütfen tüm alanları (özellikle tarih ve süre) doğru formatta doldurun.");
        }
    }

    // Metin kutusu
    private JTextField addStyledField(JPanel p, String label) {
        JLabel l = new JLabel(label);
        l.setForeground(COLOR_ACCENT);
        l.setFont(new Font("Segoe UI Bold", Font.PLAIN, 10));
        l.setBorder(new EmptyBorder(10, 0, 5, 0));
        p.add(l);

        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(400, 35));
        f.setBackground(COLOR_CARD);
        f.setForeground(Color.WHITE);
        f.setCaretColor(COLOR_ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER), new EmptyBorder(0, 10, 0, 10)));
        p.add(f);
        return f;
    }

    // Combobox tasarımı
    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(COLOR_CARD);
        cb.setForeground(Color.WHITE);
        cb.setMaximumSize(new Dimension(400, 35));
    }
}