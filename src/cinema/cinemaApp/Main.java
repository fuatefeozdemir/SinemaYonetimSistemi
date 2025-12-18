package cinema.cinemaApp;

import cinema.service.AuthService;
import cinema.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}