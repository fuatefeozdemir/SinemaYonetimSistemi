package cinema.ui;

import cinema.service.AuthService;
import java.time.LocalDate;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final AuthService authService;

    private JTextField txtEmail;
    private JPasswordField txtPass;
    private JComboBox<String> cmbRole;

    public RegisterFrame(AuthService authService) {
        this.authService = authService;

        setTitle("Kayıt Ol");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(33,33,33));

        JLabel lblTitle = new JLabel("KAYIT OL", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBounds(0, 20, 420, 30);
        add(lblTitle);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(Color.LIGHT_GRAY);
        lblEmail.setBounds(60, 80, 100, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(160, 80, 200, 28);
        add(txtEmail);

        JLabel lblPass = new JLabel("Şifre:");
        lblPass.setForeground(Color.LIGHT_GRAY);
        lblPass.setBounds(60, 120, 100, 25);
        add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(160, 120, 200, 28);
        add(txtPass);

        JLabel lblRole = new JLabel("Rol:");
        lblRole.setForeground(Color.LIGHT_GRAY);
        lblRole.setBounds(60, 160, 100, 25);
        add(lblRole);

        cmbRole = new JComboBox<>(new String[]{"customer", "cashier", "manager"});
        cmbRole.setBounds(160, 160, 200, 28);
        add(cmbRole);

        JButton btnRegister = new JButton("Kayıt Ol");
        btnRegister.setBounds(60, 215, 140, 35);
        btnRegister.setBackground(new Color(229,9,20));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        add(btnRegister);

        JButton btnBack = new JButton("Geri");
        btnBack.setBounds(220, 215, 140, 35);
        btnBack.setFocusPainted(false);
        add(btnBack);

        btnRegister.addActionListener(e -> onRegister());
        btnBack.addActionListener(e -> {
            new LoginFrame(authService).setVisible(true);
            dispose();
        });
    }
    private void onRegister() {
        String email = txtEmail.getText().trim();
        String pass  = new String(txtPass.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email ve şifre gir!");
            return;
        }

        // isim soyisim UI'da yok, sabit veriyoruz
        String firstName = "Customer";
        String lastName  = "User";

        LocalDate birthDate = LocalDate.of(2000, 1, 1); // şimdilik sabit

        cinema.model.people.User user =
                new cinema.model.people.Customer(firstName, lastName, email, birthDate, pass);

        authService.register(user);

        JOptionPane.showMessageDialog(this, "Kayıt başarılı! Giriş yapabilirsin.");
        new LoginFrame(authService).setVisible(true);
        dispose();
    }


}
