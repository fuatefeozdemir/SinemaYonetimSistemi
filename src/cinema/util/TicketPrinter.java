package cinema.util;

import cinema.model.Ticket;
import cinema.model.Session;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TicketPrinter {

    private static final String SAVE_PATH = "tickets_receipts/";

    public static boolean printToText(Ticket ticket, Session session) {
        File folder = new File(SAVE_PATH);
        if (!folder.exists()) folder.mkdirs();

        String fileName = SAVE_PATH + "ticket_" + ticket.getTicketId().substring(0, 8) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("==========================================");
            writer.newLine();
            writer.write("           SİNEMA BİLETİ / RECEIPT        ");
            writer.newLine();
            writer.write("==========================================");
            writer.newLine();
            writer.write("FİLM      : " + (session != null ? session.getFilm().getName().toUpperCase() : "Bilinmiyor"));
            writer.newLine();
            writer.write("TARİH/SAAT: " + (session != null ? session.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "--"));
            writer.newLine();
            writer.write("SALON     : " + (session != null ? session.getHall().getHallName() : "-"));
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
            e.printStackTrace();
            return false;
        }
    }
}