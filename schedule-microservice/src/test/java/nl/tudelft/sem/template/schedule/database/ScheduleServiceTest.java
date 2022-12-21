package nl.tudelft.sem.template.schedule.database;

import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.template.schedule.domain.request.RequestRepository;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;

    private RequestRepository mockRepo;

    @BeforeEach
    public void setUp() {
        this.mockRepo = Mockito.mock(RequestRepository.class);
        this.scheduleService = new ScheduleService(mockRepo);
    }


    @Test
    public void testScheduleRequest() {
        // Create a request to schedule
        ResourcesModel resources = new ResourcesModel();
        resources.setCpu(5);
        resources.setGpu(3);
        resources.setRam(2);
        RequestModelSchedule request = new RequestModelSchedule();
        request.setId(1);
        request.setName("Bink");
        request.setFaculty("CSE");
        request.setDescription("Test");
        request.setResources(resources);
        request.setPlannedDate(LocalDate.of(2022, 12, 25));

        // Create the request that should have been scheduled
        ScheduledRequest expected = new ScheduledRequest(1, "Bink", "Test", "CSE",
                5, 3, 2, LocalDate.of(2022, 12, 25));

        //Capture request that should be saved
        ArgumentCaptor<ScheduledRequest> captor =
                ArgumentCaptor.forClass(ScheduledRequest.class);

        // Schedule the request using the service
        scheduleService.scheduleRequest(request);

        verify(mockRepo).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(expected);
    }

    @Test
    public void testGetSchedule() {
        LocalDate date = LocalDate.of(2023, 02, 04);
        scheduleService.getSchedule(date);
        verify(mockRepo).findByDate(date);
    }

    @Test
    public void testConstructor() {
        ScheduleService scheduleService = new ScheduleService(mockRepo);
        assertThat(mockRepo).isEqualTo(scheduleService.getRequestRepository());
    }
}