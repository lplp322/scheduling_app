package nl.tudelft.sem.template.schedule.domain.request;

import nl.tudelft.sem.common.models.request.ChangeInResourcesModel;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import org.h2.mvstore.tx.TransactionStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
        ScheduledRequest newRequest = new ScheduledRequest(request.getId(), request.getName(),
                request.getDescription(), request.getFaculty(), resources.getCpu(), resources.getGpu(),
                resources.getRam(), request.getPlannedDate(), request.getCreationDate());
        requestRepository.save(newRequest);
    }

    public ResourcesModel dropRequests(ChangeInResourcesModel changeInResources) {
        List<ScheduledRequest> scheduledRequests =  requestRepository.findByDate(changeInResources.getDate());
        scheduledRequests.sort(Comparator.comparing(ScheduledRequest::getCreationDate));
        int missingCpu = changeInResources.getChangedResources().getCpu();
        int missingGpu = changeInResources.getChangedResources().getGpu();
        int missingMemory = changeInResources.getChangedResources().getRam();

        List<ScheduledRequest> droppedRequests = new ArrayList<>();
        for (ScheduledRequest request : scheduledRequests) {
            if (missingCpu >= 0 && missingGpu >= 0 && missingMemory >= 0) {
                break;
            }
            if (missingCpu < 0 || missingGpu < 0 && request.getGpuUsage() > 0
                    || missingMemory < 0 && request.getMemoryUsage() > 0) {
                droppedRequests.add(request);
                missingCpu += request.getCpuUsage();
                missingGpu += request.getGpuUsage();
                missingMemory += request.getMemoryUsage();
            }
        }

        ResourcesModel availableResources = new ResourcesModel(missingCpu, missingGpu, missingMemory);
        return availableResources;
    }

    public RequestRepository getRequestRepository() {
        return requestRepository;
    }
}
