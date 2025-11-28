package cinema.model.people;

import cinema.exception.InvalidInputException;

import java.time.LocalDate;

public class Cashier extends Personnel {
    private int dailyCount; // Günlük yapılan bilet satışı

    public Cashier(String firstName, String lastName, String email, LocalDate dateOfBirth,
                   int staffId, double hourlyRate, boolean isFullTime, LocalDate hireDate) {
        super(firstName, lastName, email, dateOfBirth, staffId, hourlyRate, isFullTime, hireDate);

        this.dailyCount = 0; // Günlük satış sayısı 0'dan başlatılır
    }

    // --- GETTER METOTLARI ---

    public int getDailyCount() {
        return dailyCount;
    }

    // --- SETTER METOTLARI ---

    public void setDailyCount(int dailyCount) {
        if(dailyCount < 0) {
            throw new InvalidInputException("Günlük bilet satışı sayısı negatif olamaz.");
        }
        this.dailyCount = dailyCount;
    }

    public void increaseDailyCount() {
        this.dailyCount++;
    }

    // User'dan kalıtılan metot (Kullanıcının bilgilerini döndürür)
    @Override
    public String displayInfo() {
        // super.toString() ile User ve Personnel bilgilerine erişilebilir.
        return super.toString() + " | Staff ID: " + getStaffID() +
                " | İşe alınma: " + getHireDate() + " | Günlük bilet satışı: " + dailyCount;
    }

    // User'dan kalıtılan metot (Kullanıcının rolünü döndürür)
    @Override
    public String getRole() {
        return "Kasiyer";
    }

    // Personnel'den kalıtılan metot (Maaş hesaplama)
    @Override
    public double calculateMonthlySalary() {
        // Part time çalışıp çalışmama durumu kontrol edilir
        double effectiveHours = isFullTime() ? FULL_TIME_HOURS : PART_TIME_HOURS;
        return getHourlyRate() * effectiveHours;
    }
}
