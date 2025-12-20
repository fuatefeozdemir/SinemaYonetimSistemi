package cinema.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeatSelectionFrame extends JFrame {

    private JPanel contentPane;
    private String movieTitle;
    private List<String> selectedSeats = new ArrayList<>();
    private int mouseX, mouseY;
    private JLabel lblSummary;
    private final double TICKET_PRICE = 150.0;
    private String selectedSession = "Bugün - 20:00";
    private JComboBox<String> cmbSession;

    // --- RENK PALETİ ---
    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_HEADER_BORDER = new Color(60, 60, 60);
    private final Color COLOR_ACCENT = new Color(229, 9, 20); // Kırmızı

    // Koltuk Durum Renkleri
    private final Color COLOR_SEAT_EMPTY = new Color(100, 100, 100);
    private final Color COLOR_SEAT_FULL = new Color(180, 40, 40);
    private final Color COLOR_SEAT_SELECTED = new Color(46, 204, 113);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new SeatSelectionFrame("Inception").setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public SeatSelectionFrame(String movieTitle) {
        this.movieTitle = movieTitle;
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 950, 700);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        initHeader();
        initStage();
        initFooter();
    }

    public SeatSelectionFrame(String movieTitle, String selectedSession) {
        this.movieTitle = movieTitle;
        this.selectedSession = selectedSession;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 950, 700);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_BG);
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        initHeader();
        initStage();
        initFooter();
    }

    // --- 1. HEADER ---
    private void initHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setPreferredSize(new Dimension(900, 50));
        headerPanel.setLayout(null);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_HEADER_BORDER));
        contentPane.add(headerPanel, BorderLayout.NORTH);

        JLabel title = new JLabel("Koltuk Seçimi: " + movieTitle + "  |  " + selectedSession);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(20, 2, 600, 30);
        headerPanel.add(title);

        JLabel sessionLbl = new JLabel(selectedSession);
        sessionLbl.setForeground(Color.LIGHT_GRAY);
        sessionLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sessionLbl.setBounds(20, 28, 400, 20);
        headerPanel.add(sessionLbl);

        JLabel close = new JLabel("X");
        close.setForeground(Color.WHITE);
        close.setFont(new Font("Segoe UI", Font.BOLD, 18));
        close.setHorizontalAlignment(SwingConstants.CENTER);
        close.setBounds(900, 0, 50, 50);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
            public void mouseEntered(MouseEvent e) { close.setForeground(COLOR_ACCENT); }
            public void mouseExited(MouseEvent e) { close.setForeground(Color.WHITE); }
        });
        headerPanel.add(close);

        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });
    }

    // --- 2. SAHNE VE KOLTUKLAR ---
    private void initStage() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setLayout(null);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Perde Çizimi
        JPanel screenPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 40),
                        0, getHeight(), new Color(33, 33, 33, 0));
                g2.setPaint(gp);

                int[] xPoints = {20, getWidth() - 20, getWidth(), 0};
                int[] yPoints = {0, 0, getHeight(), getHeight()};
                g2.fillPolygon(xPoints, yPoints, 4);

                g2.setColor(new Color(100, 100, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(20, 0, getWidth() - 20, 0);
            }
        };
        screenPanel.setOpaque(false);
        screenPanel.setBounds(175, 30, 600, 60);
        centerPanel.add(screenPanel);

        JLabel lblScreenText = new JLabel("P  E  R  D  E");
        lblScreenText.setHorizontalAlignment(SwingConstants.CENTER);
        lblScreenText.setForeground(Color.GRAY);
        lblScreenText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblScreenText.setBounds(175, 95, 600, 20);
        centerPanel.add(lblScreenText);

        // Koltuk Gridi
        JPanel seatGrid = new JPanel();
        seatGrid.setBackground(COLOR_BG);
        seatGrid.setBounds(125, 140, 700, 380);
        seatGrid.setLayout(new GridLayout(6, 8, 12, 12));

        char[] rows = {'A', 'B', 'C', 'D', 'E', 'F'};
        for (char row : rows) {
            for (int i = 1; i <= 8; i++) {
                String seatNo = row + "" + i;
                seatGrid.add(createModernSeat(seatNo));
            }
        }
        centerPanel.add(seatGrid);
    }

    // --- 3. FOOTER (GÜNCELLENEN KISIM) ---
    private void initFooter() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(COLOR_BG);
        footerPanel.setPreferredSize(new Dimension(950, 120));
        footerPanel.setLayout(null);
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        createLegendItem(footerPanel, COLOR_SEAT_EMPTY, "Boş", 50);
        createLegendItem(footerPanel, COLOR_SEAT_FULL, "Dolu", 130);
        createLegendItem(footerPanel, COLOR_SEAT_SELECTED, "Seçili", 210);

        String[] sessions = {
                "Bugün - 18:00",
                "Bugün - 20:00",
                "Bugün - 22:00",
                "Yarın - 18:00",
                "Yarın - 20:00",
                "Yarın - 22:00"
        };

        cmbSession = new JComboBox<>(sessions);
        cmbSession.setBounds(320, 45, 250, 30);
        cmbSession.setBackground(new Color(45,45,45));
        cmbSession.setForeground(Color.WHITE);
        cmbSession.setFocusable(false);
        footerPanel.add(cmbSession);
        selectedSession = (String) cmbSession.getSelectedItem();

        cmbSession.addActionListener(e -> {
            selectedSession = (String) cmbSession.getSelectedItem();
            updateSummaryLabel();
        });

        lblSummary = new JLabel();
        lblSummary.setForeground(Color.LIGHT_GRAY);
        lblSummary.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSummary.setBounds(320, 15, 560, 25);
        footerPanel.add(lblSummary);

        updateSummaryLabel();

        JButton btnConfirm = new JButton("SEÇİMİ ONAYLA");
        btnConfirm.setBounds(700, 35, 200, 40);
        btnConfirm.setBackground(COLOR_ACCENT);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- PaymentFrame'e Bağlama ---
        btnConfirm.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen en az bir koltuk seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            } else {
                Collections.sort(selectedSeats);

                // Seçilen koltukları PaymentFrame'e gönderiyoruz
                // NOT: PaymentFrame constructor'ı ArrayList bekliyorsa List'i çeviriyoruz.
                PaymentFrame paymentFrame = new PaymentFrame(movieTitle, new ArrayList<>(selectedSeats), selectedSession);
                paymentFrame.setVisible(true);

                // Koltuk ekranını kapat
                dispose();
            }
        });
        footerPanel.add(btnConfirm);
    }

    // --- ÖZEL METOT: MODERN KOLTUK YARATICI ---
    private JToggleButton createModernSeat(String seatNo) {
        boolean isSold = false; // DB entegrasyonunda burası değişecek

        JToggleButton btn = new JToggleButton(seatNo) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                Color fillColor;
                if (isSold) {
                    fillColor = COLOR_SEAT_FULL;
                } else if (isSelected()) {
                    fillColor = COLOR_SEAT_SELECTED;
                } else {
                    fillColor = getModel().isRollover() ? COLOR_SEAT_EMPTY.brighter() : COLOR_SEAT_EMPTY;
                }

                g2.setColor(fillColor);
                g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, h - 4, 12, 12));

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(getText())) / 2;
                int textY = (h + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        if (isSold) {
            btn.setEnabled(false);
        } else {
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                if (btn.isSelected()) selectedSeats.add(seatNo);
                else selectedSeats.remove(seatNo);
                updateSummaryLabel();
                btn.repaint();
            });
        }
        return btn;
    }

    private void createLegendItem(JPanel panel, Color color, String text, int x) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 18, 18, 6, 6);
            }
        };
        box.setOpaque(false);
        box.setBounds(x, 30, 20, 20);
        panel.add(box);

        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setBounds(x + 25, 30, 50, 20);
        panel.add(lbl);
    }

    private void updateSummaryLabel() {
        if (selectedSeats.isEmpty()) {
            lblSummary.setText("Koltuk seçilmedi");
        } else {
            int count = selectedSeats.size();
            double total = count * TICKET_PRICE;

            lblSummary.setText(
                    "Seçilen: " + String.join(", ", selectedSeats)
                            + " | " + count + " koltuk"
                            + " | " + total + " TL"
            );
        }
    }

}
