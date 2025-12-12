package cinema.model;

import cinema.exception.InvalidInputException;
import cinema.model.people.Customer;

import java.time.LocalDateTime;

public class Ticket {

    // Bilet sayaçı
    private static int ticketCount = 0;

    private final String ticketId;
    private final LocalDateTime purchaseTime;
    private final double finalPrice;
    private boolean isRefunded; // Bilet iade edildi mi

    // Bilete ait diğer nesneler
    private final Session session;
    private final Customer customer;
    private final Seat seat;

    public Ticket(Session session, Customer customer, Seat seat, double finalPrice) {

        Ticket.ticketCount++;
        this.ticketId = String.format("T%05d", Ticket.ticketCount);

        if (session == null || customer == null || seat == null) {
            throw new InvalidInputException("Bilet oluşturulurken Seans, Müşteri veya Koltuk boş olamaz.");
        }
        if (finalPrice <= 0) {
            throw new InvalidInputException("Final fiyatı sıfır veya negatif olamaz.");
        }

        // 3. Atamalar
        this.session = session;
        this.customer = customer;
        this.seat = seat;
        this.finalPrice = finalPrice;
        this.purchaseTime = LocalDateTime.now();
        this.isRefunded = false; // Biletler oluşturulduğunda her zaman iade edilmemiş olacaktır

        // Bilet kesildiğinde koltuğu rezerve et
        seat.reserve();
    }

    public boolean refund() {
        if (this.isRefunded) {
            return false; // Bilet zaten iade edilmiş
        }

        this.seat.free();

        this.isRefunded = true;
        return true;
    }

    // --- GETTER METOTLARI ---

    public String getTicketId() {
        return ticketId;
    }
    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }
    public double getFinalPrice() {
        return finalPrice;
    }
    public boolean isRefunded() {
        return isRefunded;
    }
    public Session getSession() {
        return session;
    }
    public Customer getCustomer() {
        return customer;
    }
    public Seat getSeat() {
        return seat;
    }

    @Override
    public String toString() {
        return "Bilet ID: " + ticketId +
                ", Film: " + session.getFilm().getTitle() +
                ", Koltuk: " + seat.getSeatCode() +
                ", Fiyat: " + finalPrice + " TL" +
                ", Saat: " + purchaseTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }
}