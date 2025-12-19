package cinema.model.content;

public class Advertisement extends Media {

    private String company;

    public Advertisement(String title, int durationMinutes, boolean isVisible, String company) {
        super(title, durationMinutes, isVisible);
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String getMediaType() {
        return "Reklam";
    }
}
