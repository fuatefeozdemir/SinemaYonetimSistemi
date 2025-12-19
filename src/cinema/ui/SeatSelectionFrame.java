package cinema.ui;

import cinema.model.Session;
import cinema.service.AuthService;
import cinema.service.TicketService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.CubicCurve2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionFrame extends JFrame {

    private final AuthService authService;
    private final TicketService ticketService;

    private JPanel contentPane;
    private JPanel seatGrid;
    private String movieTitle;
    private List<String> selectedSeats = new ArrayList<>();
    private List<String> occupiedSeats = new ArrayList<>();
    private int mouseX, mouseY;
    private JLabel lblSummary;
    private final double TICKET_PRICE = 150.0;
    private JComboBox<String> cmbSession;

    private final Color COLOR_BG = new Color(25, 25, 25);
    private final Color COLOR_HEADER_BORDER = new Color(45, 45, 45);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_CARD = new Color(229, 9, 20);

    private final Color COLOR_SEAT_EMPTY = new Color(60, 60, 60);
    private final Color COLOR_SEAT_FULL = new Color(120, 30, 30);
    private final Color COLOR_SEAT_SELECTED = new Color(46, 204, 113);

    public SeatSelectionFrame(String movieTitle, AuthService authService, TicketService ticketService) {
        this.movieTitle = movieTitle;
        this.authService = authService;
        this.ticketService = ticketService;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(BorderFactory.createLineBorder(COLOR_HEADER_BORDER, 1));
        setContentPane(contentPane);

        initHeader();
        initStage();
        initFooter();

        // 1. Önce veritabanındaki seansları ComboBox'a yükle
        loadSessions();
        // 2. İlk seansa göre koltukları tazele
        refreshSeats();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setPreferredSize(new Dimension(950, 70));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_HEADER_BORDER));

        // Sürükleme desteği
        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        JLabel title = new JLabel("🎬 " + movieTitle);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(25, 0, 500, 70);
        headerPanel.add(title);

        String name = (authService.getCurrentUser() != null) ? authService.getCurrentUser().getFirstName() : "Misafir";
        JLabel lblUser = new JLabel("Müşteri: " + name);
        lblUser.setForeground(Color.GRAY);
        lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUser.setBounds(650, 0, 200, 70);
        headerPanel.add(lblUser);

        JLabel close = new JLabel("✕");
        close.setForeground(Color.GRAY);
        close.setFont(new Font("Segoe UI", Font.BOLD, 22));
        close.setHorizontalAlignment(SwingConstants.CENTER);
        close.setBounds(890, 0, 50, 70);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); }
        });

        headerPanel.add(close);
        contentPane.add(headerPanel, BorderLayout.NORTH);
    }

    private void initStage() {
        JPanel centerPanel = new JPanel(null);
        centerPanel.setBackground(COLOR_BG);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Perde Tasarımı
        JPanel screenPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 20), 0, 100, new Color(25, 25, 25, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 15, getWidth(), 100);
                g2.setStroke(new BasicStroke(4));
                g2.setColor(new Color(200, 200, 200));
                CubicCurve2D curve = new CubicCurve2D.Float(50, 20, 300, 0, 500, 0, 750, 20);
                g2.draw(curve);
                g2.drawString("PERDE / SCREEN", 350, 45);
            }
        };
        screenPanel.setOpaque(false);
        screenPanel.setBounds(75, 10, 800, 80);
        centerPanel.add(screenPanel);

        seatGrid = new JPanel(new GridLayout(6, 10, 12, 12));
        seatGrid.setBackground(COLOR_BG);
        seatGrid.setBounds(100, 100, 750, 380);
        centerPanel.add(seatGrid);
    }

    private void loadSessions() {
        // TicketRepository'den bu film ismine ait seansları çekiyoruz
        List<Session> sessions = ticketService.getSessionsForMovie(movieTitle);
        cmbSession.removeAllItems();
        for (Session session : sessions) {
            cmbSession.addItem(session.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }
    }

    private void refreshSeats() {
        selectedSeats.clear();
        seatGrid.removeAll();

        String currentSession = findSessionId();

        if (currentSession != null) {
            // Veritabanından seçili seansa ve filme ait dolu koltukları çek
            occupiedSeats = ticketService.getOccupiedSeats(currentSession);
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
                Color c = isSold ? COLOR_SEAT_FULL : (isSelected() ? COLOR_SEAT_SELECTED : COLOR_SEAT_EMPTY);
                g2.setColor(c);
                g2.fillRoundRect(2, 2, getWidth()-6, getHeight()-6, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent())/2 - 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
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
        JPanel footerPanel = new JPanel(null);
        footerPanel.setBackground(new Color(30, 30, 30));
        footerPanel.setPreferredSize(new Dimension(950, 150));
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        legendPanel.setOpaque(false);
        legendPanel.setBounds(0, 5, 950, 40);
        legendPanel.add(new LegendItem(COLOR_SEAT_EMPTY, "BOŞ"));
        legendPanel.add(new LegendItem(COLOR_SEAT_FULL, "DOLU"));
        legendPanel.add(new LegendItem(COLOR_SEAT_SELECTED, "SEÇİLİ"));
        footerPanel.add(legendPanel);

        lblSummary = new JLabel("Henüz koltuk seçilmedi");
        lblSummary.setForeground(Color.LIGHT_GRAY);
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSummary.setBounds(50, 60, 400, 30);
        footerPanel.add(lblSummary);

        cmbSession = new JComboBox<>();
        cmbSession.setBounds(50, 95, 250, 30);
        cmbSession.setBackground(COLOR_CARD);
        cmbSession.setForeground(Color.WHITE);
        // Seans değişince koltukları DB'den yeniden oku
        cmbSession.addActionListener(e -> refreshSeats());
        footerPanel.add(cmbSession);

        JButton btnConfirm = new JButton("ÖDEMEYE GEÇ →");
        btnConfirm.setBounds(700, 70, 200, 50);
        btnConfirm.setBackground(COLOR_ACCENT);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnConfirm.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Koltuk seçmediniz!");
            } else {
                // PaymentFrame açılıyor
                new PaymentFrame(movieTitle, new ArrayList<>(selectedSeats), findSessionId(), authService, ticketService).setVisible(true);
                dispose();
            }
        });
        footerPanel.add(btnConfirm);
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) lblSummary.setText("Henüz koltuk seçilmedi");
        else lblSummary.setText(selectedSeats.size() + " Bilet | Toplam: " + (selectedSeats.size() * TICKET_PRICE) + " TL");
    }

    private class LegendItem extends JPanel {
        public LegendItem(Color color, String text) {
            setOpaque(false);
            JPanel box = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(color);
                    g.fillRoundRect(0,0,18,18,5,5);
                }
            };
            box.setPreferredSize(new Dimension(18, 18));
            add(box);
            JLabel l = new JLabel(text);
            l.setForeground(Color.LIGHT_GRAY);
            add(l);
        }
    }

    private String findSessionId() {
        List<Session> sessions = ticketService.getSessionsForMovie(movieTitle);

        for (Session session : sessions) {
            String startTime = session.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (startTime.equals(cmbSession.getSelectedItem())) {
                return session.getSessionId();
            }
        }

        return "";
    }
}