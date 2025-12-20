package cinema.service;

import cinema.model.people.User;  // BUNA DİKKAT
import cinema.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private final List<User> users = new ArrayList<>();

    public void register(User user) {
        if (user == null) throw new IllegalArgumentException("Kullanıcı null olamaz.");

        String email = user.getEmail();
        boolean exists = users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));

        if (exists) throw new IllegalArgumentException("Bu e-posta zaten kayıtlı.");

        users.add(user);
    }

    public User login(String email, String password) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .filter(u -> u.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Hatalı e-posta veya şifre."));
    }
}
