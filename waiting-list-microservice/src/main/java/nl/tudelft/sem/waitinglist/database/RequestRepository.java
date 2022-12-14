package nl.tudelft.sem.waitinglist.database;

import nl.tudelft.sem.waitinglist.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "SELECT p FROM Request p WHERE p.faculty = ?1 ORDER BY p.id ASC")
    List<Request> getRequestByByFaculty(String faculty);
}


