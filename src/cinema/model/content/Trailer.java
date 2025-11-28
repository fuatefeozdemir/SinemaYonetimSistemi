package cinema.model.content;

public class Trailer extends Media {

    // alan eklenecek

    public Trailer(String title, int durationMinutes, boolean isVisible) {
        super(title, durationMinutes, isVisible);
    }

    @Override
    public String getMediaType() {
        return "Trailer";
    }
}
