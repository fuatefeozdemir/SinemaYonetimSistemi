package cinema.util;

import cinema.service.*;

public class ServiceContainer {

    private final AuthService authService;
    private final HallService hallService;
    private final MediaService mediaService;
    private final SessionService sessionService;
    private final TicketService ticketService;

    public ServiceContainer(AuthService authService, HallService hallService, MediaService mediaService, SessionService sessionService, TicketService ticketService) {
        this.authService = authService;
        this.hallService = hallService;
        this.mediaService = mediaService;
        this.sessionService = sessionService;
        this.ticketService = ticketService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public HallService getHallService() {
        return hallService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public TicketService getTicketService() {
        return ticketService;
    }
}
