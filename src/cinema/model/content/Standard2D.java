package cinema.model.content;

import java.time.LocalDate;

public class Standard2D extends Film {

    public Standard2D(String title, int durationMinutes, boolean isVisible,
                      LocalDate releaseDate, String director, String ageRestriction, String genre, String language, float imdbRating) {
        super(title, durationMinutes, isVisible, releaseDate, director, ageRestriction, genre, language, imdbRating);
    }

    @Override
    public double calculatePrice(boolean isDiscounted) {
        double finalPrice = BASE_TICKET_PRICE;
        if (isDiscounted) {
            // İndirim varsa %10 indirim uygula
            finalPrice *= 0.90;
        }
        return finalPrice;
    }

    @Override
    public String getFilmType() {
        return "Standard";
    }

    @Override
    public String getMediaType() {
        return "Film";
    }
}
