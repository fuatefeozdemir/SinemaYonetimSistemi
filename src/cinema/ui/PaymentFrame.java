package cinema.ui;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PaymentFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtCardName, txtCardNumber, txtExpiry, txtCVV;
    private int mouseX, mouseY;

    // Veriler
    private String movieTitle;
    private ArrayList<String> selectedSeats;
    private String selectedSession;
    private final double TICKET_PRICE = 150.0;

    // Renk Paleti
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_PANEL = new Color(45, 45, 45);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MUTED = new Color(150, 150, 150);

    //  3 PARAMETRELİ CONSTRUCTOR (ASIL OLAN)
    public PaymentFrame(String movieTitle, ArrayList<String> selectedSeats, String selectedSession) {
        this.movieTitle = movieTitle;
        this.selectedSeats = selectedSeats;
        this.selectedSession = selectedSession;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        createHeader();
        createSummaryPanel();
        createPaymentForm();
    }

    // ---------------- HEADER ----------------
    private void createHeader() {
        JPanel header = new JPanel();
        header.setBounds(0, 0, 800, 40);
        header.setBackground(COLOR_BG);
        header.setLayout(null);
        contentPane.add(header);

        JLabel close = new JLabel("X");
        close.setForeground(Color.WHITE);
        close.setFont(new Font("Segoe UI", Font.BOLD, 18));
        close.setBounds(760, 0, 40, 40);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
        });
        header.add(close);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
            }
        });
    }

    // ---------------- SUMMARY PANEL ----------------
    private void createSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setBounds(30, 60, 300, 410);
        panel.setBackground(COLOR_PANEL);
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(60,60,60)));
        contentPane.add(panel);

        JLabel title = new JLabel("SİPARİŞ ÖZETİ");
        title.setForeground(COLOR_ACCENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(20, 20, 200, 30);
        panel.add(title);

        addSummary(panel, "FİLM:", movieTitle, 70);
        addSummary(panel, "KOLTUKLAR:", String.join(", ", selectedSeats), 135);
        addSummary(panel, "SEANS:", selectedSession, 200);

        double total = selectedSeats.size() * TICKET_PRICE;

        JLabel totalLabel = new JLabel("TOPLAM");
        totalLabel.setForeground(COLOR_TEXT_MUTED);
        totalLabel.setBounds(20, 280, 200, 20);
        panel.add(totalLabel);

        JLabel price = new JLabel(total + " TL");
        price.setForeground(Color.WHITE);
        price.setFont(new Font("Segoe UI", Font.BOLD, 26));
        price.setBounds(20, 305, 200, 40);
        panel.add(price);
    }

    private void addSummary(JPanel panel, String title, String value, int y) {
        JLabel t = new JLabel(title);
        t.setForeground(COLOR_TEXT_MUTED);
        t.setBounds(20, y, 200, 20);
        panel.add(t);

        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setBounds(20, y + 25, 260, 20);
        panel.add(v);
    }

    // ---------------- PAYMENT FORM ----------------
    private void createPaymentForm() {
        JLabel lbl = new JLabel("Kart Bilgileri");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setBounds(360, 60, 300, 30);
        contentPane.add(lbl);

        JLabel lblName = new JLabel("Kart Sahibi Ad Soyad");
        lblName.setForeground(COLOR_TEXT_MUTED);
        lblName.setBounds(360, 95, 300, 20);
        contentPane.add(lblName);

        JLabel lblCard = new JLabel("Kart Numarası");
        lblCard.setForeground(COLOR_TEXT_MUTED);
        lblCard.setBounds(360, 165, 300, 20);
        contentPane.add(lblCard);

        JLabel lblExpiry = new JLabel("Son Kul. (AA/YY)");
        lblExpiry.setForeground(COLOR_TEXT_MUTED);
        lblExpiry.setBounds(360, 235, 200, 20);
        contentPane.add(lblExpiry);

        JLabel lblCVV = new JLabel("CVV");
        lblCVV.setForeground(COLOR_TEXT_MUTED);
        lblCVV.setBounds(570, 235, 100, 20);
        contentPane.add(lblCVV);

        txtCardName = createField("Kart Sahibi", 360, 120, 380);
        txtCardNumber = createField("Kart Numarası", 360, 190, 380);
        txtExpiry = createField("AA/YY", 360, 260, 170);
        txtCVV = createField("CVV", 570, 260, 170);

        JButton pay = new JButton("ÖDEMEYİ TAMAMLA");
        pay.setBounds(360, 350, 380, 50);
        pay.setBackground(COLOR_ACCENT);
        pay.setForeground(Color.WHITE);
        pay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pay.setFocusPainted(false);

        pay.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Ödeme başarılı!\nİyi seyirler 🎬");
            dispose();
        });

        contentPane.add(pay);
    }

    private JTextField createField(String placeholder, int x, int y, int w) {
        JTextField f = new JTextField();
        f.setBounds(x, y, w, 35);
        f.setBackground(COLOR_BG);
        f.setForeground(Color.WHITE);
        f.setCaretColor(COLOR_ACCENT);
        f.setBorder(new MatteBorder(0, 0, 2, 0, Color.GRAY));
        contentPane.add(f);
        return f;
    }
}
