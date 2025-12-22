package cinema.ui;

import cinema.model.content.*;
import cinema.model.*;
import cinema.service.*;
import cinema.util.ServiceContainer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ManagerMainFrame extends JFrame {

    // Servis Katmanları
    private final ServiceContainer serviceContainer;

    // UI Bileşenleri
    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private JTable tblFilms, tblSessions;
    private DefaultTableModel filmModel, sessionModel;

    private int mouseX, mouseY;

    // Renk değişkenleri
    private final Color COLOR_BG = new Color(10, 10, 10);
    private final Color COLOR_CARD = new Color(22, 22, 22);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT_MAIN = new Color(245, 245, 245);
    private final Color COLOR_BORDER = new Color(35, 35, 35);

    public ManagerMainFrame(ServiceContainer serviceContainer) {
        this.serviceContainer = serviceContainer;

        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabsOverlapBorder", true);
        UIManager.put("TabbedPane.shadow", COLOR_BG);
        UIManager.put("TabbedPane.darkShadow", COLOR_BG);
        UIManager.put("TabbedPane.light", COLOR_BG);
        UIManager.put("TabbedPane.highlight", COLOR_BG);

        setUndecorated(true);
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

        refreshAllTables(); // Verileri ilk açılışta yükle
    }

    // Logo, Başlık ve Çıkış butonunu içeren üst bar
    private void initHeader() {
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1100, 85));
        header.setBackground(COLOR_BG);

        // Sürükle bırak özelliği
        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY); }
        });

        JLabel lblLogo = new JLabel("SİNEMA");
        lblLogo.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        lblLogo.setForeground(COLOR_ACCENT);
        lblLogo.setBounds(40, 22, 150, 40);
        header.add(lblLogo);

        JButton btnLogout = new JButton("ÇIKIŞ");
        btnLogout.setBounds(850, 30, 90, 32);
        styleHeaderButton(btnLogout, COLOR_ACCENT);
        btnLogout.addActionListener(e -> {
            new LoginFrame(serviceContainer).setVisible(true);
            dispose();
        });
        header.add(btnLogout);

        contentPane.add(header, BorderLayout.NORTH);
    }

    // Sekmeli yapının ve tabloların bulunduğu orta bölüm
    private void initCenter() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(true);
        tabbedPane.setBackground(COLOR_BG);
        tabbedPane.setForeground(Color.GRAY);
        tabbedPane.setFont(new Font("Segoe UI Bold", Font.PLAIN, 14));
        tabbedPane.setBorder(null);

        // Film Listesi
        filmModel = new DefaultTableModel(new Object[]{"TÜR", "FİLM ADI", "KATEGORİ", "SÜRE"}, 0);
        tblFilms = new JTable(filmModel);
        setupTab(tabbedPane, tblFilms, "FİLMLER");

        // Seans Listesi
        sessionModel = new DefaultTableModel(new Object[]{"FİLM", "SALON", "BAŞLANGIÇ"}, 0);
        tblSessions = new JTable(sessionModel);
        setupTab(tabbedPane, tblSessions, "SEANSLAR");

        contentPane.add(tabbedPane, BorderLayout.CENTER);
    }

    // Tabloyu scroll paneline sarıp sekmeye ekleyen yardımcı metot
    private void setupTab(JTabbedPane tp, JTable table, String title) {
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(COLOR_CARD);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sp.setBackground(COLOR_BG);
        sp.setOpaque(true);
        tp.addTab(title, sp);
    }

    // Ekle, Düzenle, Sil butonlarının bulunduğu alt panel
    private void initFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(COLOR_BG);
        footer.setPreferredSize(new Dimension(1100, 75));

        footer.add(createActionButton("EKLE", COLOR_ACCENT));
        footer.add(createActionButton("DÜZENLE", COLOR_CARD));
        footer.add(createActionButton("SİL", COLOR_CARD));

        contentPane.add(footer, BorderLayout.SOUTH);
    }

    // İşlem butonlarını standart hale getiren metot
    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(COLOR_BORDER));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            int tab = tabbedPane.getSelectedIndex();
            if ("EKLE".equals(text)) {
                if (tab == 0) onAddFilm(); else onAddSession();
            } else if ("DÜZENLE".equals(text)) {
                onEdit(tab);
            } else if ("SİL".equals(text)) {
                onDelete(tab);
            }
        });
        return btn;
    }

    // Tüm veri tablolarını günceller
    private void refreshAllTables() {
        loadFilms();
        loadSessions();
    }

    // Veritabanındaki filmleri tabloya yansıtır
    private void loadFilms() {
        filmModel.setRowCount(0);
        serviceContainer.getMediaService().getAllFilms().forEach(m -> {
            if (m instanceof Film f) {
                filmModel.addRow(new Object[]{f.getFilmType(), f.getName(), f.getGenre(), f.getDurationMinutes() + " dk"});
            }
        });
    }

    // Aktif seansları tabloya yansıtır
    private void loadSessions() {
        sessionModel.setRowCount(0);
        serviceContainer.getMediaService().getAllFilms().forEach(m -> {
            List<Session> filmSessions = serviceContainer.getSessionService().getSessionsByMediaName(m.getName());
            if (filmSessions != null) {
                filmSessions.forEach(s -> sessionModel.addRow(new Object[]{
                        s.getFilm().getName(),
                        s.getHall().getHallName(),
                        s.getStartTime().toString().replace("T", " ")
                }));
            }
        });
    }

    // Seçili satırdaki veriyi düzenlemek için ilgili formu açar
    private void onEdit(int tabIndex) {
        int r = getSelectedRowForTab(tabIndex);
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlemek için bir kayıt seçin!");
            return;
        }

        if (tabIndex == 0) {
            String name = filmModel.getValueAt(r, 1).toString();
            Film f = (Film) serviceContainer.getMediaService().getMediaByName(name);
            FilmFormDialog dialog = new FilmFormDialog(this, "Düzenle", f, serviceContainer.getMediaService());
            dialog.setModal(true);
            dialog.setVisible(true);
            refreshAllTables();
        } else if (tabIndex == 1) {
            onDelete(1); // Mevcut seansı silip yenisini tanımlatarak güncelleme yapıyoruz
            onAddSession();
        }
    }

    // Seçili kaydı kullanıcı onayı ile sistemden kaldırır
    private void onDelete(int tabIndex) {
        int r = getSelectedRowForTab(tabIndex);
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek kaydı seçin!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Seçili kayıt silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (tabIndex == 0) {
                    String filmName = filmModel.getValueAt(r, 1).toString();
                    serviceContainer.getMediaService().deleteMedia(filmName);
                } else {
                    // DÜZELTME: Tablodaki seans verilerini alıp servis katmanına gönderiyoruz
                    String film = sessionModel.getValueAt(r, 0).toString();
                    String hall = sessionModel.getValueAt(r, 1).toString();
                    String time = sessionModel.getValueAt(r, 2).toString();
                    serviceContainer.getSessionService().deleteSession(film, hall, time);
                }
                refreshAllTables(); // Her durumda tabloları yeniliyoruz
            } catch (Exception ex) {
                String errorMsg = ex.getMessage();
                if (errorMsg != null && (errorMsg.contains("Referential integrity") || errorMsg.contains("CONSTRAINT"))) {
                    JOptionPane.showMessageDialog(this,
                            "İŞLEM ENGELLENDİ!\n\n" +
                                    "Bu kayda bağlı başka veriler (Biletler veya Seanslar) bulunuyor.\n" +
                                    "Lütfen önce bağlı verileri temizleyin.",
                            "Veritabanı Kısıtlaması", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Beklenmedik bir hata oluştu: " + ex.getMessage());
                }
            }
        }
    }

    private int getSelectedRowForTab(int tabIndex) {
        JTable currentTable = (tabIndex == 0) ? tblFilms : tblSessions;
        int viewIndex = currentTable.getSelectedRow();
        if (viewIndex != -1) {
            return currentTable.convertRowIndexToModel(viewIndex);
        }
        return -1;
    }

    private void onAddFilm() {
        FilmFormDialog dialog = new FilmFormDialog(this, "Film Ekle", null, serviceContainer.getMediaService());
        dialog.setModal(true);
        dialog.setVisible(true);
        refreshAllTables();
    }

    private void onAddSession() {
        SessionFormDialog dialog = new SessionFormDialog(this, serviceContainer);
        dialog.setModal(true);
        dialog.setVisible(true);
        refreshAllTables();
    }

    // Tablo görünümünü (satır boyu, renkler vb.) özelleştirir
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT_MAIN);
        table.setGridColor(COLOR_BORDER);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(45, 45, 45));
        table.setSelectionForeground(COLOR_ACCENT);
        table.setShowVerticalLines(false);

        table.getTableHeader().setBackground(COLOR_BG);
        table.getTableHeader().setForeground(COLOR_ACCENT);
        table.getTableHeader().setFont(new Font("Segoe UI Bold", Font.PLAIN, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void styleHeaderButton(JButton btn, Color hover) {
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.GRAY);
        btn.setBorder(new LineBorder(COLOR_BORDER));
    }
}