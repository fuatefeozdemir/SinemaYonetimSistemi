package cinema.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {

    // Bilete ait alanlar
    private String ticketId;
    private final LocalDateTime purchaseTime;
    private final double finalPrice;

    private final String session; // Seans bilgisi (ID)
    private final String customer; // Müşteri bilgisi (E-posta)
    private final String seatCode; // Seçilen koltuk

    public Ticket(String session, String customer, String seatCode, double finalPrice) {
        this.ticketId = UUID.randomUUID().toString(); // Otomatik  ID üretimi
        this.purchaseTime = LocalDateTime.now(); // Kayıt anındaki zamanı alır
        this.finalPrice = finalPrice;
        this.session = session;
        this.customer = customer;
        this.seatCode = seatCode;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public String getSession() {
        return session;
    }

    public String getCustomer() {
        return customer;
    }

    public String getSeatCode() {
        return seatCode;
    }
}