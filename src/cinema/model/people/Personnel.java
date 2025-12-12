package cinema.model.people;

import cinema.exception.InvalidInputException;

import java.time.LocalDate;

public abstract class Personnel extends User {
    private int staffID;
    private double hourlyRate; // Çalışanın saatlik ücreti
    private boolean isFullTime; // Çalışanın part time çalışıp çalışmama durumu
    private LocalDate hireDate; // Çalışanın işe alım tarihi

    // Personellerin maaşlarının hesaplanmasında kullanılacak olan sabitler.
    public static final double FULL_TIME_HOURS = 160.0;
    public static final double PART_TIME_HOURS = 80.0;

    public Personnel(String firstName, String lastName, String email, LocalDate dateOfBirth, String password,
                     int staffId, double hourlyRate, boolean isFullTime, LocalDate hireDate) {
        super(firstName, lastName, email, dateOfBirth, password);
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
        if (hireDate.isAfter(LocalDate.now())) {
            throw new InvalidInputException("İşe alım tarihi gelecekte olamaz.");
        }
        this.hireDate = hireDate;
    }

    // Çalışanın aylık ücretini hesaplayan metot
    public abstract double calculateMonthlySalary();
}
