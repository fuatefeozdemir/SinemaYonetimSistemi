package cinema.util;

import cinema.model.Ticket;
import cinema.model.Session;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

// Satın alınan biletin detaylarını içeren txt formatında fiş oluşturan yardımcı sınıf

public class TicketPrinter {

    private static final String SAVE_PATH = "tickets_receipts/";

    public static boolean printToText(Ticket ticket, Session session) {
        // Kayıt klasörü yoksa oluşturur
        File folder = new File(SAVE_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Bilet idsini kullanarak çakışmayı önleyecek dosya adı oluşturur
        String fileName = SAVE_PATH + "ticket_" + ticket.getTicketId().substring(0, 8) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("==========================================");
            writer.newLine();
            writer.write("                SİNEMA BİLETİ             ");
            writer.newLine();
            writer.write("==========================================");
            writer.newLine();

            String film = (session != null) ? session.getFilm().getName().toUpperCase() : "Bilinmiyor";
            String time = (session != null) ? session.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "--";
            String hall = (session != null) ? session.getHall().getHallName() : "-";

            writer.write("FİLM      : " + film);
            writer.newLine();
            writer.write("TARİH/SAAT: " + time);
            writer.newLine();
            writer.write("SALON     : " + hall);
            writer.newLine();
            writer.write("KOLTUK    : " + ticket.getSeatCode());
            writer.newLine();
            writer.write("------------------------------------------");
            writer.newLine();

            writer.write("MÜŞTERİ   : " + ticket.getCustomer());
            writer.newLine();
            writer.write("FİYAT     : " + String.format("%.2f", ticket.getFinalPrice()) + " TL");
            writer.newLine();
            writer.write("BİLET NO  : " + ticket.getTicketId());
            writer.newLine();
            writer.write("==========================================");

            return true;
        } catch (IOException e) {
            System.err.println("Bilet yazdırılırken bir dosya hatası oluştu: " + e.getMessage());
            return false;
        }
    }
}