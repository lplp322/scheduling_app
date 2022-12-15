package nl.tudelft.sem.waitinglist.database;

import java.util.List;
import nl.tudelft.sem.waitinglist.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "SELECT p FROM Request p WHERE p.faculty = ?1 ORDER BY p.id ASC")
    List<Request> getRequestByFaculty(String faculty);
}


