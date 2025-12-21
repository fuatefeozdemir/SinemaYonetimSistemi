package cinema.model.people;

import cinema.exception.InvalidInputException;

import java.time.LocalDate;

public class Cashier extends Personnel {
    private int staffID;
    private double hourlyRate; // Çalışanın saatlik ücreti
    private boolean isFullTime; // Çalışanın part time çalışıp çalışmama durumu
    private LocalDate hireDate; // Çalışanın işe alım tarihi

    private int dailyCount; // Günlük yapılan bilet satışı

    public Cashier(String firstName, String lastName, String email, LocalDate dateOfBirth, String password,
                   int staffId, double hourlyRate, boolean isFullTime, LocalDate hireDate) {
        super(firstName, lastName, email, dateOfBirth,password, staffId, hourlyRate, isFullTime, hireDate);

        this.dailyCount = 0; // Günlük satış sayısı 0'dan başlatılır
    }

    public int getDailyCount() {
        return dailyCount;
    }

    public void setDailyCount(int dailyCount) {
        if(dailyCount < 0) {
            throw new InvalidInputException("Günlük bilet satışı sayısı negatif olamaz.");
        }
        this.dailyCount = dailyCount;
    }

    public void increaseDailyCount() {
        this.dailyCount++;
    }

    @Override
    public String getRole() {
        return "Kasiyer";
    }

    // Maaş hesaplama metotu
    @Override
    public double calculateMonthlySalary() {
        // Part time çalışıp çalışmama durumu kontrol edilir
        double effectiveHours = isFullTime() ? FULL_TIME_HOURS : PART_TIME_HOURS;
        return getHourlyRate() * effectiveHours;
    }
}
