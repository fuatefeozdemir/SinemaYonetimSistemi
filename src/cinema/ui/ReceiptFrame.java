package cinema.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReceiptFrame extends JFrame {

    public ReceiptFrame(String movieTitle, ArrayList<String> seats, double totalPrice) {

        setTitle("Bilet");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(33,33,33));

        JLabel lblTitle = new JLabel("🎟 BİLET BİLGİLERİ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));
        add(lblTitle, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(new Color(45,45,45));
        area.setForeground(Color.WHITE);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setText(
                "Film: " + movieTitle +
                        "\n\nKoltuklar: " + String.join(", ", seats) +
                        "\n\nToplam Tutar: " + String.format("%.0f", totalPrice) + " TL"
        );

        add(area, BorderLayout.CENTER);

        JButton btnMainMenu = new JButton("ANA MENÜYE DÖN");
        btnMainMenu.setBackground(new Color(229,9,20));
        btnMainMenu.setForeground(Color.WHITE);
        btnMainMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMainMenu.setFocusPainted(false);

        btnMainMenu.addActionListener(e -> {
            new CustomerMainFrame().setVisible(true);
            dispose();
        });

        add(btnMainMenu, BorderLayout.SOUTH);
    }
}
