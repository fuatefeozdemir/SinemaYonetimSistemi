package cinema.service;

import cinema.H2.H2HallRepository;
import cinema.model.Hall;
import cinema.exception.AlreadyExistException;
import cinema.exception.InvalidInputException;
import cinema.repository.HallRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sinema salonlarının yönetimini sağlayan servis
 * Bu sınıftaki bir çok metot ilerde ihtiyaç duyulabilecek sebepler için eklenmiştir ve şuanda kullanılmıyordur.
 */

public class HallService {

    private final HallRepository hallRepository;

    public HallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    // Salon oluşturur
    public void createHall(String name, int rows, int cols) throws AlreadyExistException, InvalidInputException {
        if (name == null || name.trim().isEmpty() || rows <= 0 || cols <= 0) {
            throw new InvalidInputException("Salon adı boş olamaz; satır ve sütun sayıları pozitif olmalıdır.");
        }

        // Aynı isimde salon olup olmadığı kontrol ediliyor
        if (hallRepository.getHall(name) != null) {
            throw new AlreadyExistException("'" + name + "' isimli salon zaten sistemde mevcut.");
        }

        Hall newHall = new Hall(name, rows, cols);
        hallRepository.saveHall(newHall);
    }

    // Tüm salonları liste olarak getirir
    public List<Hall> getAllHalls() {
        return hallRepository.getAllHalls();
    }

    // Salon isimlerini liste olarak getirir
    public List<String> getAllHallNames() {
        return hallRepository.getAllHalls().stream()
                .map(Hall::getHallName)
                .collect(Collectors.toList());
    }

    // Salonun satır ve sütun sayılarını çarparak toplam koltuk sayısını verir
    public int getTotalCapacity(String name) {
        Hall hall = hallRepository.getHall(name);
        return (hall != null) ? (hall.getRowCount() * hall.getColumnCount()) : 0;
    }

    // Salonu günceller
    public void updateHall(String oldName, String newName, int rows, int cols) throws InvalidInputException {
        if (newName == null || newName.trim().isEmpty() || rows <= 0 || cols <= 0) {
            throw new InvalidInputException("Güncelleme başarısız: Bilgiler eksik veya hatalı.");
        }

        hallRepository.updateHall(oldName, newName, rows, cols);
    }

    // Salonu siler
    public void removeHall(String name) {
        hallRepository.deleteHall(name);
    }
}