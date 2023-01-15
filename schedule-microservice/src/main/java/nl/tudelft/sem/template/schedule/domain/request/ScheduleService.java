package nl.tudelft.sem.template.schedule.domain.request;

import nl.tudelft.sem.common.models.request.RequestModel;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * A DDD service for getting and changing the schedule.
 */
@Service
public class ScheduleService {

    private final transient RequestRepository requestRepository;

    /**
     * Instantiates a new ScheduleService.
     *
     * @param requestRepository The request repository.
     */
    public ScheduleService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Get the schedule on a specific date from the repository.
     *
     * @param date The date from which the schedule should be fetched.
     * @return The list of requests scheduled at the specific date.
     */
    public List<ScheduledRequest> getSchedule(LocalDate date) {
        return requestRepository.findByDate(date);
    }

    /**
     * Saves the request to the repository of scheduled requests.
     *
     * @param request The request that should be saved.
     */
    public void scheduleRequest(RequestModelSchedule request) {
        ResourcesModel resources = request.getResources();
        ScheduledRequest newRequest = new ScheduledRequest(request.getId(), new RequestModel(request.getName(),
                request.getDescription(), request.getFaculty(), new ResourcesModel(resources.getCpu(),
                resources.getGpu(), resources.getRam())), request.getPlannedDate());
        requestRepository.save(newRequest);
    }

    public RequestRepository getRequestRepository() {
        return requestRepository;
    }
}
