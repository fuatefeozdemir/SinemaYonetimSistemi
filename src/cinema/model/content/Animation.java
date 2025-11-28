package cinema.model.content;

import java.time.LocalDate;

public class Animation extends Film {

    // alan eklenecek

    private static final double ANIMATION_PRICE_DISCOUNT = 0.8;

    public Animation(String title, int durationMinutes, boolean isVisible,
                     LocalDate releaseDate, String director, String ageRestriction, String genre, String language, float imdbRating) {
        super(title, durationMinutes, isVisible, releaseDate, director, ageRestriction, genre, language, imdbRating);
    }

    @Override
    public double calculatePrice(boolean isDiscounted) {
        double finalPrice = BASE_TICKET_PRICE * ANIMATION_PRICE_DISCOUNT;
        if (isDiscounted) {
            // İndirim varsa %10 indirim uygula
            finalPrice *= 0.90;
        }
        return finalPrice;
    }

    @Override
    public String getFilmType() {
        return "Animasyon";
    }

    @Override
    public String getMediaType() {
        return "Film";
    }
}
