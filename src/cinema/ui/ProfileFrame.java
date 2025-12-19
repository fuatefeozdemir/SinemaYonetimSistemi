package cinema.ui;

import cinema.model.people.Customer;
import cinema.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProfileFrame extends JFrame {

    private final AuthService authService;
    private JPanel contentPane;
    private int mouseX, mouseY;

    private JTextField txtFirstName, txtLastName, txtBirthDate;
    private JPasswordField txtPassword;

    private final Color COLOR_BG = new Color(25, 25, 25);
    private final Color COLOR_CARD = new Color(40, 40, 40);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT = new Color(230, 230, 230);
    private final Color COLOR_BORDER = new Color(50, 50, 50);

    public ProfileFrame(AuthService authService) {
        this.authService = authService;

        setUndecorated(true);
        setSize(500, 700);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(contentPane);

        initHeader();

        // Sekmeli Yapı (Tabbed Pane) - Modern Görünüm için
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_BG);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabbedPane.addTab("👤 BİLGİLERİM", createProfilePanel());
        tabbedPane.addTab("🎟️ BİLETLERİM", createTicketsPanel());

        contentPane.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        Customer user = (Customer) authService.getCurrentUser();
        JPanel panel = new JPanel(null);
        panel.setBackground(COLOR_BG);

        // Avatar Alanı
        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        lblAvatar.setForeground(COLOR_ACCENT);
        lblAvatar.setBounds(200, 20, 100, 60);
        panel.add(lblAvatar);

        // Inputlar
        createStaticField(panel, "E-POSTA (Sabit)", user.getEmail(), 100);

        JLabel lbl1 = createLabel(panel, "AD", 170);
        txtFirstName = createTextField(panel, user.getFirstName(), 190);

        JLabel lbl2 = createLabel(panel, "SOYAD", 240);
        txtLastName = createTextField(panel, user.getLastName(), 260);

        JLabel lbl3 = createLabel(panel, "DOĞUM TARİHİ (GG.AA.YYYY)", 310);
        String dateStr = (user.getDateOfBirth() != null) ? user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
        txtBirthDate = createTextField(panel, dateStr, 330);

        JLabel lbl4 = createLabel(panel, "ŞİFRE", 380);
        txtPassword = new JPasswordField(user.getPassword());
        txtPassword.setBounds(50, 400, 400, 40);
        styleComponent(txtPassword);
        panel.add(txtPassword);

        // Güncelle Butonu
        JButton btnUpdate = new JButton("DEĞİŞİKLİKLERİ KAYDET");
        btnUpdate.setBounds(50, 470, 400, 45);
        btnUpdate.setBackground(COLOR_ACCENT);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdate.addActionListener(e -> handleUpdate());
        panel.add(btnUpdate);

        // Hesap Sil Butonu
        JButton btnDelete = new JButton("Hesabımı Kalıcı Olarak Sil");
        btnDelete.setBounds(50, 530, 400, 30);
        btnDelete.setForeground(new Color(150, 150, 150));
        btnDelete.setContentAreaFilled(false);
        btnDelete.setBorder(null);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> handleDeleteAccount());
        panel.add(btnDelete);

        return panel;
    }

    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        // Veritabanından kullanıcının biletlerini çekiyoruz
//        List<String> userTickets = TicketRepository.getTicketsByEmail(authService.getCurrentUser().getEmail());
//
//        if (userTickets.isEmpty()) {
//            listModel.addElement("Henüz satın alınmış biletiniz bulunmuyor.");
//        } else {
//            for (String t : userTickets) listModel.addElement(t);
//        }

        JList<String> ticketList = new JList<>(listModel);
        ticketList.setBackground(COLOR_CARD);
        ticketList.setForeground(COLOR_TEXT);
        ticketList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticketList.setFixedCellHeight(40);
        ticketList.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(ticketList);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void handleDeleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hesabınızı silmek istediğinize emin misiniz?\nBu işlem geri alınamaz!",
                "HESAP SİLME", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.deleteUser();
            JOptionPane.showMessageDialog(this, "Hesabınız başarıyla silindi.");
            System.exit(0);
        }
    }

    private void handleUpdate() {
        try {
            Customer user = (Customer) authService.getCurrentUser();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            user.setFirstName(txtFirstName.getText());
            user.setLastName(txtLastName.getText());
            user.setDateOfBirth(LocalDate.parse(txtBirthDate.getText(), formatter));
            user.setPassword(new String(txtPassword.getPassword()));

            authService.updateUser(user);
            JOptionPane.showMessageDialog(this, "Profil güncellendi!");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Tarih formatı hatalı (GG.AA.YYYY)!");
        }
    }

    private void createStaticField(JPanel p, String label, String value, int y) {
        createLabel(p, label, y);
        JTextField tf = createTextField(p, value, y + 20);
        tf.setEditable(false);
        tf.setForeground(Color.GRAY);
    }

    private JLabel createLabel(JPanel p, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(COLOR_ACCENT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setBounds(50, y, 400, 20);
        p.add(lbl);
        return lbl;
    }

    private JTextField createTextField(JPanel p, String text, int y) {
        JTextField tf = new JTextField(text);
        tf.setBounds(50, y, 400, 40);
        styleComponent(tf);
        p.add(tf);
        return tf;
    }

    private void styleComponent(JComponent c) {
        c.setBackground(COLOR_CARD);
        c.setForeground(COLOR_TEXT);
        c.setBorder(new EmptyBorder(0, 10, 0, 10));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setBackground(COLOR_BG);
        header.setPreferredSize(new Dimension(500, 50));

        JLabel title = new JLabel("HESAP VE BİLETLERİM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setBounds(20, 10, 200, 30);
        header.add(title);

        JLabel close = new JLabel("✕");
        close.setForeground(Color.WHITE);
        close.setBounds(460, 10, 30, 30);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
        });
        header.add(close);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });
        contentPane.add(header, BorderLayout.NORTH);
    }
}