package cinema.cinemaApp;

import cinema.H2.*;
import cinema.service.*;
import cinema.ui.LoginFrame;
import cinema.util.ServiceContainer;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            H2UserRepository userRepository = new H2UserRepository();
            H2HallRepository hallRepository = new H2HallRepository();
            H2MediaRepository mediaRepository = new H2MediaRepository();
            H2SessionRepository sessionRepository = new H2SessionRepository();
            H2TicketRepository ticketRepository = new H2TicketRepository();

            AuthService authService = new AuthService(userRepository);
            HallService hallService = new HallService(hallRepository);
            MediaService mediaService = new MediaService(mediaRepository);
            SessionService sessionService = new SessionService(sessionRepository, hallRepository, mediaRepository);
            TicketService ticketService = new TicketService(ticketRepository, sessionRepository);
            ServiceContainer serviceContainer = new ServiceContainer(authService, hallService, mediaService, sessionService, ticketService);


            LoginFrame loginFrame = new LoginFrame(serviceContainer);
            loginFrame.setVisible(true);


        });
    }
}