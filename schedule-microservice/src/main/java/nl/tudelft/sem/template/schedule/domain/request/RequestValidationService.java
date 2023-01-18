package nl.tudelft.sem.template.schedule.domain.request;

import nl.tudelft.sem.common.models.providers.TimeProvider;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.template.schedule.authentication.AuthManager;
import nl.tudelft.sem.template.schedule.external.ResourcesInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A DDD service for checking if a made request is valid.
 */
@Service
public class RequestValidationService {

    private final transient TimeProvider timeProvider;

    private final transient ResourcesInterface resourcesInterface;

    /**
     * Instantiates a new ReqruestValidationService.
     *
     * @param timeProvider Provider of date and time.
     * @param resourcesInterface Interface that can connect to the resources microservice.
     */
    @Autowired
    public RequestValidationService(TimeProvider timeProvider, ResourcesInterface resourcesInterface) {
        this.timeProvider = timeProvider;
        this.resourcesInterface = resourcesInterface;
    }

    /**
     * Throws a ResponseStatusException when the planned date for the request has already passed or if it is the next
     * day when it is already passed 23:55.
     *
     * @param plannedDate The date the request is planned on.
     * @throws ResponseStatusException Exception with message that no requests can be scheduled for this date.
     */
    public void validDate(LocalDate plannedDate) throws ResponseStatusException {
        LocalDate currDate = timeProvider.now().toLocalDate();
        LocalTime currTime = timeProvider.now().toLocalTime();
        if (!plannedDate.isAfter(currDate) || (plannedDate.minusDays(1).isEqual(currDate)
                && currTime.isAfter(LocalTime.of(23, 55)))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot schedule any requests for this date anymore.");
        }
    }

    /**
     * Throws a ResponseStatusException when the request to be scheduled needs more GPU resources than CPU resources
     * or more memory resources than CPU resources, which is not possible.
     *
     * @param requiredResources The resources that are required for the request.
     * @throws ResponseStatusException Exception with message that the resource usage of the request is invalid.
     */
    public void validResources(ResourcesModel requiredResources) throws ResponseStatusException {
        if (requiredResources.getCpu() < requiredResources.getGpu()
                || requiredResources.getCpu() < requiredResources.getRam()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot schedule a request requiring more GPU or memory resources than CPU resources");
        }
    }

    /**
     * Throws an ResponseStatusException when there are not enough available resources for the request to be scheduled
     * on a specific day with resources of a specific faculty (plus free resources), since the request needs more
     * resources.
     *
     * @param requiredResources The resources that are required for the request.
     * @param plannedDate The date the request is planned on.
     * @param faculty The faculty from which the request should use the resources.
     * @throws ResponseStatusException Exception with message that there are not enough resources left for the request.
     */
    public void enoughResources(ResourcesModel requiredResources, LocalDate plannedDate, String faculty)
            throws ResponseStatusException {
        ResourcesModel availableResources = resourcesInterface.getAvailableResources(
                new AvailableResourcesRequestModel(faculty, plannedDate)).getBody();
        if (!requiredResources.enoughAvailable(availableResources)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There are not enough resources available on this date for this request.");
        }
    }
}
