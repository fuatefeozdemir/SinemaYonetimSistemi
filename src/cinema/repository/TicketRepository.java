package cinema.repository;

import cinema.model.Session;
import cinema.model.Ticket;
import cinema.model.people.Cashier;
import cinema.model.people.Customer;

import java.util.List;

public interface TicketRepository {
    void initialize();
    void saveTicket(Ticket ticket);
    void updateRefundStatus(String ticketId, boolean status);
    List<Ticket> getTicketsByCustomer(String email);
    List<Session> getSessionsForMovie(String movieTitle);
    List<String> getOccupiedSeats(String sessionId);
}