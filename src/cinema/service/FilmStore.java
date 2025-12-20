package cinema.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilmStore {
    private static final List<String> FILMS = new ArrayList<>();

    static {
        // başlangıçta birkaç film (istersen silersin)
        FILMS.add("Inception");
        FILMS.add("Interstellar");
        FILMS.add("Joker");
        FILMS.add("Avatar 2");
    }

    public static List<String> getFilms() {
        return Collections.unmodifiableList(FILMS);
    }

    public static void addFilm(String title) {
        if (title == null) return;
        title = title.trim();
        if (title.isEmpty()) return;

        // aynı film tekrar eklenmesin (case-insensitive)
        for (String f : FILMS) {
            if (f.equalsIgnoreCase(title)) return;
        }
        FILMS.add(title);
    }

    public static void removeFilm(String title) {
        if (title == null) return;
        String t = title.trim();
        FILMS.removeIf(f -> f.equalsIgnoreCase(t));
    }

    public static void updateFilm(String oldTitle, String newTitle) {
        removeFilm(oldTitle);
        addFilm(newTitle);
    }
}
