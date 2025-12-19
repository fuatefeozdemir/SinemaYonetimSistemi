package cinema.ui;

import cinema.service.AuthService;
import cinema.service.TicketService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReceiptFrame extends JFrame {

    private final AuthService authService; // AuthService eklendi
    private final TicketService ticketService; // AuthService eklendi

    public ReceiptFrame(String movieTitle, ArrayList<String> seats, double totalPrice, AuthService authService, TicketService ticketService) {
        this.authService = authService; // Parametre olarak alındı
        this.ticketService = ticketService;

        setTitle("Bilet Özeti");
        setUndecorated(true); // Tasarıma uygun olması için kenarlıksız yaptık
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(33,33,33));
        contentPane.setBorder(BorderFactory.createLineBorder(new Color(229, 9, 20), 2));
        setContentPane(contentPane);

        // Header
        JLabel lblTitle = new JLabel("🎟 BİLETİNİZ HAZIR", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(229, 9, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // Bilgi Alanı
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(45,45,45));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        addInfoRow(infoPanel, "FİLM:", movieTitle);
        addInfoRow(infoPanel, "KOLTUKLAR:", String.join(", ", seats));
        addInfoRow(infoPanel, "TOPLAM:", String.format("%.2f TL", totalPrice));
        addInfoRow(infoPanel, "MÜŞTERİ:", authService.getCurrentUser().getFirstName());

        contentPane.add(infoPanel, BorderLayout.CENTER);

        // Buton
        JButton btnMainMenu = new JButton("ANA MENÜYE DÖN");
        btnMainMenu.setPreferredSize(new Dimension(400, 50));
        btnMainMenu.setBackground(new Color(229,9,20));
        btnMainMenu.setForeground(Color.WHITE);
        btnMainMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMainMenu.setFocusPainted(false);
        btnMainMenu.setBorderPainted(false);
        btnMainMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnMainMenu.addActionListener(e -> {
            // HATA BURADAYDI: authService parametresini gönderiyoruz
            new CustomerMainFrame(authService, ticketService).setVisible(true);
            dispose();
        });

        contentPane.add(btnMainMenu, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setForeground(Color.WHITE);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        val.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));

        panel.add(lbl);
        panel.add(val);
    }
}