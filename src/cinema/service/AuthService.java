package cinema.service;

import cinema.exception.AlreadyExistException;
import cinema.exception.AuthenticationException;
import cinema.exception.InvalidInputException;
import cinema.model.people.User;
import cinema.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // UI'dan gelen Register isteği buraya düşer
    public void register(User user) {
        if (user == null) {
            throw new InvalidInputException("Kullanıcı bilgileri boş olamaz.");
        }

        // Önce veritabanında bu e-posta var mı kontrol et
        User existing = userRepository.getUser(user.getEmail());
        if (existing != null) {
            throw new AlreadyExistException("Bu e-posta adresi zaten kayıtlı!");
        }

        // Veritabanına kaydet
        userRepository.saveUser(user);
    }

    // UI'dan gelen Login isteği buraya düşer
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new InvalidInputException("E-posta ve şifre boş bırakılamaz.");
        }

        // Kullanıcıyı veritabanından e-posta ile çek
        User user = userRepository.getUser(email);

        // Kullanıcı var mı ve şifre doğru mu?
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return user;
        }



        throw new AuthenticationException("E-posta veya şifre hatalı!");
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    public void updateUser(User user) {
        userRepository.updateUser(user);
        User newUser = userRepository.getUser(user.getEmail());
        this.currentUser = newUser;
    }
    public void deleteUser() {
        userRepository.deleteUser(getCurrentUser().getEmail());

    }
    public User getUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.getUser(email);
    }
}