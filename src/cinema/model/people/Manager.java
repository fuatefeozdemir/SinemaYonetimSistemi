package cinema.model.people;

import cinema.exception.InvalidInputException;

import java.time.LocalDate;

public class Manager extends Personnel {

    private static final double MANAGER_RATE_MULTIPLIER = 1.5; // Yöneticinin maaş katsayısı sabiti

    public Manager(String firstName, String lastName, String email, LocalDate dateOfBirth, String password,
                   int staffId, double hourlyRate, boolean isFullTime, LocalDate hireDate) {
        super(firstName, lastName, email, dateOfBirth, password, staffId, hourlyRate, isFullTime, hireDate);
    }

    @Override
    public String getRole() {
        return "Yönetici";
    }

    // Maaş hesaplama metotu
    @Override
    public double calculateMonthlySalary() {
        // Part time çalışıp çalışmama durumu kontrol edilir
        double effectiveHours = isFullTime() ? FULL_TIME_HOURS : PART_TIME_HOURS;

        // Yöneticinin saatlik ücreti katsayı ile çarpılarak maaşı hesaplanır
        return (getHourlyRate() * MANAGER_RATE_MULTIPLIER) * effectiveHours;
    }
}