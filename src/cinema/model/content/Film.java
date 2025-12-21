package cinema.model.content;

import cinema.exception.InvalidInputException;
import cinema.service.PricedContent;

import java.time.LocalDate;

public abstract class Film extends Media implements PricedContent {

    // Film bilgileri olarak gözükecek değişkenler
    private LocalDate releaseDate;
    private String director;
    private String ageRestriction;
    private String genre;
    private String language;
    private float imdbRating;
    private final String type;

    public Film(String name, int durationMinutes, boolean isVisible, LocalDate releaseDate, String director, String ageRestriction, String genre, String language, float imdbRating) {
        super(name, durationMinutes, isVisible);
        setReleaseDate(releaseDate);
        setDirector(director);
        setAgeRestriction(ageRestriction);
        setGenre(genre);
        setLanguage(language);
        setImdbRating(imdbRating);
        this.type = this.getFilmType();
    }

    // GETTERLAR

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getDirector() {
        return director;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public float getImdbRating() {
        return imdbRating;
    }

    // SETTERLAR

    public void setReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null || releaseDate.isAfter(LocalDate.now())) {
            throw new InvalidInputException("Vizyon tarihi null veya gelecekte olamaz.");
        }
        this.releaseDate = releaseDate;
    }

    public void setDirector(String director) {
        if (director == null) {
            throw new InvalidInputException("Yönetmen null olamaz.");
        }
        this.director = director;
    }

    public void setAgeRestriction(String ageRestriction) {
        if (ageRestriction == null) {
            throw new InvalidInputException("Yaş kısıtlaması null olamaz.");
        }
        this.ageRestriction = ageRestriction;
    }

    public void setGenre(String genre) {
        if (genre == null) {
            throw new InvalidInputException("Film türü null olamaz.");
        }
        this.genre = genre;
    }

    public void setLanguage(String language) {
        if (language == null) {
            throw new InvalidInputException("Film dili null olamaz.");
        }
        this.language = language;
    }

    public void setImdbRating(float imdbRating) {
        if (imdbRating < 0 || imdbRating > 10) {
            throw new InvalidInputException("Film puanı 0-10 dışında olamaz.");
        }
        this.imdbRating = imdbRating;
    }

    public String getType() {
        return type;
    }

    // PricedContent arayüzünden override edilir
    @Override
    public abstract double calculatePrice(boolean isDiscounted);

    // Filmden kalıtılan alt sınıflarda filmin tipini (Standard, Premium, Animasyon) döndürür
    public abstract String getFilmType();

}
