package cinema.model;

import cinema.exception.InvalidInputException;
import cinema.model.people.Customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Ticket {
    private final String ticketId;
    private final LocalDateTime purchaseTime;
    private final double finalPrice;
    private final String session;
    private final String customer;
    private final String seatCode; // Seat nesnesi yerine String kod tutuyoruz


    public Ticket(String session, String customer, String seatCode, double finalPrice) {
        this.ticketId = UUID.randomUUID().toString();
        this.purchaseTime = LocalDateTime.now();
        this.finalPrice = finalPrice;
        this.session = session;
        this.customer = customer;
        this.seatCode = seatCode;
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