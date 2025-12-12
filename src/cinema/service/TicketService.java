package cinema.service;

import cinema.exception.SeatOccupiedException;
import cinema.model.Session;
import cinema.model.Seat;
import cinema.model.Ticket;
import cinema.model.people.Cashier;
import cinema.model.people.Customer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private List<Ticket> soldTickets;

    public TicketService() {
        this.soldTickets = new ArrayList<>();
    }

    // Online satış ile müşteri alır
    public Ticket buyTicket(Session session, Customer customer, String seatCode) {
        return processTicket(session, customer, seatCode);
    }

    // Gişe satışı ile kasiyer satar (Metot overloading)
    public Ticket buyTicket(Session session, Customer customer, String seatCode, Cashier cashier) {
        Ticket ticket = processTicket(session, customer, seatCode);
        cashier.increaseDailyCount();
        return ticket;
    }

    private Ticket processTicket(Session session, Customer customer, String seatCode) {

        Seat seat = session.getSeat(seatCode);
        if (seat.isTaken()) {
            throw new SeatOccupiedException("Seçilen koltuk (" + seatCode + ") dolu.");
        }

        boolean isDiscounted = calculateDiscountStatus(customer);

        double finalPrice = ((PricedContent) session.getFilm()).calculatePrice(isDiscounted);

        Ticket newTicket = new Ticket(session, customer, seat, finalPrice);
        soldTickets.add(newTicket);
        customer.addLoyaltyPoints(5);

        return newTicket;
    }

    // Eğer müşterinin yaşı 18 den küçükse indirimli bilet satışı yapılır
    private boolean calculateDiscountStatus(Customer customer) {
        long age = ChronoUnit.YEARS.between(customer.getDateOfBirth(), LocalDate.now());
        return age < 18;
    }

    public List<Ticket> getSoldTickets() {
        return soldTickets;
    }
}