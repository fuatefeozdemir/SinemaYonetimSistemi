package cinema.ui;

import javax.swing.*;
import java.awt.*;

public class FilmFormDialog extends JDialog {

    private final Color COLOR_BG = new Color(33, 33, 33);
    private final Color COLOR_PANEL = new Color(45, 45, 45);
    private final Color COLOR_ACCENT = new Color(229, 9, 20);
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Color COLOR_MUTED = new Color(150, 150, 150);

    private JTextField txtId, txtName, txtGenre, txtDuration;
    private JComboBox<String> cmbStatus;

    private Object[] resultRow = null;

    public FilmFormDialog(JFrame owner, String title, Object[] existingRow) {
        super(owner, title, true);
        setSize(420, 340);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel root = new JPanel(null);
        root.setBackground(COLOR_BG);
        setContentPane(root);

        JPanel card = new JPanel(null);
        card.setBounds(15, 15, 375, 240);
        card.setBackground(COLOR_PANEL);
        root.add(card);

        JLabel h = new JLabel(title);
        h.setForeground(COLOR_TEXT);
        h.setFont(new Font("Segoe UI", Font.BOLD, 16));
        h.setBounds(15, 10, 300, 25);
        card.add(h);

        int y = 50;

        card.add(label("ID", 15, y));
        txtId = field(120, y);
        card.add(txtId);

        y += 40;
        card.add(label("Film Adı", 15, y));
        txtName = field(120, y);
        card.add(txtName);

        y += 40;
        card.add(label("Tür", 15, y));
        txtGenre = field(120, y);
        card.add(txtGenre);

        y += 40;
        card.add(label("Süre (dk)", 15, y));
        txtDuration = field(120, y);
        card.add(txtDuration);

        y += 40;
        card.add(label("Durum", 15, y));
        cmbStatus = new JComboBox<>(new String[]{"AKTİF", "PASİF"});
        cmbStatus.setBounds(120, y, 230, 30);
        cmbStatus.setBackground(new Color(38, 38, 38));
        cmbStatus.setForeground(Color.WHITE);
        cmbStatus.setFocusable(false);
        card.add(cmbStatus);

        JButton btnOk = new JButton("Kaydet");
        btnOk.setBounds(15, 265, 180, 35);
        btnOk.setBackground(COLOR_ACCENT);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorderPainted(false);
        btnOk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        root.add(btnOk);

        JButton btnCancel = new JButton("İptal");
        btnCancel.setBounds(210, 265, 180, 35);
        btnCancel.setBackground(COLOR_PANEL);
        btnCancel.setForeground(COLOR_TEXT);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        root.add(btnCancel);

        if (existingRow != null) {
            txtId.setText(String.valueOf(existingRow[0]));
            txtName.setText(String.valueOf(existingRow[1]));
            txtGenre.setText(String.valueOf(existingRow[2]));
            txtDuration.setText(String.valueOf(existingRow[3]));
            cmbStatus.setSelectedItem(String.valueOf(existingRow[4]));
        }

        btnOk.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
    }

    private JLabel label(String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setForeground(COLOR_MUTED);
        l.setBounds(x, y, 100, 30);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JTextField field(int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 230, 30);
        f.setBackground(new Color(38, 38, 38));
        f.setForeground(Color.WHITE);
        f.setCaretColor(COLOR_ACCENT);
        f.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        return f;
    }

    private void onSave() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String genre = txtGenre.getText().trim();
        String duration = txtDuration.getText().trim();
        String status = String.valueOf(cmbStatus.getSelectedItem());

        if (id.isEmpty() || name.isEmpty() || genre.isEmpty() || duration.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tüm alanları doldur.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer.parseInt(id);
            Integer.parseInt(duration);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID ve Süre sayı olmalı.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultRow = new Object[]{
                Integer.parseInt(id),
                name,
                genre,
                Integer.parseInt(duration),
                status
        };
        dispose();
    }

    public Object[] getResultRow() {
        return resultRow;
    }
}
