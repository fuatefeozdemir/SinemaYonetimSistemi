package cinema.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private final double TICKET_PRICE = 150.0; // Bilet Fiyatı

    // Renk Paleti
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_PANEL = new Color(45, 45, 45); // Panel Rengi
    private final Color COLOR_ACCENT = new Color(229, 9, 20); // Kırmızı
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_TEXT_MUTED = new Color(150, 150, 150);

    // Test için Main Metodu
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // Test verisi
                ArrayList<String> seats = new ArrayList<>();
                seats.add("A1");
                seats.add("A2");
                PaymentFrame frame = new PaymentFrame("Inception", seats);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PaymentFrame(String movieTitle, ArrayList<String> selectedSeats) {
        this.movieTitle = movieTitle;
        this.selectedSeats = selectedSeats;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- HEADER ---
        createHeader();

        // --- SOL PANEL: SİPARİŞ ÖZETİ ---
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBounds(30, 60, 300, 410);
        summaryPanel.setBackground(COLOR_PANEL);
        summaryPanel.setLayout(null);
        // Hafif yuvarlak köşe efekti yerine border ekleyelim
        summaryPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(60,60,60)));
        contentPane.add(summaryPanel);

        JLabel lblSummaryTitle = new JLabel("SİPARİŞ ÖZETİ");
        lblSummaryTitle.setForeground(COLOR_ACCENT);
        lblSummaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSummaryTitle.setBounds(20, 20, 200, 30);
        summaryPanel.add(lblSummaryTitle);

        // Film Bilgisi
        addSummaryLabel(summaryPanel, "FİLM:", 70, true);
        addSummaryLabel(summaryPanel, movieTitle, 95, false);

        // Koltuklar
        addSummaryLabel(summaryPanel, "KOLTUKLAR:", 135, true);
        // Köşeli parantezleri kaldırıp temiz gösterim yapalım
        String seatsStr = selectedSeats.toString().replace("[", "").replace("]", "");
        addSummaryLabel(summaryPanel, seatsStr, 160, false);

        // Tarih (Mock)
        addSummaryLabel(summaryPanel, "TARİH / SEANS:", 200, true);
        addSummaryLabel(summaryPanel, "Bugün - 20:00", 225, false);

        // Ara Çizgi
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.GRAY);
        separator.setBounds(20, 280, 260, 10);
        summaryPanel.add(separator);

        // Toplam Tutar
        double totalAmount = selectedSeats.size() * TICKET_PRICE;
        JLabel lblTotalLabel = new JLabel("TOPLAM TUTAR");
        lblTotalLabel.setForeground(COLOR_TEXT_MUTED);
        lblTotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTotalLabel.setBounds(20, 300, 150, 20);
        summaryPanel.add(lblTotalLabel);

        JLabel lblTotalPrice = new JLabel(totalAmount + " TL");
        lblTotalPrice.setForeground(Color.WHITE);
        lblTotalPrice.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalPrice.setBounds(20, 325, 200, 40);
        summaryPanel.add(lblTotalPrice);


        // --- SAĞ PANEL: KREDİ KARTI FORMU ---
        JLabel lblPaymentTitle = new JLabel("Kart Bilgileri");
        lblPaymentTitle.setForeground(Color.WHITE);
        lblPaymentTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPaymentTitle.setBounds(360, 60, 200, 30);
        contentPane.add(lblPaymentTitle);

        // Kart Üzerindeki İsim
        createFormLabel("Kart Sahibi Ad Soyad", 360, 110);
        txtCardName = createTextField(360, 135, 380);
        contentPane.add(txtCardName);

        // Kart Numarası
        createFormLabel("Kart Numarası", 360, 190);
        txtCardNumber = createTextField(360, 215, 380);
        // Basit bir placeholder mantığı veya maskeleme (sadece görsel)
        txtCardNumber.setText("4444 5555 6666 7777");
        txtCardNumber.setForeground(Color.GRAY);
        txtCardNumber.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(txtCardNumber.getText().startsWith("4444")) {
                    txtCardNumber.setText("");
                    txtCardNumber.setForeground(Color.WHITE);
                }
            }
        });
        contentPane.add(txtCardNumber);

        // Son Kullanma ve CVV (Yan Yana)
        createFormLabel("Son Kul. (AA/YY)", 360, 270);
        txtExpiry = createTextField(360, 295, 170);
        contentPane.add(txtExpiry);

        createFormLabel("CVV", 570, 270);
        txtCVV = createTextField(570, 295, 170);
        contentPane.add(txtCVV);

        // Ödeme Butonu
        JButton btnPay = new JButton("ÖDEMEYİ TAMAMLA (" + totalAmount + " TL)");
        btnPay.setBounds(360, 370, 380, 50);
        btnPay.setBackground(COLOR_ACCENT);
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.setFocusPainted(false);
        btnPay.setBorderPainted(false);
        btnPay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnPay.addActionListener(e -> processPayment());

        contentPane.add(btnPay);
    }

    // --- YARDIMCI METOTLAR ---

    // Ödeme İşlemi Simülasyonu
    private void processPayment() {
        // Basit validasyon
        if (txtCardName.getText().isEmpty() || txtCVV.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ödeme yapılıyormuş gibi hissettirmek için Timer kullanımı
        Timer timer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Ödeme Başarılı!\nBiletiniz oluşturuldu.\nİyi seyirler dileriz.");
                System.exit(0); // Veya ana menüye dön
            }
        });

        // Kullanıcıya bilgi verip butonu pasif yap
        JOptionPane.showMessageDialog(this, "Banka ile iletişim kuruluyor, lütfen bekleyin...", "İşlem Sürüyor", JOptionPane.INFORMATION_MESSAGE);
        timer.setRepeats(false);
        timer.start();
    }

    private void addSummaryLabel(JPanel panel, String text, int y, boolean isTitle) {
        JLabel lbl = new JLabel(text);
        if (isTitle) {
            lbl.setForeground(COLOR_TEXT_MUTED);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else {
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        }
        lbl.setBounds(20, y, 260, 20);
        panel.add(lbl);
    }

    private void createFormLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(COLOR_TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setBounds(x, y, 200, 20);
        contentPane.add(lbl);
    }

    private JTextField createTextField(int x, int y, int width) {
        JTextField field = new JTextField();
        field.setBounds(x, y, width, 35);
        field.setBackground(COLOR_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(COLOR_ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(new MatteBorder(0, 0, 2, 0, Color.GRAY));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_ACCENT));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new MatteBorder(0, 0, 2, 0, Color.GRAY));
            }
        });
        return field;
    }

    private void createHeader() {
        JPanel header = new JPanel();
        header.setBounds(0,0,800,40);
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

        // Sürükleme
        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });
    }
}
