package cinema.model.people;

import cinema.exception.InvalidInputException;

import java.time.LocalDate;

public class Customer extends User {
    private int loyaltyPoints;

    // Varsayılan Constructor (Puan 0'dan başlar)
    public Customer(String firstName, String lastName, String email, LocalDate dateOfBirth, String password) {
        super(firstName, lastName, email, dateOfBirth, password);
        // Setter'ı kullanarak 0 ataması yapılır.
        setLoyaltyPoints(0);
    }

    // Constructor (Overloading) (Veri yükleme senaryosunda kullanılır.)
    public Customer(String firstName, String lastName, String email, LocalDate dateOfBirth, String password, int loyaltyPoints) {
        super(firstName, lastName, email, dateOfBirth, password);

        setLoyaltyPoints(loyaltyPoints);
    }

    // --- GETTER METOTLARI ---

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    // --- SETTER METOTLARI ---

    public void setLoyaltyPoints(int loyaltyPoints) {
        if (loyaltyPoints < 0) {
            throw new InvalidInputException("Sadakat puanları negatif olamaz.");
        }
        this.loyaltyPoints = loyaltyPoints;
    }

    // Müşterinin mevcut puanına puan eklemek için kullanılan metot.

    public void addLoyaltyPoints(int points) {
        if (points <= 0) {
            throw new InvalidInputException("Eklenecek puan 0'dan büyük olmalı.");
        }
        this.loyaltyPoints += points;
    }

    @Override
    public String getRole() {
        return "Müşteri";
    }

    @Override
    public String toString() {
        return "Customer{" +
                "loyaltyPoints=" + loyaltyPoints +
                '}';
    }
}