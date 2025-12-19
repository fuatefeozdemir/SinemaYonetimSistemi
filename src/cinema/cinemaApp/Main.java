package cinema.cinemaApp;

import cinema.H2.H2TicketRepository;
import cinema.repository.UserRepository;
import cinema.service.AuthService;
import cinema.H2.H2UserRepository;
import cinema.service.TicketService;
import cinema.ui.LoginFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            H2UserRepository userRepository = new H2UserRepository();
            H2TicketRepository ticketRepository = new H2TicketRepository();


            AuthService authService = new AuthService(userRepository);
            TicketService ticketService = new TicketService(ticketRepository);


            LoginFrame loginFrame = new LoginFrame(authService, ticketService);
            loginFrame.setVisible(true);
        });
    }
}