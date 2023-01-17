package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import nl.tudelft.sem.waitinglist.external.SchedulerService;
import nl.tudelft.sem.waitinglist.external.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "mockClock"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WaitingListAutomaticTasksTest {
    @Autowired
    private RequestRepository repo;
    @Autowired
    private Clock clock;
    @Autowired
    private WaitingListAutomaticTasks waitingListAutomaticTasks;
    @MockBean
    private UserService userService;
    @MockBean
    private SchedulerService schedulerService;

    private Request request;

    @BeforeEach
    void beforeEach() {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 23, 54);
        request = new Request(name, description, faculty, resources, deadline, currentDateTime);
    }

    @Test
    void removeRequestsForNextDay() {
        String name = "name2";
        String description = "description2";
        String faculty = "faculty2";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 14);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 13, 23, 22);

        // ID 1: deadline 15
        repo.save(request);
        Long id1 = repo.save(request).getId();

        // ID 2: deadline 14
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        Long id2 = repo.save(request2).getId();

        // ID 3: deadline 14
        Request request3 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request3);
        Long id3 = repo.save(request3).getId();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isTrue();
        assertThat(repo.existsById(id3)).isTrue();

        // Current date: 13
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        waitingListAutomaticTasks.removeRequestsForNextDay();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isFalse();
        assertThat(repo.existsById(id3)).isFalse();

        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id1, RequestStatus.REJECTED));
        verify(userService, times(1)).changeRequestStatus(new ChangeRequestStatus(id2, RequestStatus.REJECTED));
        verify(userService, times(1)).changeRequestStatus(new ChangeRequestStatus(id3, RequestStatus.REJECTED));
    }

    @Test
    void tryToScheduleInLastSixHoursSingle() {
        String name = "name2";
        String description = "description2";
        String faculty = "faculty2";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 14);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 13, 19, 22);

        // ID 1: deadline 15
        repo.save(request);
        Long id1 = repo.save(request).getId();

        // ID 2: deadline 14
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        Long id2 = repo.save(request2).getId();

        // ID 3: deadline 17
        Request request3 = new Request(name, description, faculty, resources,
                deadline.plusDays(3), currentDateTime.plusDays(3));
        repo.save(request3);
        Long id3 = repo.save(request3).getId();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isTrue();
        assertThat(repo.existsById(id3)).isTrue();

        // Current date: 13
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        waitingListAutomaticTasks.tryToScheduleInLastSixHours();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isFalse();
        assertThat(repo.existsById(id3)).isTrue();

        verify(schedulerService, times(1)).scheduleRequest(any());
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id1, RequestStatus.ACCEPTED));
        verify(userService, times(1)).changeRequestStatus(new ChangeRequestStatus(id2, RequestStatus.ACCEPTED));
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id3, RequestStatus.ACCEPTED));
    }

    @Test
    void tryToScheduleInLastSixHoursMultiple() {
        String name = "name2";
        String description = "description2";
        String faculty = "faculty2";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 14);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 13, 19, 22);

        // ID 1: deadline 15
        repo.save(request);
        Long id1 = repo.save(request).getId();

        // ID 2: deadline 14
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        Long id2 = repo.save(request2).getId();

        // ID 3: deadline 14
        Request request3 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request3);
        Long id3 = repo.save(request3).getId();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isTrue();
        assertThat(repo.existsById(id3)).isTrue();

        // Current date: 13
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        waitingListAutomaticTasks.tryToScheduleInLastSixHours();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isFalse();
        assertThat(repo.existsById(id3)).isFalse();

        verify(schedulerService, times(2)).scheduleRequest(any());
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id1, RequestStatus.ACCEPTED));
        verify(userService, times(1)).changeRequestStatus(new ChangeRequestStatus(id2, RequestStatus.ACCEPTED));
        verify(userService, times(1)).changeRequestStatus(new ChangeRequestStatus(id3, RequestStatus.ACCEPTED));
    }

    @Test
    void tryToScheduleInLastSixHoursSchedulerFull() {
        String name = "name2";
        String description = "description2";
        String faculty = "faculty2";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 14);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 13, 19, 22);

        // ID 1: deadline 15
        repo.save(request);
        Long id1 = repo.save(request).getId();

        // ID 2: deadline 14
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        Long id2 = repo.save(request2).getId();

        // ID 3: deadline 14
        Request request3 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request3);
        Long id3 = repo.save(request3).getId();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isTrue();
        assertThat(repo.existsById(id3)).isTrue();

        // Current date: 13
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.badRequest().build());

        waitingListAutomaticTasks.tryToScheduleInLastSixHours();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isTrue();
        assertThat(repo.existsById(id3)).isTrue();

        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id1, RequestStatus.ACCEPTED));
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id2, RequestStatus.ACCEPTED));
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id3, RequestStatus.ACCEPTED));
    }

    @Test
    void tryToScheduleInLastSixHoursSchedulerFullCheckOnlySmallerRequests() {
        String name = "name2";
        String description = "description2";
        String faculty = "faculty2";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 14);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 13, 19, 22);

        // ID 1: deadline 15
        repo.save(request);
        Long id1 = repo.save(request).getId();

        // ID 2: deadline 14
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        Long id2 = repo.save(request2).getId();

        // ID 3: deadline 14
        Request request3 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request3);
        Long id3 = repo.save(request3).getId();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isTrue();
        assertThat(repo.existsById(id3)).isTrue();

        // Current date: 13
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.badRequest().build());

        waitingListAutomaticTasks.tryToScheduleInLastSixHours();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isTrue();
        assertThat(repo.existsById(id3)).isTrue();

        verify(schedulerService, times(1)).scheduleRequest(any());
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id1, RequestStatus.ACCEPTED));
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id2, RequestStatus.ACCEPTED));
        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id3, RequestStatus.ACCEPTED));
    }
}