package cinema.model.content;

public class Advertisement extends Media {

    // alan eklenecek

    public Advertisement(String title, int durationMinutes, boolean isVisible) {
        super(title, durationMinutes, isVisible);
    }

    @Override
    public String getMediaType() {
        return "Reklam";
    }
}
