package nl.tudelft.sem.template.schedule.domain;

import nl.tudelft.sem.common.models.providers.TimeProvider;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.template.schedule.domain.request.RequestValidationService;
import nl.tudelft.sem.template.schedule.external.ResourcesInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the request validation service.
 */
@SpringBootTest
public class RequestValidationServiceTest {

    @Autowired
    RequestValidationService requestValidationService;

    @Autowired
    private TimeProvider mockTime;

    @Autowired
    private ResourcesInterface mockResourcesInterface;

    /**
     * Mocking the repository to keep it unit tests.
     */
    @BeforeEach
    public void setUp() {
        this.mockTime = Mockito.mock(TimeProvider.class);
        this.mockResourcesInterface = Mockito.mock(ResourcesInterface.class);
        this.requestValidationService = new RequestValidationService(mockTime, mockResourcesInterface);
    }

    @Test
    void validDateTest() {
        LocalDateTime currDateTime = LocalDateTime.of(2023, 01, 17, 23, 56);
        when(mockTime.now()).thenReturn(currDateTime);

        LocalDate plannedDate = LocalDate.of(2023, 01, 19);
        assertDoesNotThrow(() -> requestValidationService.validDate(plannedDate));
    }

    @Test
    void passedDateTest() {
        LocalDateTime currDateTime = LocalDateTime.of(2023, 01, 17, 00, 00);
        when(mockTime.now()).thenReturn(currDateTime);

        LocalDate plannedDate = LocalDate.of(2023, 01, 17);
        assertThrows(ResponseStatusException.class, () -> requestValidationService.validDate(plannedDate));
    }

    @Test
    void justTooLateTest() {
        LocalDateTime currDateTime = LocalDateTime.of(2023, 01, 17, 23, 56);
        when(mockTime.now()).thenReturn(currDateTime);

        LocalDate plannedDate = LocalDate.of(2023, 01, 18);
        assertThrows(ResponseStatusException.class, () -> requestValidationService.validDate(plannedDate));
    }

    @Test
    void validResources() {
        ResourcesModel requiredResources = new ResourcesModel(5, 5, 5);
        assertDoesNotThrow(() -> requestValidationService.validResources(requiredResources));
    }

    @Test
    void moreGpuThanCpuResources() {
        ResourcesModel requiredResources = new ResourcesModel(5, 6, 5);
        assertThrows(ResponseStatusException.class, () -> requestValidationService.validResources(requiredResources));
    }

    @Test
    void moreMemoryThanCpuResources() {
        ResourcesModel requiredResources = new ResourcesModel(5, 5, 6);
        assertThrows(ResponseStatusException.class, () -> requestValidationService.validResources(requiredResources));
    }

    @Test
    void enoughResources() {
        String faculty = "EEMCS";
        LocalDate plannedDate = LocalDate.of(2023, 01, 18);
        ResourcesModel availableResources = new ResourcesModel(5, 5, 5);
        when(mockResourcesInterface.getAvailableResources(new AvailableResourcesRequestModel(faculty, plannedDate)))
                .thenReturn(ResponseEntity.ok(availableResources));

        ResourcesModel requiredResources = new ResourcesModel(5, 5, 5);
        assertDoesNotThrow(() -> requestValidationService.enoughResources(requiredResources, plannedDate, faculty));
    }

    @Test
    void notEnoughCpu() {
        String faculty = "EEMCS";
        LocalDate plannedDate = LocalDate.of(2023, 01, 18);
        ResourcesModel availableResources = new ResourcesModel(4, 5, 5);
        when(mockResourcesInterface.getAvailableResources(new AvailableResourcesRequestModel(faculty, plannedDate)))
                .thenReturn(ResponseEntity.ok(availableResources));

        ResourcesModel requiredResources = new ResourcesModel(5, 5, 5);
        assertThrows(ResponseStatusException.class, () -> requestValidationService.enoughResources(requiredResources,
                plannedDate, faculty));
    }

    @Test
    void notEnoughGpu() {
        String faculty = "EEMCS";
        LocalDate plannedDate = LocalDate.of(2023, 01, 18);
        ResourcesModel availableResources = new ResourcesModel(5, 4, 5);
        when(mockResourcesInterface.getAvailableResources(new AvailableResourcesRequestModel(faculty, plannedDate)))
                .thenReturn(ResponseEntity.ok(availableResources));

        ResourcesModel requiredResources = new ResourcesModel(5, 5, 5);
        assertThrows(ResponseStatusException.class, () -> requestValidationService.enoughResources(requiredResources,
                plannedDate, faculty));
    }

    @Test
    void notEnoughMemory() {
        String faculty = "EEMCS";
        LocalDate plannedDate = LocalDate.of(2023, 01, 18);
        ResourcesModel availableResources = new ResourcesModel(5, 5, 4);
        when(mockResourcesInterface.getAvailableResources(new AvailableResourcesRequestModel(faculty, plannedDate)))
                .thenReturn(ResponseEntity.ok(availableResources));

        ResourcesModel requiredResources = new ResourcesModel(5, 5, 5);
        assertThrows(ResponseStatusException.class, () -> requestValidationService.enoughResources(requiredResources,
                plannedDate, faculty));
    }
}

