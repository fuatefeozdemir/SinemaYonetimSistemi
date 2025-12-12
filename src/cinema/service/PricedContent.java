package cinema.service;

public interface PricedContent {
    int BASE_TICKET_PRICE = 100; // Biletlerin indirimsiz ve zamsız ücreti

    double calculatePrice(boolean isDiscounted);
}
