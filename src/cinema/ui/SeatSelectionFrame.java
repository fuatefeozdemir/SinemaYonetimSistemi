package cinema.ui;

import cinema.model.Session;
import cinema.model.Hall;
import cinema.model.content.Film;
import cinema.model.people.Customer;
import cinema.model.people.User;
import cinema.service.AuthService;
import cinema.service.TicketService;
import cinema.service.SessionService;
import cinema.util.ServiceContainer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionFrame extends JFrame {

    private final ServiceContainer serviceContainer;

    // UI Panelleri
    private JPanel contentPane;
    private JPanel seatGrid;

    // Veri Alanları
    private String movieTitle;
    private String targetEmail; // Biletin tanımlanacağı kullanıcı adresi
    private String initialSessionTime;

    private List<String> selectedSeats = new ArrayList<>(); // Seçilen koltuklar
    private List<String> occupiedSeats = new ArrayList<>(); // Dolu olan koltuklar

    private int mouseX, mouseY;
    private JLabel lblSummary;
    private JComboBox<String> cmbSession;

    // Koltuk boyutlar için sabitler
    private final int SEAT_SIZE = 35;
    private final int SEAT_GAP = 6;

    // Renk değişkenleri
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    // Koltuk renkleri
    private final Color COLOR_SEAT_EMPTY = new Color(40, 40, 40);
    private final Color COLOR_SEAT_FULL = new Color(120, 30, 30);
    private final Color COLOR_SEAT_SELECTED = new Color(46, 204, 113);

    // Müşterinin kendisi için açtığı varsayılan constructor
    public SeatSelectionFrame(String movieTitle, ServiceContainer serviceContainer) {
        this.movieTitle = movieTitle;
        this.targetEmail = serviceContainer.getAuthService().getCurrentUser().getEmail();
        this.serviceContainer = serviceContainer;
        setupUI();
    }

    // Kasiyerin işlem yaptığı durumlarda kullanılan constructor (Overlaoding)
    public SeatSelectionFrame(String movieTitle, String selectedSessionTime, String targetEmail, ServiceContainer serviceContainer) {
        this.movieTitle = movieTitle;
        this.initialSessionTime = selectedSessionTime;
        this.targetEmail = targetEmail;
        this.serviceContainer = serviceContainer;
        setupUI();

        if (initialSessionTime != null) {
            selectSessionInCombo(initialSessionTime);
        }
    }

    private void setupUI() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 650, 30, 30));

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
        header.setPreferredSize(new Dimension(900, 70));

        JLabel title = new JLabel(movieTitle.toUpperCase());
        title.setForeground(COLOR_ACCENT);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        title.setBounds(35, 15, 600, 40);
        header.add(title);

        JButton btnClose = new JButton("X");
        btnClose.setBounds(845, 15, 40, 35);
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

    // Perde görseli oluşturur
    private void initStage() {
        JPanel centerPanel = new JPanel(null);
        centerPanel.setBackground(COLOR_BG);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        JPanel screenPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(229, 9, 20, 40), 0, 40, COLOR_BG));
                g2.fillArc(50, 0, 700, 80, 0, 180);
                g2.setColor(new Color(60, 60, 60));
                g2.setStroke(new BasicStroke(3));
                g2.drawArc(50, 10, 700, 50, 0, 180);
                g2.dispose();
            }
        };
        screenPanel.setBounds(50, 10, 800, 70);
        screenPanel.setOpaque(false);
        centerPanel.add(screenPanel);

        seatGrid = new JPanel();
        seatGrid.setBackground(COLOR_BG);
        centerPanel.add(seatGrid);
    }

    // Comboboxa seansları yükler
    private void loadSessions() {
        List<Session> sessions = serviceContainer.getSessionService().getSessionsByMediaName(movieTitle);
        cmbSession.removeAllItems();
        for (Session s : sessions) {
            String label = s.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    + " - " + s.getHall().getHallName();
            cmbSession.addItem(label);
        }
    }

    // Seçilen seansın comboboxda seçili gelmesini sağlar
    private void selectSessionInCombo(String time) {
        for (int i = 0; i < cmbSession.getItemCount(); i++) {
            if (cmbSession.getItemAt(i).startsWith(time)) {
                cmbSession.setSelectedIndex(i);
                break;
            }
        }
    }


    // Seansın ait olduğu salondaki koltuk boyutlarına göre koltukları yerleştirir
    private void refreshSeats() {
        selectedSeats.clear();
        seatGrid.removeAll();

        String currentSessionId = findSessionId();
        Session currentSession = serviceContainer.getSessionService().getSession(currentSessionId);

        if (currentSession != null) {
            Hall hall = currentSession.getHall();
            int rows = hall.getRowCount();
            int cols = hall.getColumnCount();

            int totalWidth = (cols * SEAT_SIZE) + ((cols - 1) * SEAT_GAP);
            int totalHeight = (rows * SEAT_SIZE) + ((rows - 1) * SEAT_GAP);

            seatGrid.setBounds((900 - totalWidth) / 2, 90, totalWidth, totalHeight);
            seatGrid.setLayout(new GridLayout(rows, cols, SEAT_GAP, SEAT_GAP));

            occupiedSeats = serviceContainer.getTicketService().getOccupiedSeats(currentSessionId);

            for (int r = 0; r < rows; r++) {
                char rowLetter = (char) ('A' + r);
                for (int c = 1; c <= cols; c++) {
                    String seatNo = rowLetter + String.valueOf(c);
                    boolean isSold = occupiedSeats.contains(seatNo);
                    seatGrid.add(createSeat(seatNo, isSold));
                }
            }
        }

        updateSummary();
        seatGrid.revalidate();
        seatGrid.repaint();
    }

    // Tıklanabilir koltukları ve renkleri oluşturur
    private JToggleButton createSeat(String seatNo, boolean isSold) {
        JToggleButton btn = new JToggleButton(seatNo) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSold) g2.setColor(COLOR_SEAT_FULL);
                else if (isSelected()) g2.setColor(COLOR_SEAT_SELECTED);
                else g2.setColor(COLOR_SEAT_EMPTY);

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Bold", Font.PLAIN, 9));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent())/2 - 2);
                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(SEAT_SIZE, SEAT_SIZE));
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

    // Renk açıklamalarının ve diğer tuşların bulunduğu alt bar
    private void initFooter() {
        JPanel footer = new JPanel(null);
        footer.setBackground(COLOR_CARD);
        footer.setPreferredSize(new Dimension(900, 140));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        legend.setOpaque(false);
        legend.setBounds(0, 5, 900, 35);
        legend.add(new LegendItem(COLOR_SEAT_EMPTY, "BOŞ"));
        legend.add(new LegendItem(COLOR_SEAT_FULL, "DOLU"));
        legend.add(new LegendItem(COLOR_SEAT_SELECTED, "SEÇİLİ"));
        footer.add(legend);

        cmbSession = new JComboBox<>();
        cmbSession.setBounds(40, 65, 250, 35);
        cmbSession.setBackground(COLOR_BG);
        cmbSession.setForeground(Color.WHITE);
        cmbSession.addActionListener(e -> refreshSeats());
        footer.add(cmbSession);

        lblSummary = new JLabel("Koltuk seçimi bekleniyor...");
        lblSummary.setForeground(COLOR_TEXT_SUB);
        lblSummary.setFont(new Font("Segoe UI Black", Font.PLAIN, 15));
        lblSummary.setBounds(320, 65, 300, 35);
        footer.add(lblSummary);

        JButton btnConfirm = new JButton("ÖDEMEYE GEÇ →");
        btnConfirm.setBounds(680, 60, 180, 45);
        btnConfirm.setBackground(COLOR_ACCENT);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI Black", Font.PLAIN, 13));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen en az bir koltuk seçin!");
                return;
            }
            new PaymentFrame(movieTitle, new ArrayList<>(selectedSeats), findSessionId(), targetEmail, serviceContainer).setVisible(true);
            dispose();
        });
        footer.add(btnConfirm);

        contentPane.add(footer, BorderLayout.SOUTH);
    }

    // Bilet fiyatına göre toplam tutarı günceller
    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            lblSummary.setText("Koltuk seçimi bekleniyor...");
            lblSummary.setForeground(COLOR_TEXT_SUB);
        } else {
            String sessionId = findSessionId();
            Session session = serviceContainer.getSessionService().getSession(sessionId);
            double total = selectedSeats.size() * 100.0;

            if (session != null && session.getFilm() instanceof Film film) {
                boolean isDiscounted = checkDiscountStatus(serviceContainer.getAuthService().getCurrentUser());
                total = selectedSeats.size() * film.calculatePrice(isDiscounted);
            }
            lblSummary.setText(selectedSeats.size() + " Koltuk | " + String.format("%.2f", total) + " TL");
            lblSummary.setForeground(Color.WHITE);
        }
    }

    // İndirim olup olmama durumunu kontrol eder
    private boolean checkDiscountStatus(User user) {
        if (user instanceof Customer customer && customer.getDateOfBirth() != null) {
            return ChronoUnit.YEARS.between(customer.getDateOfBirth(), LocalDate.now()) < 18;
        }
        return false;
    }

    // Comboboxda seçili seansın idsini bulur
    private String findSessionId() {
        List<Session> sessions = serviceContainer.getSessionService().getSessionsByMediaName(movieTitle);
        Object selected = cmbSession.getSelectedItem();
        if (selected == null) return "";

        for (Session s : sessions) {
            String label = s.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    + " - " + s.getHall().getHallName();
            if (label.equals(selected.toString())) return s.getSessionId();
        }
        return "";
    }

    private void styleControlBtn(JButton btn, Color hover) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(COLOR_TEXT_SUB);
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
                    g.setColor(color);
                    g.fillRoundRect(0,0,12,12,3,3);
                }
            };
            box.setPreferredSize(new Dimension(12, 12));
            add(box);
            JLabel l = new JLabel(text);
            l.setForeground(COLOR_TEXT_SUB);
            l.setFont(new Font("Segoe UI Bold", Font.PLAIN, 10));
            add(l);
        }
    }
}