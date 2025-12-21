package cinema.ui;

import cinema.model.Session;
import cinema.model.Ticket;
import cinema.model.people.Customer;
import cinema.model.people.User;
import cinema.service.AuthService;
import cinema.service.TicketService;
import cinema.service.SessionService;
import cinema.util.TicketPrinter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProfileFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;

    // UI Bileşenleri
    private JPanel contentPane;
    private JPanel mainContentPanel;
    private CardLayout cardLayout; // Sekmeler arası geçiş için
    private int mouseX, mouseY;

    // Form Alanları
    private JTextField txtFirstName, txtLastName, txtBirthDate;
    private JPasswordField txtPassword;

    // Renk değişkenleri
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    public ProfileFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;

        setUndecorated(true);
        setSize(550, 780);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 550, 780, 30, 30));

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(contentPane);

        initHeader();
        initNavigation();
        initMainContent();
    }

    // Üst bar
    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setBackground(COLOR_BG);
        header.setPreferredSize(new Dimension(550, 70));

        JLabel title = new JLabel("HESAP AYARLARI");
        title.setForeground(COLOR_TEXT_MAIN);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        title.setBounds(30, 20, 250, 30);
        header.add(title);

        JButton btnMin = new JButton("_");
        btnMin.setBounds(460, 15, 35, 35);
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        header.add(btnMin);

        JButton btnClose = new JButton("X");
        btnClose.setBounds(500, 15, 35, 35);
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        contentPane.add(header, BorderLayout.NORTH);
    }

    // Profil ve Biletlerim butonlarının olduğu alan
    private void initNavigation() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        navPanel.setBackground(COLOR_BG);
        navPanel.setPreferredSize(new Dimension(550, 50));
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        JButton btnInfo = createNavButton("PROFİL BİLGİLERİ", true);
        JButton btnTickets = createNavButton("BİLETLERİM", false);

        btnInfo.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "INFO");
            resetNavButtons(navPanel, btnInfo);
        });

        btnTickets.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "TICKETS");
            resetNavButtons(navPanel, btnTickets);
        });

        navPanel.add(btnInfo);
        if (authService.getCurrentUser() instanceof Customer) {
            navPanel.add(btnTickets);
        }
        contentPane.add(navPanel, BorderLayout.CENTER);
    }

    // Seçilen menüye göre değişen ana ekran
    private void initMainContent() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        mainContentPanel.add(createProfilePanel(), "INFO");
        mainContentPanel.add(createTicketsPanel(), "TICKETS");

        contentPane.add(mainContentPanel, BorderLayout.SOUTH);
        mainContentPanel.setPreferredSize(new Dimension(550, 660));
    }

    // Kullanıcının kendi bilgilerini değiştirebildiği panel
    private JPanel createProfilePanel() {
        User user = authService.getCurrentUser();
        JPanel panel = new JPanel(null);
        panel.setBackground(COLOR_BG);

        JLabel lblAvatar = new JLabel("👤", SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 60));
        lblAvatar.setForeground(COLOR_ACCENT);
        lblAvatar.setBounds(225, 10, 100, 100);
        panel.add(lblAvatar);

        int startY = 110;
        int spacing = 75;

        addModernField(panel, "E-POSTA (Değiştirilemez)", user.getEmail(), startY, false);
        txtFirstName = addModernField(panel, "AD", user.getFirstName(), startY + spacing, true);
        txtLastName = addModernField(panel, "SOYAD", user.getLastName(), startY + (spacing * 2), true);
        txtBirthDate = addModernField(panel, "DOĞUM TARİHİ (GG.AA.YYYY)",
                (user.getDateOfBirth() != null) ? user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "",
                startY + (spacing * 3), true);

        JLabel lblPass = new JLabel("ŞİFRE");
        lblPass.setForeground(COLOR_ACCENT);
        lblPass.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lblPass.setBounds(50, startY + (spacing * 4), 450, 20);
        panel.add(lblPass);

        txtPassword = new JPasswordField(user.getPassword());
        txtPassword.setBounds(50, startY + (spacing * 4) + 22, 450, 42);
        styleInput(txtPassword);
        panel.add(txtPassword);

        JButton btnSave = new JButton("DEĞİŞİKLİKLERİ KAYDET");
        btnSave.setBounds(50, 540, 450, 45);
        styleMainButton(btnSave);
        btnSave.addActionListener(e -> handleUpdate());
        panel.add(btnSave);

        if (user instanceof Customer) {
            JButton btnDel = new JButton("Hesabı Sil");
            btnDel.setBounds(225, 600, 100, 25);
            btnDel.setForeground(COLOR_TEXT_SUB);
            btnDel.setContentAreaFilled(false);
            btnDel.setBorder(null);
            btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDel.addActionListener(e -> handleDeleteAccount());
            panel.add(btnDel);
        }
        return panel;
    }

    // Kullanıcının biletlerini listeleyen panel
    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel ticketListContainer = new JPanel();
        ticketListContainer.setLayout(new BoxLayout(ticketListContainer, BoxLayout.Y_AXIS));
        ticketListContainer.setBackground(COLOR_BG);

        String currentEmail = authService.getCurrentUser().getEmail();
        List<Ticket> myTickets = ticketService.getMyTickets(currentEmail);

        if (myTickets == null || myTickets.isEmpty()) {
            JLabel lblEmpty = new JLabel("🎟️ Henüz satın alınmış bir biletiniz bulunmuyor.", SwingConstants.CENTER);
            lblEmpty.setForeground(COLOR_TEXT_SUB);
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblEmpty.setBorder(new EmptyBorder(100, 0, 0, 0));
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            ticketListContainer.add(lblEmpty);
        } else {
            for (Ticket t : myTickets) {
                ticketListContainer.add(createTicketCard(t));
                ticketListContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(ticketListContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(COLOR_BG);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Biletler için kart oluşturur
    private JPanel createTicketCard(Ticket t) {
        Session session = SessionService.getSession(t.getSession());
        String filmName = (session != null) ? session.getFilm().getName() : "Bilinmeyen Film";
        String sessionTime = (session != null) ? session.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--";
        String hallName = (session != null) ? session.getHall().getHallName() : "Salon -";

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(COLOR_ACCENT); // Sol taraftaki dekoratif kırmızı çizgi
                g2.fillRoundRect(0, 0, 8, getHeight(), 20, 20);
                g2.fillRect(4, 0, 4, getHeight());
                g2.dispose();
            }
        };
        card.setMaximumSize(new Dimension(480, 130));
        card.setPreferredSize(new Dimension(480, 130));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 25, 12, 15));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 0));
        infoPanel.setOpaque(false);

        JLabel lblFilm = new JLabel(filmName.toUpperCase());
        lblFilm.setForeground(COLOR_TEXT_MAIN);
        lblFilm.setFont(new Font("Segoe UI Black", Font.BOLD, 15));

        JLabel lblDetails = new JLabel(String.format("📍 %s  |  💺 %s  |  🕒 %s", hallName, t.getSeatCode(), sessionTime));
        lblDetails.setForeground(COLOR_TEXT_SUB);
        lblDetails.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));

        JLabel lblId = new JLabel("Bilet No: " + t.getTicketId().substring(0, 8).toUpperCase() + " | " + t.getFinalPrice() + " TL");
        lblId.setForeground(new Color(70, 70, 70));
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        infoPanel.add(lblFilm);
        infoPanel.add(lblDetails);
        infoPanel.add(lblId);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 25));
        actionPanel.setOpaque(false);

        JButton btnPrint = new JButton("FİŞ OLUŞTUR");
        styleSmallButton(btnPrint, Color.WHITE);
        btnPrint.addActionListener(e -> {
            if (TicketPrinter.printToText(t, session)) {
                JOptionPane.showMessageDialog(this, "Fiş başarıyla oluşturuldu.");
            }
        });

        JButton btnRefund = new JButton("İADE ET");
        styleSmallButton(btnRefund, COLOR_ACCENT);
        btnRefund.addActionListener(e -> handleTicketRefund(t));

        actionPanel.add(btnPrint);
        actionPanel.add(btnRefund);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private void handleTicketRefund(Ticket t) {
        int choice = JOptionPane.showConfirmDialog(this, "Bileti iade etmek istediğinize emin misiniz?", "BİLET İADE", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            if (ticketService.refundTicket(t.getTicketId())) {
                JOptionPane.showMessageDialog(this, "Bilet iade edildi.");
                refreshTickets();
            }
        }
    }

    private void refreshTickets() {
        mainContentPanel.add(createTicketsPanel(), "TICKETS");
        cardLayout.show(mainContentPanel, "TICKETS");
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void handleUpdate() {
        try {
            User user = authService.getCurrentUser();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            user.setFirstName(txtFirstName.getText().trim());
            user.setLastName(txtLastName.getText().trim());
            user.setDateOfBirth(LocalDate.parse(txtBirthDate.getText().trim(), formatter));
            user.setPassword(new String(txtPassword.getPassword()));

            authService.updateUser(user);
            JOptionPane.showMessageDialog(this, "Profiliniz başarıyla güncellendi.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Hata: Tarih formatı GG.AA.YYYY olmalıdır.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private void handleDeleteAccount() {
        if (JOptionPane.showConfirmDialog(this, "Hesabınızı silmek üzeresiniz. Bu işlem geri alınamaz!", "DİKKAT", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            authService.deleteUser();
            System.exit(0);
        }
    }

    // Görsel metotlar

    private void styleSmallButton(JButton btn, Color hoverColor) {
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 9));
        btn.setForeground(new Color(180, 180, 180));
        btn.setBackground(new Color(30, 30, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(new Color(55, 55, 55), 1, true));
        btn.setPreferredSize(new Dimension(85, 28));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(hoverColor); btn.setBorder(new LineBorder(hoverColor)); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(180, 180, 180)); btn.setBorder(new LineBorder(new Color(55, 55, 55))); }
        });
    }

    private JTextField addModernField(JPanel p, String label, String value, int y, boolean editable) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(COLOR_ACCENT);
        lbl.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lbl.setBounds(50, y, 450, 20);
        p.add(lbl);

        JTextField tf = new JTextField(value);
        tf.setBounds(50, y + 22, 450, 42);
        tf.setEditable(editable);
        if (!editable) tf.setForeground(COLOR_TEXT_SUB);
        styleInput(tf);
        p.add(tf);
        return tf;
    }

    private void styleInput(JTextField tf) {
        tf.setBackground(COLOR_CARD);
        tf.setForeground(COLOR_TEXT_MAIN);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER, 1, true), new EmptyBorder(0, 15, 0, 15)));
    }

    private void styleMainButton(JButton btn) {
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleControlBtn(JButton btn, Color hover) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(new Color(100, 100, 100));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(hover); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(100, 100, 100)); }
        });
    }

    private JButton createNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 12));
        btn.setContentAreaFilled(false);
        btn.setBorder(active ? BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_ACCENT) : null);
        btn.setForeground(active ? COLOR_TEXT_MAIN : COLOR_TEXT_SUB);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void resetNavButtons(JPanel parent, JButton selected) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JButton b) {
                b.setBorder(null);
                b.setForeground(COLOR_TEXT_SUB);
            }
        }
        selected.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_ACCENT));
        selected.setForeground(COLOR_TEXT_MAIN);
    }

    public void showTicketsTab() {
        if (cardLayout != null && mainContentPanel != null) {
            cardLayout.show(mainContentPanel, "TICKETS");
        }
    }
}