package cinema.repository;

import cinema.model.people.User;

public interface UserRepository {

    void initialize();
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(String email);
    User getUser(String email);
}
