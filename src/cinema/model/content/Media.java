package cinema.model.content;

import cinema.exception.InvalidInputException;

public abstract class Media {
    private static int mediaCount = 0; // Medya sayısı 0'dan başlar

    private final String mediaId; // Medyanın kodu
    private String title; // Medyanın ismi
    private int durationMinutes; // Medyanın süresi
    private boolean isVisible; // Medya gösterimde mi

    public Media(String title, int durationMinutes, boolean isVisible) {
        mediaCount++;

        this.mediaId = String.format("M%03d", Media.mediaCount); // Medya kodlarının M001 formatında yazılmasını sağlar
        setTitle(title);
        setDurationMinutes(durationMinutes);
        setVisible(isVisible);
    }

    // --- GETTER METOTLARI ---

    public static int getMediaCount() {
        return mediaCount;
    }
    public String getMediaId() {
        return mediaId;
    }
    public String getTitle() {
        return title;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public boolean isVisible() {
        return isVisible;
    }

    // --- SETTER METOTLARI ---

    public void setTitle(String title) {
        if (title == null) {
            throw new InvalidInputException("Başlık null olamaz.");
        }
        this.title = title;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new InvalidInputException("Süre negatif olamaz.");
        }
        this.durationMinutes = durationMinutes;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    // Medyadan kalıtılan alt sınıflarda medya tipini (Film/Fragman/Reklam) döndürür
    public abstract String getMediaType();

}
