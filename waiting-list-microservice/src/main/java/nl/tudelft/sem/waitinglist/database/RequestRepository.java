package nl.tudelft.sem.waitinglist.database;

import nl.tudelft.sem.waitinglist.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
}
