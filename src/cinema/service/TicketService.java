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

    // --- MÜŞTERİ (ONLINE) SATIŞ ---
    public void buyTicket(String sessionId, Customer customer, String seatCode) {
        Ticket ticket = processTicket(sessionId, customer, seatCode);
        ticketRepository.saveTicket(ticket);
    }

    // --- KASİYER (GİŞE) SATIŞI ---
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
        double finalPrice = 100.0; // Varsayılan/Hata durumunda kullanılacak fiyat

        // HATA BURADAYDI: Media nesnesini Film'e cast etmeliyiz
        if (session.getFilm() instanceof Film film) {
            // Eğer nesne bir Film ise (Standard2D, Premium3D veya Animation)
            finalPrice = film.calculatePrice(isDiscounted);
        } else {
            // Eğer ileride Film olmayan başka bir Media türü eklersen burası çalışır
            System.out.println("Uyarı: Bu içerik bir Film türünde değil, standart fiyat uygulandı.");
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
        // Yaşı 18'den küçükse indirim hakkı kazanır
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