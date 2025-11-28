package cinema.service;

public interface PricedContent {
    int BASE_TICKET_PRICE = 30;

    double calculatePrice(boolean isDiscounted);
}
