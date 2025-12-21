package cinema.service;

import cinema.H2.H2SessionRepository;
import cinema.H2.H2TicketRepository;
import cinema.model.Session;
import cinema.model.Ticket;
import cinema.model.content.Film; // Film sınıfı eklendi
import cinema.model.people.Cashier;
import cinema.model.people.Customer;
import cinema.repository.TicketRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // Müşteri bilet satışı
    public void buyTicket(String sessionId, Customer customer, String seatCode) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);
        ticketRepository.saveTicket(ticket);
    }

    // Kasiyer bilet satışı (Overloading)
    public void buyTicket(String sessionId, Customer customer, String seatCode, Cashier cashier) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);
        ticketRepository.saveTicket(ticket);

        if (cashier != null) {
            cashier.setDailyCount(cashier.getDailyCount() + 1);
        }
    }

    private Ticket processTicket(String sessionId, Customer customer, String seatCode) {
        Session session = H2SessionRepository.getSession(sessionId);

        if (session == null) {
            throw new RuntimeException("Seans bulunamadı! ID: " + sessionId);
        }

        boolean isDiscounted = calculateDiscountStatus(customer);
        double finalPrice = 100.0;

        if (session.getFilm() instanceof Film film) {
            finalPrice = film.calculatePrice(isDiscounted);
        } else {
            System.out.println("Uyarı: Bu içerik bir Film türünde değil, standart fiyat uygulandı.");
        }

        Ticket newTicket = new Ticket(session.getSessionId(), customer.getEmail(), seatCode, finalPrice);

        // Her bilet satışı için müşteriye 5 puan ekler
        customer.addLoyaltyPoints(5);

        return newTicket;
    }

    // İndirim uygulanıp uygulanmayacağına bakan metot
    private boolean calculateDiscountStatus(Customer customer) {
        if (customer.getDateOfBirth() == null) return false;
        long age = ChronoUnit.YEARS.between(customer.getDateOfBirth(), LocalDate.now());
        // Yaş 18 den küçükse indirim uygulanır
        return age < 18;
    }

    public List<Session> getSessionsForMovie(String movieTitle) {
        return ticketRepository.getSessionsForMovie(movieTitle);
    }

    public List<String> getOccupiedSeats(String sessionId) {
        return ticketRepository.getOccupiedSeats(sessionId);
    }

    public List<Ticket> getMyTickets(String email) {
        return H2TicketRepository.getTicketsByCustomerEmail(email);
    }

    public boolean refundTicket(String ticketId) {
        return H2TicketRepository.deleteTicket(ticketId);
    }
}