package cinema.ui;

import cinema.model.Session;
import cinema.service.AuthService;
import cinema.service.TicketService;
import cinema.service.SessionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;

    private JPanel contentPane;
    private JPanel seatGrid;
    private String movieTitle;
    private String targetEmail;
    private List<String> selectedSeats = new ArrayList<>();
    private List<String> occupiedSeats = new ArrayList<>();
    private int mouseX, mouseY;
    private JLabel lblSummary;
    private JComboBox<String> cmbSession;

    // Premium Renk Paleti
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    private final Color COLOR_SEAT_EMPTY = new Color(40, 40, 40);
    private final Color COLOR_SEAT_FULL = new Color(120, 30, 30);
    private final Color COLOR_SEAT_SELECTED = new Color(46, 204, 113); // Yeşil seçim

    private final double TICKET_PRICE = 150.0;

    public SeatSelectionFrame(String movieTitle, AuthService authService, TicketService ticketService) {
        this.movieTitle = movieTitle;
        this.authService = authService;
        this.ticketService = ticketService;
        this.targetEmail = authService.getCurrentUser().getEmail();
        setupUI();
    }

    public SeatSelectionFrame(String movieTitle, String selectedSessionTime, String targetEmail,
                              AuthService authService, TicketService ticketService) {
        this.movieTitle = movieTitle;
        this.authService = authService;
        this.ticketService = ticketService;
        this.targetEmail = targetEmail;
        setupUI();
        if (selectedSessionTime != null) cmbSession.setSelectedItem(selectedSessionTime);
    }

    private void setupUI() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 800, 30, 30));

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(contentPane);

        initHeader();
        initStage();
        initFooter();

        loadSessions();
        refreshSeats();
    }

    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setBackground(COLOR_BG);
        header.setPreferredSize(new Dimension(1000, 80));

        JLabel title = new JLabel(movieTitle.toUpperCase());
        title.setForeground(COLOR_ACCENT);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setBounds(40, 20, 600, 40);
        header.add(title);

        // Kontroller
        JButton btnMin = new JButton("_");
        btnMin.setBounds(900, 15, 40, 35);
        btnMin.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        header.add(btnMin);

        JButton btnClose = new JButton("X");
        btnClose.setBounds(945, 15, 40, 35);
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

        contentPane.add(header, BorderLayout.NORTH);
    }

    private void initStage() {
        JPanel centerPanel = new JPanel(null);
        centerPanel.setBackground(COLOR_BG);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Modern Işıklı Perde
        JPanel screenPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Perde Işığı (Gradient)
                GradientPaint gp = new GradientPaint(0, 0, new Color(229, 9, 20, 40), 0, 50, new Color(10, 10, 10, 0));
                g2.setPaint(gp);
                g2.fillArc(50, 0, 700, 100, 0, 180);

                g2.setColor(new Color(60, 60, 60));
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(50, 10, 700, 60, 0, 180);

                g2.setFont(new Font("Segoe UI Bold", Font.PLAIN, 12));
                g2.setColor(COLOR_TEXT_SUB);
                g2.drawString("PERDE / SCREEN", 355, 60);
                g2.dispose();
            }
        };
        screenPanel.setBounds(100, 20, 800, 80);
        screenPanel.setOpaque(false);
        centerPanel.add(screenPanel);

        // Koltuk Izgarası
        seatGrid = new JPanel(new GridLayout(6, 10, 12, 12));
        seatGrid.setBackground(COLOR_BG);
        seatGrid.setBounds(125, 120, 750, 380);
        centerPanel.add(seatGrid);
    }

    private void loadSessions() {
        // TicketService içindeki metodun adını projenle uyumlu hale getirmelisin (Örn: getSessionsByMediaName)
        List<Session> sessions = SessionService.getSessionsByMediaName(movieTitle);
        cmbSession.removeAllItems();
        for (Session s : sessions) {
            cmbSession.addItem(s.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }
    }

    private void refreshSeats() {
        selectedSeats.clear();
        seatGrid.removeAll();
        String currentSessionId = findSessionId();

        if (!currentSessionId.isEmpty()) {
            occupiedSeats = ticketService.getOccupiedSeats(currentSessionId);
        }

        for (char row = 'A'; row <= 'F'; row++) {
            for (int i = 1; i <= 10; i++) {
                String seatNo = row + String.valueOf(i);
                boolean isSold = occupiedSeats.contains(seatNo);
                seatGrid.add(createSeat(seatNo, isSold));
            }
        }
        updateSummary();
        seatGrid.revalidate();
        seatGrid.repaint();
    }

    private JToggleButton createSeat(String seatNo, boolean isSold) {
        JToggleButton btn = new JToggleButton(seatNo) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSold) g2.setColor(COLOR_SEAT_FULL);
                else if (isSelected()) g2.setColor(COLOR_SEAT_SELECTED);
                else g2.setColor(COLOR_SEAT_EMPTY);

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(isSold ? new Color(255,255,255,50) : Color.WHITE);
                g2.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent())/2 - 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isSold) {
            btn.setEnabled(false);
        } else {
            btn.addActionListener(e -> {
                if (btn.isSelected()) selectedSeats.add(seatNo);
                else selectedSeats.remove(seatNo);
                updateSummary();
            });
        }
        return btn;
    }

    private void initFooter() {
        JPanel footer = new JPanel(null);
        footer.setBackground(COLOR_CARD);
        footer.setPreferredSize(new Dimension(1000, 180));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        legend.setOpaque(false);
        legend.setBounds(0, 5, 1000, 45);
        legend.add(new LegendItem(COLOR_SEAT_EMPTY, "BOŞ"));
        legend.add(new LegendItem(COLOR_SEAT_FULL, "DOLU"));
        legend.add(new LegendItem(COLOR_SEAT_SELECTED, "SEÇİLİ"));
        footer.add(legend);

        // Seans Seçimi
        JLabel lblSes = new JLabel("SEANS SEÇİN:");
        lblSes.setForeground(COLOR_ACCENT);
        lblSes.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        lblSes.setBounds(50, 60, 200, 20);
        footer.add(lblSes);

        cmbSession = new JComboBox<>();
        cmbSession.setBounds(50, 85, 250, 40);
        cmbSession.setBackground(COLOR_BG);
        cmbSession.setForeground(Color.WHITE);
        cmbSession.setBorder(new LineBorder(COLOR_BORDER));
        cmbSession.addActionListener(e -> refreshSeats());
        footer.add(cmbSession);

        // Özet
        lblSummary = new JLabel("Koltuk seçimi bekleniyor...");
        lblSummary.setForeground(COLOR_TEXT_MAIN);
        lblSummary.setFont(new Font("Segoe UI Bold", Font.PLAIN, 16));
        lblSummary.setBounds(400, 85, 300, 40);
        footer.add(lblSummary);

        // Buton
        JButton btnConfirm = new JButton("ÖDEMEYE GEÇ →");
        btnConfirm.setBounds(750, 80, 200, 50);
        btnConfirm.setBackground(COLOR_ACCENT);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI Black", Font.PLAIN, 14));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen koltuk seçimi yapın!");
                return;
            }
            new PaymentFrame(movieTitle, new ArrayList<>(selectedSeats), findSessionId(),
                    authService, ticketService, targetEmail).setVisible(true);
            dispose();
        });
        footer.add(btnConfirm);

        contentPane.add(footer, BorderLayout.SOUTH);
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            lblSummary.setText("Koltuk seçimi bekleniyor...");
            lblSummary.setForeground(COLOR_TEXT_SUB);
        } else {
            lblSummary.setText(selectedSeats.size() + " Bilet | " + (selectedSeats.size() * TICKET_PRICE) + " TL");
            lblSummary.setForeground(Color.WHITE);
        }
    }

    private String findSessionId() {
        List<Session> sessions = SessionService.getSessionsByMediaName(movieTitle);
        Object selected = cmbSession.getSelectedItem();
        if (selected == null) return "";

        for (Session s : sessions) {
            String timeStr = s.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (timeStr.equals(selected.toString())) return s.getSessionId();
        }
        return "";
    }

    private void styleControlBtn(JButton btn, Color hover) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setForeground(COLOR_TEXT_SUB);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(hover); }
            public void mouseExited(MouseEvent e) { btn.setForeground(COLOR_TEXT_SUB); }
        });
    }

    private class LegendItem extends JPanel {
        public LegendItem(Color color, String text) {
            setOpaque(false);
            JPanel box = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.fillRoundRect(0,0,14,14,4,4);
                }
            };
            box.setPreferredSize(new Dimension(14, 14));
            add(box);
            JLabel l = new JLabel(text);
            l.setForeground(COLOR_TEXT_SUB);
            l.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
            add(l);
        }
    }
}