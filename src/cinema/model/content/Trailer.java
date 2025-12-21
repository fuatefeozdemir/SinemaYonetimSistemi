package cinema.model.content;

public class Trailer extends Media {

    private final String filmName; // Trailerin ait olduğu film

    public Trailer(String name, int durationMinutes, boolean isVisible, String filmName) {
        super(name, durationMinutes, isVisible);
        this.filmName = filmName;
    }

    @Override
    public String getMediaType() {
        return "Trailer";
    }

    public String getFilmName() {
        return filmName;
    }
}
