package cinema.service;

import cinema.exception.SeatOccupiedException;
import cinema.model.Session;
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
        if (cashier != null) {
            cashier.setDailyCount(cashier.getDailyCount() + 1);
        }
        return ticket;
    }

    private Ticket processTicket(Session session, Customer customer, String seatCode) {
        if (session.isSeatTaken(seatCode)) {
            throw new SeatOccupiedException("Seçilen koltuk (" + seatCode + ") zaten dolu.");
        }

        boolean isDiscounted = calculateDiscountStatus(customer);

        double finalPrice = 0;
        if (session.getFilm() instanceof PricedContent pricedFilm) {
            finalPrice = pricedFilm.calculatePrice(isDiscounted);
        }
        Ticket newTicket = new Ticket(session, customer, seatCode, finalPrice);

        soldTickets.add(newTicket);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + 5);

        return newTicket;
    }

    // İndirim kontrolü
    private boolean calculateDiscountStatus(Customer customer) {
        long age = ChronoUnit.YEARS.between(customer.getDateOfBirth(), LocalDate.now());
        return age < 18;
    }

    public List<Ticket> getSoldTickets() {
        return new ArrayList<>(soldTickets);
    }
}