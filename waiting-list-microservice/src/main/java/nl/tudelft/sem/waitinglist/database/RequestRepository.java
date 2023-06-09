package nl.tudelft.sem.waitinglist.database;

import java.time.LocalDate;
import java.util.List;
import nl.tudelft.sem.waitinglist.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;


@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "SELECT p FROM Request p WHERE p.faculty = ?1 ORDER BY p.id ASC")
    List<Request> getRequestByFaculty(String faculty);

    @Query(value = "SELECT p FROM Request p WHERE p.id = ?1")
    Request getRequestById(Long id);

    @Query(value = "SELECT p FROM Request p WHERE p.deadline = ?1 ORDER BY p.id ASC")
    List<Request> getAllRequestsByDeadline(LocalDate deadline);

    @Transactional
    List<Request> deleteByDeadline(LocalDate deadline);
}


