package nl.tudelft.sem.template.schedule.domain.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A DDD repository for querying and persisting scheduled requests.
 */
@Repository
public interface RequestRepository extends JpaRepository<ScheduledRequest, String> {
    /**
     * Find requests by NetID of user.
     */
    List<ScheduledRequest> findByNetId(String netId);

    /**
     * Find requests scheduled on specific date.
     */
    List<ScheduledRequest> findByDate(LocalDate date);

    /**
     * Find used cpu resources on specific date.
     */
    @Query(value = "SELECT SUM(s.cpu_usage) FROM ScheduledRequest s", nativeQuery = true)
    int findCpuUsageByDate(Date date);

    /**
     * Find used gpu resources on specific date.
     */
    @Query(value = "SELECT SUM(s.gpu_usage) FROM ScheduledRequest s", nativeQuery = true)
    int findGpuUsageByDate(Date date);

    /**
     * Find used memory resources on specific date.
     */
    @Query(value = "SELECT SUM(s.memory_usage) FROM ScheduledRequest s", nativeQuery = true)
    int findMemoryUsageByDate(Date date);
}