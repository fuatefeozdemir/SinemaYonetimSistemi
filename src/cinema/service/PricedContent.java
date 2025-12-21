package cinema.service;

// Ücretlendirilebilir tüm içerikler için ortak fiyatlandırma kurallarını belirleyen arayüz.

public interface PricedContent {

    // Bilet fiyatı hesaplanırken baz alınacak başlangıç fiyatı
    double BASE_TICKET_PRICE = 100.0;

    // Bilet fiyatını hesaplar
    double calculatePrice(boolean isDiscounted);
}