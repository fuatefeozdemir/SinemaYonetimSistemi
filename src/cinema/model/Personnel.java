package cinema.model;

import cinema.exception.InvalidInputException;

import java.time.LocalDate;

public abstract class Personnel extends User {
    private int staffID;
    private double hourlyRate;
    private boolean isFullTime;
    private LocalDate hireDate;

    // Personellerin maaşlarının hesaplanmasında kullanılacak olan sabitler.
    public static final double FULL_TIME_HOURS = 160.0;
    public static final double PART_TIME_HOURS = 80.0;

    public Personnel(String firstName, String lastName, String email, LocalDate dateOfBirth,
                     int staffId, double hourlyRate, boolean isFullTime, LocalDate hireDate) {
        super(firstName, lastName, email, dateOfBirth);

        // Constructor'da setter metotları çağrılarak gerekli kontrollerin yapılması sağlanır.
        setStaffID(staffId);
        setHourlyRate(hourlyRate);
        setFullTime(isFullTime);
        setHireDate(hireDate);
    }

    // --- GETTER METOTLARI ---

    public int getStaffID() {
        return staffID;
    }
    public double getHourlyRate() {
        return hourlyRate;
    }
    public boolean isFullTime() {
        return isFullTime;
    }
    public LocalDate getHireDate() {
        return hireDate;
    }

    // --- SETTER METOTLARI ---

    public void setStaffID(int staffID) {
        if(staffID < 0) {
            throw new InvalidInputException("Staff ID negatif olamaz.");
        }
        this.staffID = staffID;
    }

    public void setHourlyRate(double hourlyRate) {
        if(hourlyRate < 0) {
            throw new InvalidInputException("Saatlik ücret negatif olamaz.");
        }
        this.hourlyRate = hourlyRate;
    }

    public void setFullTime(boolean fullTime) {
        isFullTime = fullTime;
    }

    public void setHireDate(LocalDate hireDate) {
        if (hireDate.isAfter(LocalDate.now())) { // Karşılaştırma
            throw new InvalidInputException("İşe alım tarihi gelecekte olamaz.");
        }
        this.hireDate = hireDate;
    }

    @Override
    public abstract String displayInfo();

    @Override
    public abstract String getRole();

    public abstract double calculateMonthlySalary();
}
