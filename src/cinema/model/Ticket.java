package cinema.model;

import cinema.exception.InvalidInputException;
import cinema.model.people.Customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {

    private static int ticketCount = 0;

    private final String ticketId;
    private final LocalDateTime purchaseTime;
    private final double finalPrice;
    private boolean isRefunded;

    private final Session session;
    private final Customer customer;
    private final String seatCode; // Seat nesnesi yerine String kod tutuyoruz

    public Ticket(Session session, Customer customer, String seatCode, double finalPrice) {
        Ticket.ticketCount++;
        this.ticketId = String.format("T%05d", Ticket.ticketCount);

        if (session == null || customer == null || seatCode == null || seatCode.isEmpty()) {
            throw new InvalidInputException("Bilet oluşturulurken Seans, Müşteri veya Koltuk kodu boş olamaz.");
        }
        if (finalPrice <= 0) {
            throw new InvalidInputException("Final fiyatı sıfır veya negatif olamaz.");
        }

        this.session = session;
        this.customer = customer;
        this.seatCode = seatCode.toUpperCase().trim();
        this.finalPrice = finalPrice;
        this.purchaseTime = LocalDateTime.now();
        this.isRefunded = false;

        // Bilet kesildiğinde ilgili seansın matrisinde koltuğu rezerve et
        this.session.reserveSeat(this.seatCode);
    }

    public boolean refund() {
        if (this.isRefunded) {
            return false; // Zaten iade edilmiş
        }

        // Seans üzerindeki koltuğu serbest bırak
        this.session.freeSeat(this.seatCode);

        this.isRefunded = true;
        return true;
    }

    // --- GETTERLAR ---

    public String getTicketId() { return ticketId; }
    public LocalDateTime getPurchaseTime() { return purchaseTime; }
    public double getFinalPrice() { return finalPrice; }
    public boolean isRefunded() { return isRefunded; }
    public Session getSession() { return session; }
    public Customer getCustomer() { return customer; }
    public String getSeatCode() { return seatCode; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Bilet ID: " + ticketId +
                ", Film: " + session.getFilm().getName() +
                ", Koltuk: " + seatCode +
                ", Müşteri: " + customer.getFirstName() + " " + customer.getLastName() +
                ", Fiyat: " + finalPrice + " TL" +
                ", İşlem Tarihi: " + purchaseTime.format(formatter);
    }
}