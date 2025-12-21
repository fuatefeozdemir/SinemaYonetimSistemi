package cinema.repository;

// yapılacak

import cinema.model.Hall;

import java.util.List;

public interface HallRepository {

    void initialize();
    void saveHall(Hall hall);
    Hall getHall(String name);
    void updateHall(String oldName, String newName, int rows, int cols);
    List<Hall> getAllHalls();
    void deleteHall(String name);
}
