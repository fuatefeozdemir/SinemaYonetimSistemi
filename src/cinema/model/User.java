package cinema.model;

import cinema.exception.InvalidInputException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class User {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;

    public User(String firstName, String lastName, String email, LocalDate dateOfBirth) {
        // Constructor'da setter metotları çağrılarak gerekli kontrollerin yapılması sağlanır.
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setDateOfBirth(dateOfBirth);
    }

    // --- GETTER METOTLARI ---
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    // --- SETTER METOTLARI ---
    public void setFirstName(String firstName) {
        if (firstName == null) {
            throw new InvalidInputException("İsim null olamaz.");
        }

        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (lastName == null) {
            throw new InvalidInputException("Soyisim null olamaz.");
        }
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        if (email == null) {
            throw new InvalidInputException("Email null olamaz.");
        }
        this.email = email;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new InvalidInputException("Doğum tarihi null olamaz.");
        }

        // Time sınıfındaki bu metot ile 2 tarih arasındaki yıl sayısı bulunarak yaş değişkeni oluşturulur.
        long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());

        // Kullanıcının yaşının gerekli yaştan büyük olup olmama durumuna bakılır.
        if (age < 16) {
            throw new InvalidInputException("Kullanıcı yaşı 16'dan küçük olamaz.");
        }
        this.dateOfBirth = dateOfBirth;
    }

    // Kullanıcının verilerinin gerekli formatta gösterilmesini sağlar.
    public abstract String displayInfo();

    // Kullanıcının rolünün(Çalışan/Müşteri) döndürülmesini sağlar.
    public abstract String getRole();
}
