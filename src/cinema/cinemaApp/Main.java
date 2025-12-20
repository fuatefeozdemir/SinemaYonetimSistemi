package cinema.cinemaApp;

import cinema.model.Hall;
import cinema.model.Session;
import cinema.model.Ticket;
import cinema.model.content.*;
import cinema.model.people.Cashier;
import cinema.model.people.Customer;
import cinema.model.people.Manager;
import cinema.model.people.User;
import cinema.service.TicketService;
import cinema.storage.*;

import javax.swing.SwingUtilities;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

                    // --- 1. REPOSITORYLERİN HAZIRLANMASI ---
                    UserRepository userRepo = new UserRepository();
                    MediaRepository mediaRepo = new MediaRepository();
                    HallRepository hallRepo = new HallRepository();
                    SessionRepository sessionRepo = new SessionRepository();
                    TicketRepository ticketRepo = new TicketRepository();

                    // Tabloları sırasıyla oluştur (İlişki sırası önemli!)
                    userRepo.initialize();
                    mediaRepo.initialize();
                    hallRepo.initialize();
                    sessionRepo.initialize();
                    ticketRepo.initialize();

                    // --- 2. KULLANICI GİRİŞLERİ ---
                    Customer customerAli = new Customer("Ali", "Kaya", "ali@mail.com", LocalDate.of(2005, 5, 15), "ali123");
                    customerAli.setId(UUID.randomUUID().toString());
                    userRepo.saveUser(customerAli); // 18 yaş altı (İndirimli bilet testi için)

                    Cashier cashierMehmet = new Cashier("Mehmet", "Demir", "mehmet@sinema.com", LocalDate.of(1990, 1, 1), "m123", 501, 150.0, true, LocalDate.now());
                    cashierMehmet.setId(UUID.randomUUID().toString());
                    userRepo.saveUser(cashierMehmet);

                    // --- 3. SALON VE MEDYA GİRİŞLERİ ---
                    Hall hall1 = new Hall("Salon 1 (Dolby)", 10, 10); // 100 Kişilik
                    hallRepo.saveHall(hall1);

                    Film filmInterstellar = new Standard2D("Interstellar", 169, true, LocalDate.of(2014, 11, 7),
                            "Christopher Nolan", "13+", "Sci-Fi", "English", 8.7f);
                    mediaRepo.saveMedia(filmInterstellar);

                    Trailer interstellarTrailer = new Trailer("Interstellar Teaser", 2, true, "Interstellar");
                    mediaRepo.saveMedia(interstellarTrailer);

                    // --- 4. SEANS OLUŞTURMA ---
                    LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0));
                    LocalDateTime end = start.plusMinutes(filmInterstellar.getDurationMinutes());

                    Session sessionNight = new Session("SESS-001", hall1, filmInterstellar, start, end);
                    sessionRepo.saveSession(sessionNight);

                    // --- 5. BİLET SATIŞ SİMÜLASYONU (TicketService üzerinden) ---
                    TicketService ticketService = new TicketService();

                    // Ali için "B5" koltuğuna bilet alıyoruz
                    // (TicketService içinde: yaş kontrolü yapılır -> fiyat hesaplanır -> seans koltuğu rezerve edilir)
                    Ticket aliBilet = ticketService.buyTicket(sessionNight, customerAli, "B5", cashierMehmet);

                    // Veritabanı Kayıtları
                    ticketRepo.saveTicket(aliBilet);       // Bileti kaydet
                    sessionRepo.updateSeats(sessionNight); // Seansın dolan koltuğunu (B5) güncelle
                    userRepo.updateUser(customerAli);      // Ali'nin kazandığı +5 puanı kaydet
                    userRepo.updateUser(cashierMehmet);    // Kasiyerin satış sayısını güncelle

                    System.out.println("=== TEST VERİLERİ BAŞARIYLA YÜKLENDİ ===");
                    System.out.println("Film: " + filmInterstellar.getName());
                    System.out.println("Müşteri: " + customerAli.getFirstName() + " (Puan: " + customerAli.getLoyaltyPoints() + ")");
                    System.out.println("Bilet No: " + aliBilet.getTicketId() + " | Koltuk: " + aliBilet.getSeatCode());
                    System.out.println("Kasiyer: " + cashierMehmet.getFirstName() + " Satış Sayısı: " + cashierMehmet.getDailyCount());

//                    HallRepository hallRepo = new HallRepository();
//                    MediaRepository mediaRepo = new MediaRepository();
//                    SessionRepository sessionRepo = new SessionRepository();
//
//                    // 2. Örnek Salon (Hall) Oluşturma ve Kaydetme
//                    Hall imaxHall = new Hall("IMAX-01", 10, 12); // 120 kişilik
//                    hallRepo.saveHall(imaxHall);
//
//                    // 3. Örnek Film Oluşturma ve Kaydetme
//                    Film oppenheimer = new Standard2D(
//                            "Oppenheimer", 180, true,
//                            LocalDate.of(2023, 7, 21),
//                            "Christopher Nolan", "13+", "Biography/Drama",
//                            "English", 8.4f
//                    );
//                    mediaRepo.saveMedia(oppenheimer);
//
//                    // 4. Zaman Dilimlerini Ayarlama
//                    // Bugün saat 20:00'de başlasın
//                    LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0));
//                    // Filmin süresine (180 dk) göre bitişi hesapla
//                    LocalDateTime endTime = startTime.plusMinutes(oppenheimer.getDurationMinutes());
//
//                    // 5. Seans Nesnesini Oluşturma
//                    String sessionID = "SESS-" + UUID.randomUUID().toString().substring(0, 8);
//                    Session nightSession = new Session(
//                            sessionID,
//                            imaxHall,
//                            oppenheimer,
//                            startTime,
//                            endTime
//                    );
//
//                    // 6. Bazı koltukları test amaçlı önceden rezerve edelim
//                    nightSession.reserveSeat("A1");
//                    nightSession.reserveSeat("A2");
//                    nightSession.reserveSeat("F5");
//
//                    // 7. Veritabanına Kaydetme
//                    sessionRepo.saveSession(nightSession);
//
//                    System.out.println("Seans başarıyla oluşturuldu!");
//                    System.out.println("ID: " + sessionID);
//                    System.out.println("Başlangıç: " + startTime);
//                    System.out.println("Koltuk Durumu: " + nightSession.getAvailableSeatsCount() + " boş koltuk kaldi");


