package cinema.service;

import cinema.exception.AuthenticationException;
import cinema.exception.InvalidInputException;
import cinema.model.people.User;

import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private final List<User> users;
    private User currentUser;

    public AuthService() {
        this.users = new ArrayList<>();
    }

    public void register(User user) {
        if (user == null) {
            throw new InvalidInputException("Kayıt edilecek kullanıcı nesnesi boş olamaz.");
        }
        this.users.add(user);
    }

    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new InvalidInputException("E-posta ve şifre alanı boş bırakılamaz.");
        }

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                this.currentUser = user;
                return user;
            }
        }

        throw new AuthenticationException("Giriş başarısız! E-posta veya şifre hatalı.");
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}