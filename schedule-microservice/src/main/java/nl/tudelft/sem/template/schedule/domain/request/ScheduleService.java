package nl.tudelft.sem.template.schedule.domain.request;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * A DDD service for getting and changing the schedule.
 */
@Service
public class ScheduleService {

    private final transient RequestRepository requestRepository;

    /**
     * Instantiates a new ScheduleService.
     * @param requestRepository The request repository.
     */
    public ScheduleService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<ScheduledRequest> getSchedule(LocalDate date) {
        return requestRepository.findByDate(date);
    }

    public void scheduleRequest(ScheduledRequest request) {
        requestRepository.save(request);
    }
}
