package cinema.model;

import cinema.exception.InvalidInputException;


public class Hall {

    // Salonun alanları
    private final String hallName;
    private final int rowCount;
    private final int columnCount;

    public Hall(String hallName, int rowCount, int columnCount) {

        // Salon boyutlarını kontrol eder
        if (rowCount <= 0 || columnCount <= 0) {
            throw new InvalidInputException("Hata: Satır ve sütun sayıları 0'dan büyük olmalıdır.");
        }

        this.hallName = hallName;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }

    public String getHallName() {
        return hallName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }
}