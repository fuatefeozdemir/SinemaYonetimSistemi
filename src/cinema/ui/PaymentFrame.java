package cinema.ui;

import cinema.model.people.User;
import cinema.model.people.Customer;
import cinema.model.people.Cashier;
import cinema.service.AuthService;
import cinema.service.TicketService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class PaymentFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;
    private final String targetCustomerEmail;
    private JPanel contentPane;
    private JTextField txtCardName, txtCardNumber, txtExpiry, txtCVV;
    private int mouseX, mouseY;

    private String movieTitle;
    private ArrayList<String> selectedSeats;
    private final String selectedSession;
    private final double TICKET_PRICE = 150.0;

    // Premium Renk Paleti (Diğer ekranlarla tam uyumlu)
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    public PaymentFrame(String movieTitle, ArrayList<String> selectedSeats, String selectedSession,
                        AuthService authService, TicketService ticketService, String targetCustomerEmail) {
        this.movieTitle = movieTitle;
        this.selectedSeats = selectedSeats;
        this.selectedSession = selectedSession;
        this.authService = authService;
        this.ticketService = ticketService;
        this.targetCustomerEmail = targetCustomerEmail;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 850, 550, 30, 30));

        contentPane = new JPanel(null);
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(contentPane);

        initHeader();
        initSummaryPanel();
        initPaymentForm();
    }

    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 850, 60);
        header.setBackground(COLOR_BG);
        contentPane.add(header);

        JLabel logo = new JLabel("SİNEMA");
        logo.setForeground(COLOR_ACCENT);
        logo.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        logo.setBounds(30, 15, 150, 30);
        header.add(logo);

        // Pencere Kontrolleri
        JButton btnMin = new JButton("_");
        btnMin.setBounds(750, 15, 40, 35);
        btnMin.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        header.add(btnMin);

        JButton btnClose = new JButton("X");
        btnClose.setBounds(795, 15, 40, 35);
        btnClose.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });
    }

    private void initSummaryPanel() {
        JPanel summaryCard = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
        summaryCard.setBounds(30, 80, 320, 440);
        summaryCard.setOpaque(false);
        contentPane.add(summaryCard);

        JLabel title = new JLabel("SİPARİŞ ÖZETİ");
        title.setForeground(COLOR_ACCENT);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 20));
        title.setBounds(30, 30, 250, 30);
        summaryCard.add(title);

        addInfo(summaryCard, "FİLM", movieTitle.toUpperCase(), 80);
        addInfo(summaryCard, "SEANS", selectedSession, 150);
        addInfo(summaryCard, "KOLTUKLAR", String.join(", ", selectedSeats), 220);

        double total = selectedSeats.size() * TICKET_PRICE;
        JLabel lblTotalT = new JLabel("TOPLAM TUTAR");
        lblTotalT.setForeground(COLOR_TEXT_SUB);
        lblTotalT.setFont(new Font("Segoe UI Bold", Font.PLAIN, 12));
        lblTotalT.setBounds(30, 330, 200, 20);
        summaryCard.add(lblTotalT);

        JLabel lblPrice = new JLabel(String.format("%.2f TL", total));
        lblPrice.setForeground(Color.WHITE);
        lblPrice.setFont(new Font("Segoe UI Black", Font.BOLD, 32));
        lblPrice.setBounds(30, 355, 260, 45);
        summaryCard.add(lblPrice);
    }

    private void initPaymentForm() {
        int startX = 390;
        int fieldW = 410;

        JLabel lblTitle = new JLabel("KART BİLGİLERİ");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        lblTitle.setBounds(startX, 85, 300, 30);
        contentPane.add(lblTitle);

        txtCardName = addLabeledField("KART SAHİBİ", startX, 140, fieldW);
        txtCardNumber = addLabeledField("KART NUMARASI", startX, 215, fieldW);
        txtExpiry = addLabeledField("AA / YY", startX, 290, 195);
        txtCVV = addLabeledField("CVV", startX + 215, 290, 195);

        JButton btnPay = new JButton("ÖDEMEYİ TAMAMLA →");
        btnPay.setBounds(startX, 400, fieldW, 55);
        btnPay.setBackground(COLOR_ACCENT);
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
        btnPay.setFocusPainted(false);
        btnPay.setBorderPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.addActionListener(e -> handlePayment());
        contentPane.add(btnPay);

        JLabel lblSecurity = new JLabel("🔒 Güvenli Ödeme Altyapısı");
        lblSecurity.setForeground(COLOR_TEXT_SUB);
        lblSecurity.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
        lblSecurity.setBounds(startX, 465, 410, 20);
        lblSecurity.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblSecurity);
    }

    private void handlePayment() {
        if (txtCardNumber.getText().isEmpty() || txtCVV.getText().isEmpty() || txtCardName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen kart bilgilerini eksiksiz girin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User loggedInUser = authService.getCurrentUser();
            Customer buyer;

            if (loggedInUser instanceof Cashier) {
                User target = authService.getUser(targetCustomerEmail);
                if (!(target instanceof Customer)) throw new Exception("Müşteri bulunamadı!");
                buyer = (Customer) target;
            } else {
                buyer = (Customer) loggedInUser;
            }

            // Bilet Kayıt
            for (String seatCode : selectedSeats) {
                if (loggedInUser instanceof Cashier) {
                    ticketService.buyTicket(selectedSession, buyer, seatCode, (Cashier) loggedInUser);
                } else {
                    ticketService.buyTicket(selectedSession, buyer, seatCode);
                }
            }

            JOptionPane.showMessageDialog(this, "Ödeme onaylandı! Biletleriniz profilinize eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

            // Direkt Profil ekranına yönlendir
            dispose();
            ProfileFrame pf = new ProfileFrame(authService, ticketService);
            pf.showTicketsTab(); // Biletler sekmesini açan metodun ProfileFrame'de olması gerekir
            pf.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private JTextField addLabeledField(String label, int x, int y, int w) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(COLOR_ACCENT);
        lbl.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lbl.setBounds(x, y, w, 20);
        contentPane.add(lbl);

        JTextField tf = new JTextField();
        tf.setBounds(x, y + 22, w, 40);
        tf.setBackground(COLOR_CARD);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER, 1, true), new EmptyBorder(0, 10, 0, 10)));
        contentPane.add(tf);
        return tf;
    }

    private void addInfo(JPanel p, String title, String value, int y) {
        JLabel t = new JLabel(title);
        t.setForeground(COLOR_TEXT_SUB);
        t.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        t.setBounds(30, y, 260, 20);
        p.add(t);

        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        v.setBounds(30, y + 20, 260, 25);
        p.add(v);
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