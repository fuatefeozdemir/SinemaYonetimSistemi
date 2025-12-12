package cinema.model.content;

public class Trailer extends Media {

    public Trailer(String title, int durationMinutes, boolean isVisible) {
        super(title, durationMinutes, isVisible);
    }

    @Override
    public String getMediaType() {
        return "Trailer";
    }
}
