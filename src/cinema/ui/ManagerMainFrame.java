package cinema.ui;

import cinema.model.content.*;
import cinema.service.AuthService;
import cinema.service.MediaService;
import cinema.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ManagerMainFrame extends JFrame {

    private final AuthService authService;
    private final MediaService mediaService;
    private final TicketService ticketService;

    private JPanel contentPane;
    private JTable tblFilms;
    private DefaultTableModel model;
    private int mouseX, mouseY;

    // Premium Renk Paleti
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_TEXT_SUB = new Color(150, 150, 150);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    public ManagerMainFrame(AuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;
        this.mediaService = new MediaService();

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 700, 30, 30));

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_BG);
        contentPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        setContentPane(contentPane);

        initHeader();
        initCenter();
        initFooter();

        loadFilmsFromDatabase();
    }

    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1100, 90));
        header.setBackground(COLOR_BG);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        // Logo
        JLabel lblLogo = new JLabel("SİNEMA");
        lblLogo.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
        lblLogo.setForeground(COLOR_ACCENT);
        lblLogo.setBounds(40, 25, 150, 40);
        header.add(lblLogo);

        JLabel lblSub = new JLabel("YÖNETİCİ PANELİ");
        lblSub.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXT_SUB);
        lblSub.setBounds(195, 32, 200, 30);
        header.add(lblSub);

        // Profil ve Çıkış Butonları
        JButton btnProfile = new JButton("PROFİL");
        btnProfile.setBounds(710, 34, 90, 32);
        styleHeaderButton(btnProfile, Color.WHITE);
        btnProfile.addActionListener(e -> new ProfileFrame(authService, ticketService).setVisible(true));
        header.add(btnProfile);

        JButton btnLogout = new JButton("ÇIKIŞ");
        btnLogout.setBounds(810, 34, 90, 32);
        styleHeaderButton(btnLogout, COLOR_ACCENT);
        btnLogout.addActionListener(e -> {
            new LoginFrame(authService, ticketService).setVisible(true);
            dispose();
        });
        header.add(btnLogout);

        // --- SAĞ ÜST KONTROL TUŞLARI (CustomerMainFrame ile aynı) ---
        JButton btnMin = new JButton("_");
        btnMin.setBounds(1000, 15, 35, 35);
        btnMin.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        styleControlBtn(btnMin, Color.WHITE);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        header.add(btnMin);

        JButton btnClose = new JButton("X");
        btnClose.setBounds(1040, 15, 35, 35);
        btnClose.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
        styleControlBtn(btnClose, COLOR_ACCENT);
        btnClose.addActionListener(e -> System.exit(0));
        header.add(btnClose);

        contentPane.add(header, BorderLayout.NORTH);
    }

    private void initCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(COLOR_BG);
        center.setBorder(new EmptyBorder(10, 40, 10, 40));

        model = new DefaultTableModel(new Object[]{"ID", "FİLM ADI", "TÜR", "SÜRE", "DURUM"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tblFilms = new JTable(model);
        styleTable(tblFilms);

        JScrollPane sp = new JScrollPane(tblFilms);
        sp.setBorder(new LineBorder(COLOR_BORDER));
        sp.getViewport().setBackground(COLOR_CARD);
        center.add(sp, BorderLayout.CENTER);

        contentPane.add(center, BorderLayout.CENTER);
    }

    private void initFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(COLOR_BG);
        footer.setPreferredSize(new Dimension(1100, 85));

        footer.add(createActionButton("SİL", COLOR_CARD, COLOR_ACCENT));
        footer.add(createActionButton("DÜZENLE", COLOR_CARD, Color.WHITE));
        footer.add(createActionButton("FİLM EKLE", COLOR_ACCENT, Color.WHITE));

        contentPane.add(footer, BorderLayout.SOUTH);
    }

    // --- STİL METOTLARI ---

    private void styleTable(JTable table) {
        table.setRowHeight(45);
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT_MAIN);
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(new Color(40, 40, 40));
        table.setSelectionForeground(COLOR_ACCENT);
        table.setShowVerticalLines(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getTableHeader().setBackground(COLOR_BG);
        table.getTableHeader().setForeground(COLOR_ACCENT);
        table.getTableHeader().setFont(new Font("Segoe UI Bold", Font.PLAIN, 13));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createActionButton(String text, Color bg, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            if (text.equals("FİLM EKLE")) onAddFilm();
            else if (text.equals("DÜZENLE")) onEditFilm();
            else if (text.equals("SİL")) onDeleteFilm();
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBorder(new LineBorder(hoverColor)); }
            public void mouseExited(MouseEvent e) { btn.setBorder(new LineBorder(COLOR_BORDER)); }
        });
        return btn;
    }

    private void styleHeaderButton(JButton btn, Color hoverColor) {
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.LIGHT_GRAY);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(55, 55, 55), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(hoverColor); btn.setBorder(new LineBorder(hoverColor)); }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.LIGHT_GRAY); btn.setBorder(new LineBorder(new Color(55, 55, 55))); }
        });
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

    // --- VERİTABANI VE KAYIT MANTIĞI ---

    private void loadFilmsFromDatabase() {
        model.setRowCount(0);
        try {
            List<Media> films = mediaService.getAllFilms();
            for (Media m : films) {
                if (m instanceof Film f) {
                    model.addRow(new Object[]{
                            f.getFilmType(), // "Premium", "Standard" vb.
                            f.getName(),
                            f.getGenre(),
                            f.getDurationMinutes() + " dk",
                            f.isVisible() ? "AKTİF" : "PASİF"
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void onAddFilm() {
        // FilmFormDialog'un Service katmanını kullanması gerekir!
        FilmFormDialog dlg = new FilmFormDialog(this, "Film Ekle", null, mediaService);
        dlg.setVisible(true);
        loadFilmsFromDatabase(); // Dialog kapandıktan sonra listeyi yenile
    }

    private void onEditFilm() {
        int r = tblFilms.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Film seçiniz!"); return; }

        String currentName = model.getValueAt(r, 1).toString();
        Media m = mediaService.getMediaByName(currentName);

        if (m instanceof Film f) {
            FilmFormDialog dlg = new FilmFormDialog(this, "Film Düzenle", f, mediaService);
            dlg.setVisible(true);
            loadFilmsFromDatabase();
        }
    }

    private void onDeleteFilm() {
        int r = tblFilms.getSelectedRow();
        if (r < 0) return;
        String title = model.getValueAt(r, 1).toString();
        if (JOptionPane.showConfirmDialog(this, title + " silinecek?", "Onay", JOptionPane.YES_NO_OPTION) == 0) {
            mediaService.deleteMedia(title);
            loadFilmsFromDatabase();
        }
    }
}