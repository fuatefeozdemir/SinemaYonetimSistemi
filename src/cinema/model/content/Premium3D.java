package cinema.model.content;

import java.time.LocalDate;

public class Premium3D extends Film {

    // alan eklenecek

    private static final double PREMIUM_PRICE_MULTIPLIER = 1.2;

    public Premium3D(String title, int durationMinutes, boolean isVisible,
                     LocalDate releaseDate, String director, String ageRestriction, String genre, String language, float imdbRating) {
        super(title, durationMinutes, isVisible, releaseDate, director, ageRestriction, genre, language, imdbRating);
    }

    @Override
    public double calculatePrice(boolean isDiscounted) {
        double finalPrice = BASE_TICKET_PRICE * PREMIUM_PRICE_MULTIPLIER;
        if (isDiscounted) {
            // İndirim varsa %10 indirim uygula
            finalPrice *= 0.90;
        }
        return finalPrice;
    }

    @Override
    public String getFilmType() {
        return "Premium";
    }

    @Override
    public String getMediaType() {
        return "Film";
    }
}
