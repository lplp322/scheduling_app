package nl.tudelft.sem.template.schedule.domain.request;

import nl.tudelft.sem.common.models.request.RequestModel;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public void scheduleRequest(RequestModel request) {
        ResourcesModel resources = request.getResources();
        ScheduledRequest newRequest = new ScheduledRequest(request.getId(), request.getName(),
                request.getDescription(), resources.getCpu(), resources.getGpu(), resources.getRam(),
                request.getDeadline(), request.getNetId());
        requestRepository.save(newRequest);
    }
}
