package cinema.cinemaApp;

import cinema.model.content.*;
import cinema.model.people.Cashier;
import cinema.model.people.Customer;
import cinema.model.people.Manager;
import cinema.model.people.User;
import cinema.storage.MediaRepository;
import cinema.storage.UserRepository;

import javax.swing.SwingUtilities;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

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



            MediaRepository.initDatabase();

            // 1. Film
            Film film = new Standard2D("Inception", 148, true,
                    LocalDate.of(2010, 7, 8), "Christopher Nolan",
                    "13+", "Sci-Fi", "English", 8.8f);
                MediaRepository.saveMedia(film);

            // 2. Reklam
            Advertisement ad = new Advertisement("Coca Cola Summer", 1, true, "Coca Cola Company");
            MediaRepository.saveMedia(ad);

            // 3. Fragman (Filme bağlı)
            Trailer trailer = new Trailer("Inception Teaser", 2, true, "Inception");
            MediaRepository.saveMedia(trailer);

            System.out.println("Tüm örnek veriler başarıyla eklendi!");


            // 1. Yeni Bir Film (Dune: Part Two)
            Film dune = new Premium3D(
                    "Dune: Part Two",
                    166,
                    true,
                    LocalDate.of(2024, 3, 1),
                    "Denis Villeneuve",
                    "13+",
                    "Sci-Fi/Adventure",
                    "English",
                    8.6f
            );
            MediaRepository.saveMedia(dune);

            // 2. Dune Filmine Bağlı Fragmanlar
            Trailer duneTeaser = new Trailer("Dune 2 Teaser", 2, true, "Dune: Part Two");
            Trailer duneOfficial = new Trailer("Dune 2 Official Trailer", 3, true, "Dune: Part Two");
            MediaRepository.saveMedia(duneTeaser);
            MediaRepository.saveMedia(duneOfficial);

            // 3. Farklı Bir Reklam (Pepsi Promo)
            Advertisement pepsiAd = new Advertisement("Pepsi Zero Sugar", 1, true, "PepsiCo");
            MediaRepository.saveMedia(pepsiAd);

            // 4. Mevcut Veriyi Güncelleme Testi (Inception'ı 4D yapalım)
            Media inception = MediaRepository.getMedia("Inception");
            if (inception instanceof Film f) {
                f.setDurationMinutes(150); // Genişletilmiş versiyon
                MediaRepository.updateMedia(f);
                System.out.println("Inception başarıyla 4DX olarak güncellendi.");
            }

//            AuthService authService = new AuthService();
//            LoginFrame loginFrame = new LoginFrame(authService);
//            loginFrame.setVisible(true);
        });
    }
}