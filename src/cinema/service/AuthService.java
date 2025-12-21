package cinema.service;

import cinema.exception.AlreadyExistException;
import cinema.exception.AuthenticationException;
import cinema.exception.InvalidInputException;
import cinema.model.people.User;
import cinema.repository.UserRepository;


public class AuthService {

    private final UserRepository userRepository;
    private User currentUser; // O an oturumu açık olan kullanıcı

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Kayıt işlemi
    public void register(User user) {
        if (user == null) {
            throw new InvalidInputException("Kullanıcı bilgileri boş olamaz.");
        }

        User existing = userRepository.getUser(user.getEmail());
        if (existing != null) {
            throw new AlreadyExistException("Bu e-posta adresi zaten kayıtlı!");
        }

        userRepository.saveUser(user);
    }

    // Giriş işlemi
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new InvalidInputException("E-posta ve şifre boş bırakılamaz.");
        }

        User user = userRepository.getUser(email);

        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user; // Oturumu hafızaya al
            return user;
        }

        throw new AuthenticationException("E-posta veya şifre hatalı!");
    }

    // Aktif oturumdan çıkış yapar
    public void logout() {
        this.currentUser = null;
    }

    // Oturumu açık olan kullanıcıyı döndürür
    public User getCurrentUser() {
        return currentUser;
    }

    // Kullanıcı bilgilerini günceller
    public void updateUser(User user) {
        userRepository.updateUser(user);
        // Güncel veriyi veritabanından tekrar çekip oturuma yansıtıyoruz
        this.currentUser = userRepository.getUser(user.getEmail());
    }

    // Mevcut oturumdaki kullanıcıyı sistemden siler
    public void deleteUser() {
        if (getCurrentUser() != null) {
            userRepository.deleteUser(getCurrentUser().getEmail());
        }
    }

    // Verilen e-posta adresine sahip kullanıcıyı bulur
    public User getUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.getUser(email);
    }
}