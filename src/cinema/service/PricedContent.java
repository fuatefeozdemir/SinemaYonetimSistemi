package cinema.service;

public interface PricedContent {
    double BASE_TICKET_PRICE = 100;

    double calculatePrice(boolean isDiscounted);
}