//            UserRepository. initialize();
//            LocalDate birthDate = LocalDate.of(1995, 5, 19);
//
//// 3. Adım: Customer nesnesini başlatıyoruz
//            String mail = "ahmet.yilmaz@example.com";
//            Customer myCustomer = new Customer(
//                    "Ahmet",
//                    "Yılmaz",
//                    mail,
//                    birthDate,
//                    "Sifre123!",
//                    55
//            );
////
//            UserRepository.saveUser(myCustomer);
//            User dbUser = UserRepository.getUser(mail);
//            Customer customer = (Customer) dbUser;
//            customer.setFirstName("MEMO");
//            customer.setLoyaltyPoints(100);
////            System.out.println(dbUser1.getLoyaltyPoints());
////            dbUser.setFirstName("Mehmet");
//            UserRepository.updateUser(customer);
////            UserStorage.deleteUser(mail);
//
//
//            LocalDate birthDate1 = LocalDate.of(1985, 8, 15);
//            LocalDate hireDate = LocalDate.of(2023, 1, 10);
//
//// 2. Manager nesnesini başlatalım
//            Manager exampleManager = new Manager(
//                    "Murat",                // firstName
//                    "Demir",                // lastName
//                    "murat.demir@sirket.com", // email
//                    birthDate1,              // dateOfBirth (LocalDate)
//                    "GuvenliSifre123!",     // password
//                    1001,                   // staffID (int)
//                    150.50,                 // hourlyRate (double)
//                    true,                   // isFullTime (boolean)
//                    hireDate                // hireDate (LocalDate)
//            );
//            UserRepository.saveUser(exampleManager);
//            Manager dbManager = (Manager) UserRepository.getUser("murat.demir@sirket.com");
//            System.out.println(dbManager.getHourlyRate());
//            dbManager.setHourlyRate(160.60);
//            UserRepository.updateUser(dbManager);
//            Manager dbManager1 = (Manager) UserRepository.getUser("murat.demir@sirket.com");
//            System.out.println(dbManager1.getHourlyRate());
//
//            Cashier newCashier = new Cashier(
//                    "Elif",
//                    "Aydın",
//                    "elif.aydin@sinema.com",
//                    LocalDate.of(1998, 11, 20),
//                    "cashierPass!",
//                    3005,           // staffID
//                    120.0,          // hourlyRate
//                    false,          // isFullTime (Part-time)
//                    LocalDate.now()// hireDate
//            );
//
//// Kaydet
//            UserRepository.saveUser(newCashier);



//            MediaRepository.initDatabase();
//
//            // 1. Film
//            Film film = new Standard2D("Inception", 148, true,
//                    LocalDate.of(2010, 7, 8), "Christopher Nolan",
//                    "13+", "Sci-Fi", "English", 8.8f);
//                MediaRepository.saveMedia(film);
//
//            // 2. Reklam
//            Advertisement ad = new Advertisement("Coca Cola Summer", 1, true, "Coca Cola Company");
//            MediaRepository.saveMedia(ad);
//
//            // 3. Fragman (Filme bağlı)
//            Trailer trailer = new Trailer("Inception Teaser", 2, true, "Inception");
//            MediaRepository.saveMedia(trailer);
//
//            System.out.println("Tüm örnek veriler başarıyla eklendi!");
//
//
//            // 1. Yeni Bir Film (Dune: Part Two)
//            Film dune = new Premium3D(
//                    "Dune: Part Two",
//                    166,
//                    true,
//                    LocalDate.of(2024, 3, 1),
//                    "Denis Villeneuve",
//                    "13+",
//                    "Sci-Fi/Adventure",
//                    "English",
//                    8.6f
//            );
//            MediaRepository.saveMedia(dune);
//
//            // 2. Dune Filmine Bağlı Fragmanlar
//            Trailer duneTeaser = new Trailer("Dune 2 Teaser", 2, true, "Dune: Part Two");
//            Trailer duneOfficial = new Trailer("Dune 2 Official Trailer", 3, true, "Dune: Part Two");
//            MediaRepository.saveMedia(duneTeaser);
//            MediaRepository.saveMedia(duneOfficial);
//
//            // 3. Farklı Bir Reklam (Pepsi Promo)
//            Advertisement pepsiAd = new Advertisement("Pepsi Zero Sugar", 1, true, "PepsiCo");
//            MediaRepository.saveMedia(pepsiAd);
//
//            // 4. Mevcut Veriyi Güncelleme Testi (Inception'ı 4D yapalım)
//            Media inception = MediaRepository.getMedia("Inception");
//            if (inception instanceof Film f) {
//                f.setDurationMinutes(150); // Genişletilmiş versiyon
//                MediaRepository.updateMedia(f);
//                System.out.println("Inception başarıyla 4DX olarak güncellendi.");
//            }

//            AuthService authService = new AuthService();
//            LoginFrame loginFrame = new LoginFrame(authService);
//            loginFrame.setVisible(true);
        });
    }
}