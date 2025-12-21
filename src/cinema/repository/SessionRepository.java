package cinema.repository;

// yapılacak

import cinema.model.Session;

import java.util.List;

public interface SessionRepository {

    void initialize();
    void saveSession(Session session);
    Session getSession(String id);
    List<Session> getSessionsByMediaName(String mediaName);
    void deleteSession(String film, String hall, String start);
}
