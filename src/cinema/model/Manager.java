package cinema.model;

import cinema.exception.InvalidInputException;
import java.time.LocalDate;

public class Manager extends Personnel {
    private int responsibleHalls; // Yöneticinin sorumlu olduğu salon sayısı

    private static final double MANAGER_RATE_MULTIPLIER = 1.5; // Yöneticinin maaş katsayısı sabiti

    public Manager(String firstName, String lastName, String email, LocalDate dateOfBirth,
                   int staffId, double hourlyRate, boolean isFullTime, LocalDate hireDate,
                   int responsibleHalls) {

        super(firstName, lastName, email, dateOfBirth, staffId, hourlyRate, isFullTime, hireDate);

        setResponsibleHalls(responsibleHalls);
    }

    // --- GETTER METOTLARI ---

    public int getResponsibleHalls() {
        return responsibleHalls;
    }

    // --- SETTER METOTLARI ---

    public void setResponsibleHalls(int responsibleHalls) {
        // Aralık Kontrolü (Negatif olamaz)
        if (responsibleHalls < 0) {
            throw new InvalidInputException("Sorumlu olunan salon sayısı negatif olamaz.");
        }
        this.responsibleHalls = responsibleHalls;
    }

    @Override
    public String displayInfo() {
        return super.toString() + " | Rol: Yönetici" +
                " | Sorumlu Salon Sayısı: " + responsibleHalls;
    }

    @Override
    public String getRole() {
        return "Yönetici";
    }

    @Override
    public double calculateMonthlySalary() {
        double effectiveHours = isFullTime() ? FULL_TIME_HOURS : PART_TIME_HOURS;

        // Yöneticinin saatlik ücretini katsayı ile çarparak maaşı hesaplar.
        return (getHourlyRate() * MANAGER_RATE_MULTIPLIER) * effectiveHours;
    }
}