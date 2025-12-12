package cinema.cinemaApp;

import cinema.service.AuthService;
import cinema.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 1. Önce Servisleri Ayağa Kaldır
            AuthService authService = new AuthService();
            // FilmService filmService = new FilmService(); (İleride eklenecek)

            // 2. Login Ekranını Başlatırken Servisi İçine Gönder
            // (LoginFrame constructor'ını buna göre değiştireceğiz)
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}