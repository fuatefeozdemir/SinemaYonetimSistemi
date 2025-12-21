package cinema.model;

import cinema.exception.InvalidInputException;

public class Hall {
    private final String hallName; // Salon adı
    private final int rowCount; // Satır sayısı
    private final int columnCount; // Sütun sayısı

    public Hall(String hallName, int rowCount, int columnCount) {
        if (rowCount <= 0 || columnCount <= 0) {
            throw new InvalidInputException("Salon satır ve sütun sayıları negatif olamaz.");
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



