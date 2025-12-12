package cinema.model;

import cinema.exception.SeatOccupiedException;

public class Seat {
    private final int row; // Koltuğun satır ve sütun değişkenleri oluşturulduktan sonra değişmeyeceği için final
    private final int column;
    private boolean isTaken; // Koltuk dolu mu

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;
        this.isTaken = false; // Başlangıçta tüm koltuklar boş olacağından false
    }

    // --- GETTERLAR ---

    public String getSeatCode() {
        char rowLetter = (char) ('A' + row); // Satır numarasını ASCII tablosuna göre harfe çevirir.
        return rowLetter + Integer.toString(column+1); // Sütun numarasını string yapar ve 1 den başlatır.
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isTaken() {
        return isTaken;
    }

    // --- SETTERLAR ---

    // Koltuk dolu değilse doldurur
    public void reserve() {
        if (isTaken) {
            throw new SeatOccupiedException(getSeatCode() + " koltuğu zaten dolu!");
        }
        this.isTaken = true;
    }

    // Koltuğu boşaltır
    public void free() {
        this.isTaken = false;
    }

}

