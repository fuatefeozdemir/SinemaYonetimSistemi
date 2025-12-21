package cinema.service;

import cinema.H2.H2SessionRepository;
import cinema.H2.H2TicketRepository;
import cinema.model.Session;
import cinema.model.Ticket;
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

    // --- MÜŞTERİ (ONLINE) SATIŞ ---
    public void buyTicket(String sessionId, Customer customer, String seatCode) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);
        ticketRepository.saveTicket(ticket);
    }

    // --- KASİYER (GİŞE) SATIŞI ---
    // Bu metot bilet oluşturuyor ama kaydetmiyordu, düzeltildi.
    public void buyTicket(String sessionId, Customer customer, String seatCode, Cashier cashier) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);

        // KRİTİK DÜZELTME: Veritabanına kaydetme komutu eklendi
        ticketRepository.saveTicket(ticket);

        if (cashier != null) {
            cashier.setDailyCount(cashier.getDailyCount() + 1);
            // İsteğe bağlı: Kasiyer bilgisini de güncelleyebilirsin (DailyCount için)
            // authService.updateUser(cashier);
        }
    }

    private Ticket processTicket(String sessionId, Customer customer, String seatCode) {
        Session session = H2SessionRepository.getSession(sessionId);

        if (session == null) {
            throw new RuntimeException("Seans bulunamadı! ID: " + sessionId);
        }

        boolean isDiscounted = calculateDiscountStatus(customer);
        double finalPrice = 150.0; // Varsayılan fiyat

        // Fiyatlandırma mantığı
        if (session.getFilm() instanceof PricedContent pricedFilm) {
            finalPrice = pricedFilm.calculatePrice(isDiscounted);
        }

        // Yeni bilet nesnesi oluşturma
        Ticket newTicket = new Ticket(session.getSessionId(), customer.getEmail(), seatCode, finalPrice);

        // Müşteriye sadakat puanı ekle
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + 5);

        return newTicket;
    }

    private boolean calculateDiscountStatus(Customer customer) {
        if (customer.getDateOfBirth() == null) return false;
        long age = ChronoUnit.YEARS.between(customer.getDateOfBirth(), LocalDate.now());
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