package cinema.cinemaApp;

import cinema.H2.*;
import cinema.model.Hall;
import cinema.model.Session;
import cinema.model.content.Media;
import cinema.model.content.Premium3D;
import cinema.model.content.Standard2D;
import cinema.model.people.Cashier;
import cinema.model.people.Customer;
import cinema.model.people.Manager;
import cinema.repository.UserRepository;
import cinema.service.AuthService;
import cinema.service.TicketService;
import cinema.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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