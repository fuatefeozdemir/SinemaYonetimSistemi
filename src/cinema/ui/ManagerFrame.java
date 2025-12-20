package cinema.ui;
import cinema.service.AuthService;

import cinema.service.FilmStore;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManagerFrame extends JFrame {

    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_PANEL = new Color(45, 45, 45);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_MUTED = new Color(150, 150, 150);

    private JTable tblFilms;
    private DefaultTableModel model;
    private final AuthService authService;

    public ManagerFrame(AuthService authService) {
        this.authService = authService;

        setTitle("Manager Panel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createCenter(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        seedMockData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(950, 55));
        header.setBackground(COLOR_BG);

        JLabel title = new JLabel("YÖNETİCİ PANELİ - Film Yönetimi");
        title.setForeground(COLOR_TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(20, 10, 500, 35);
        header.add(title);

        JButton btnLogout = new JButton("Çıkış");
        btnLogout.setBounds(840, 12, 80, 30);
        btnLogout.setBackground(COLOR_PANEL);
        btnLogout.setForeground(COLOR_TEXT);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            // İstersen burada LoginFrame’e dönersin:
            // new LoginFrame().setVisible(true);
            dispose();
        });
        header.add(btnLogout);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 60));
        header.add(sep);

        return header;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(COLOR_BG);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_PANEL);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lbl = new JLabel("Filmler");
        lbl.setForeground(COLOR_TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.add(lbl, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Film Adı", "Tür", "Süre (dk)", "Durum"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tablo üzerinden edit yok
            }
        };

        tblFilms = new JTable(model);
        tblFilms.setRowHeight(28);
        tblFilms.setBackground(new Color(38, 38, 38));
        tblFilms.setForeground(COLOR_TEXT);
        tblFilms.setGridColor(new Color(70, 70, 70));
        tblFilms.setSelectionBackground(COLOR_ACCENT);
        tblFilms.setSelectionForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(tblFilms);
        sp.getViewport().setBackground(new Color(38, 38, 38));
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        card.add(sp, BorderLayout.CENTER);

        center.add(card, BorderLayout.CENTER);
        return center;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(null);
        footer.setPreferredSize(new Dimension(950, 80));
        footer.setBackground(COLOR_BG);

        JButton btnAdd = primaryButton("Film Ekle");
        btnAdd.setBounds(20, 20, 140, 40);
        btnAdd.addActionListener(e -> onAddFilm());
        footer.add(btnAdd);

        JButton btnEdit = secondaryButton("Düzenle");
        btnEdit.setBounds(170, 20, 140, 40);
        btnEdit.addActionListener(e -> onEditFilm());
        footer.add(btnEdit);

        JButton btnDelete = secondaryButton("Sil");
        btnDelete.setBounds(320, 20, 140, 40);
        btnDelete.addActionListener(e -> onDeleteFilm());
        footer.add(btnDelete);

        JLabel hint = new JLabel("Not: Şimdilik mock data. DB bağlanınca burayı servis ile dolduracağız.");
        hint.setForeground(COLOR_MUTED);
        hint.setBounds(500, 28, 430, 20);
        footer.add(hint);

        return footer;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(COLOR_ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(COLOR_PANEL);
        b.setForeground(COLOR_TEXT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void onAddFilm() {
        FilmFormDialog dlg = new FilmFormDialog(this, "Film Ekle", null);
        dlg.setVisible(true);

        Object[] row = dlg.getResultRow();
        if (row != null) {
            model.addRow(row);

            FilmStore.addFilm(row[1].toString());
        }
    }

    private void onEditFilm() {
        int r = tblFilms.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Düzenlemek için bir film seç.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] current = new Object[]{
                model.getValueAt(r, 0),
                model.getValueAt(r, 1),
                model.getValueAt(r, 2),
                model.getValueAt(r, 3),
                model.getValueAt(r, 4)
        };

        FilmFormDialog dlg = new FilmFormDialog(this, "Film Düzenle", current);
        dlg.setVisible(true);

        Object[] updated = dlg.getResultRow();
        if (updated != null) {
            for (int c = 0; c < updated.length; c++) model.setValueAt(updated[c], r, c);
        }
    }

    private void onDeleteFilm() {
        int r = tblFilms.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Silmek için bir film seç.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Seçili filmi silmek istiyor musun?", "Onay", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) model.removeRow(r);
    }

    private void seedMockData() {
        model.addRow(new Object[]{1, "Interstellar", "Bilim Kurgu", 169, "AKTİF"});
        model.addRow(new Object[]{2, "Inception", "Bilim Kurgu", 148, "AKTİF"});
        model.addRow(new Object[]{3, "Joker", "Dram", 122, "PASİF"});
    }
}
