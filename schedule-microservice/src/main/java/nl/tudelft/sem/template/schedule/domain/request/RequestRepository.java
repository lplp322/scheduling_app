package nl.tudelft.sem.template.schedule.domain.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * A DDD repository for querying and persisting scheduled requests.
 */
@Repository
public interface RequestRepository extends JpaRepository<ScheduledRequest, String> {

    /**
     * Find requests scheduled on specific date.
     */
    List<ScheduledRequest> findByDate(LocalDate date);
}