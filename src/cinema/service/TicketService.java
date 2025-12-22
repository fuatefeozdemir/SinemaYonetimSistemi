package cinema.service;

import cinema.H2.H2SessionRepository;
import cinema.H2.H2TicketRepository;
import cinema.model.Session;
import cinema.model.Ticket;
import cinema.model.content.Film;
import cinema.model.people.Cashier;
import cinema.model.people.Customer;
import cinema.repository.SessionRepository;
import cinema.repository.TicketRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TicketService {

    private final TicketRepository ticketRepository;
    private final SessionRepository sessionRepository;

    public TicketService(TicketRepository ticketRepository, SessionRepository sessionRepository) {
        this.ticketRepository = ticketRepository;
        this.sessionRepository = sessionRepository;
    }

    // Müşterinin kendi başına yaptığı bilet alımları
    public void buyTicket(String sessionId, Customer customer, String seatCode) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);
        ticketRepository.saveTicket(ticket);
    }

    // Kasiyer aracılığıyla yapılan bilet alımları (Overloading)
    public void buyTicket(String sessionId, Customer customer, String seatCode, Cashier cashier) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);
        ticketRepository.saveTicket(ticket);

        if (cashier != null) {
            cashier.setDailyCount(cashier.getDailyCount() + 1);
        }
    }

    // Bilet oluşturma sürecinde yapılması gereken işlemleri (Senas kontrolü, fiyat hesaplama) yapar
    private Ticket processTicket(String sessionId, Customer customer, String seatCode) {
        Session session = sessionRepository.getSession(sessionId);

        if (session == null) {
            throw new RuntimeException("Seans bulunamadı! ID: " + sessionId);
        }

        boolean isDiscounted = calculateDiscountStatus(customer);
        double finalPrice = 100.0; // Fiyat hesaplanamazsa varsayılan fiyat 100 TL

        if (session.getFilm() instanceof Film film) {
            finalPrice = film.calculatePrice(isDiscounted);
        }

        Ticket newTicket = new Ticket(session.getSessionId(), customer.getEmail(), seatCode, finalPrice);

        // Müşteriye her bilet için 5 sadakat puanı ekleniyor
        customer.addLoyaltyPoints(5);

        return newTicket;
    }

    // İndirim durumunu kontrol eder
    private boolean calculateDiscountStatus(Customer customer) {
        if (customer.getDateOfBirth() == null) return false;
        long age = ChronoUnit.YEARS.between(customer.getDateOfBirth(), LocalDate.now());
        return age < 18;
    }

    // Bir seanstaki rezerve edilmiş koltukları listeler
    public List<String> getOccupiedSeats(String sessionId) {
        return ticketRepository.getOccupiedSeats(sessionId);
    }

    // Giriş yapmış müşterinin geçmiş biletlerini getirir
    public List<Ticket> getMyTickets(String email) {
        return H2TicketRepository.getTicketsByCustomerEmail(email);
    }

    // Bileti iptal eder ve koltuğu boşa çıkarır
    public boolean refundTicket(String ticketId) {
        return H2TicketRepository.deleteTicket(ticketId);
    }
}