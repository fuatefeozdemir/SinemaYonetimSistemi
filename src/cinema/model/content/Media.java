package cinema.model.content;

import cinema.exception.InvalidInputException;

public abstract class Media {

    private String name; // Medyanın ismi
    private int durationMinutes; // Medyanın süresi
    private boolean isVisible; // Medya gösterimde mi

    public Media(String name, int durationMinutes, boolean isVisible) {
        setName(name);
        setDurationMinutes(durationMinutes);
        setVisible(isVisible);
    }

    // --- GETTER METOTLARI ---

    public String getName() {
        return name;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public boolean isVisible() {
        return isVisible;
    }

    // --- SETTER METOTLARI ---

    public void setName(String name) {
        if (name == null) {
            throw new InvalidInputException("Başlık null olamaz.");
        }
        this.name = name;
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
