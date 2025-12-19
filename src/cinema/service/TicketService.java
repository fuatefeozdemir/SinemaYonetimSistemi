package cinema.service;

import cinema.H2.SessionRepository;
import cinema.exception.SeatOccupiedException;
import cinema.model.Session;
import cinema.model.Ticket;
import cinema.model.people.Cashier;
import cinema.model.people.Customer;
import cinema.repository.TicketRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // Online satış ile müşteri alır
    public void buyTicket(String sessionId, Customer customer, String seatCode) {
        ticketRepository.saveTicket(processTicket(sessionId, customer, seatCode));
    }

    // Gişe satışı ile kasiyer satar (Metot overloading)
    public Ticket buyTicket(String sessionId, Customer customer, String seatCode, Cashier cashier) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);
        if (cashier != null) {
            cashier.setDailyCount(cashier.getDailyCount() + 1);
        }
        return ticket;
    }

    private Ticket processTicket(String sessionId, Customer customer, String seatCode) {
        Session session = SessionRepository.getSession(sessionId);
//        if (session.isSeatTaken(seatCode)) {
//            throw new SeatOccupiedException("Seçilen koltuk (" + seatCode + ") zaten dolu.");
//        }

        boolean isDiscounted = calculateDiscountStatus(customer);

        double finalPrice = 0;
        if (session.getFilm() instanceof PricedContent pricedFilm) {
            finalPrice = pricedFilm.calculatePrice(isDiscounted);
        }
        Ticket newTicket = new Ticket(session.getSessionId(), customer.getEmail(), seatCode, finalPrice);

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + 5);

        return newTicket;
    }

    // İndirim kontrolü
    private boolean calculateDiscountStatus(Customer customer) {
        long age = ChronoUnit.YEARS.between(customer.getDateOfBirth(), LocalDate.now());
        return age < 18;
    }

//    public List<Ticket> getSoldTickets() {
//        return new ArrayList<>(soldTickets);
//    }

    public List<Session> getSessionsForMovie(String movieTitle) {
        return ticketRepository.getSessionsForMovie(movieTitle);
    }

    public List<String> getOccupiedSeats(String sessionId) {
        return ticketRepository.getOccupiedSeats(sessionId);
    }
}