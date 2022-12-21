package nl.tudelft.sem.waitinglist.domain;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import nl.tudelft.sem.waitinglist.external.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles({"test", "mockClock"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SingleTableWaitingListTest {
    @Autowired
    private RequestRepository repo;
    @Autowired
    private SingleTableWaitingList waitingList;
    @Autowired
    private Clock clock;
    @MockBean
    private UserService userService;
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

        waitingList.removeRequestsForNextDay();

        assertThat(repo.existsById(id1)).isTrue();
        assertThat(repo.existsById(id2)).isFalse();
        assertThat(repo.existsById(id3)).isFalse();

        verify(userService, never()).changeRequestStatus(new ChangeRequestStatus(id1, RequestStatus.REJECTED));
        verify(userService, times(1)).changeRequestStatus(new ChangeRequestStatus(id2, RequestStatus.REJECTED));
        verify(userService, times(1)).changeRequestStatus(new ChangeRequestStatus(id3, RequestStatus.REJECTED));
    }


    @Test
    void addRequestWithExistingId() {
        Request savedRequest = repo.save(request);
        assertThatThrownBy(() -> waitingList.addRequest(savedRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addRequestSuccessfully() {
        Long id = waitingList.addRequest(request);
        assertThat(repo.findById(id).isPresent()).isTrue();
    }

    @Test
    void getAllRequestsEmpty() {
        assertThat(waitingList.getAllRequests().isEmpty());
    }

    @Test
    void getAllRequestsSingle() {
        repo.save(request);
        assertThat(waitingList.getAllRequests().contains(request));
    }

    @Test
    void getAllRequestsMultiple() {
        String name = "name2";
        String description = "description2";
        String faculty = "faculty2";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 14, 14);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request2);
        repo.save(request);
        assertThat(waitingList.getAllRequests().size() == 2);
        assertThat(waitingList.getAllRequests().contains(request2));
        assertThat(waitingList.getAllRequests().contains(request));
    }

    @Test
    void getAllRequestsByFacultyEmpty() {
        repo.save(request);
        assertThat(waitingList.getAllRequestsByFaculty("ewi").isEmpty());
    }

    @Test
    void getAllRequestsByFacultySingle() {
        String name = "name2";
        String description = "description2";
        String faculty = "ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 15, 44);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request2);
        assertThat(waitingList.getAllRequestsByFaculty("ewi").contains(request2));
    }

    @Test
    void getAllRequestsByFacultySingleCorrespond() {
        String name = "name2";
        String description = "description2";
        String faculty = "ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 15, 22);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request2);
        repo.save(request);
        assertThat(waitingList.getAllRequestsByFaculty("ewi").contains(request2));
        assertThat(waitingList.getAllRequestsByFaculty("ewi").size() == 1);
    }

    @Test
    void getAllRequestsByFacultyMultipleCorrespond() {
        String name = "name2";
        String description = "description2";
        String faculty = "ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 23, 22);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request);
        repo.save(request2);
        repo.save(request);
        repo.save(request2);
        assertThat(waitingList.getAllRequestsByFaculty("ewi").contains(request2));
        assertThat(waitingList.getAllRequestsByFaculty("ewi").size() == 2);
    }

    @Test
    void requestNullId() {
        assertThatThrownBy(() -> waitingList.rejectRequest(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAllRequestsWithDeadlineSingle() {
        String name2 = "name2";
        String description2 = "description2";
        String faculty = "ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 23, 22);
        Request request2 = new Request(name2, description2, faculty, resources, deadline, currentDateTime);
        repo.save(request2);
        assertThat(repo.getAllRequestsWithThisDeadline(LocalDate.of(2022, 12, 15)).get(0).getId())
                .isEqualTo(request2.getId());
    }

    @Test
    void getAllRequestsWithDeadlineMultiple() {
        String name2 = "name2";
        String description2 = "description2";
        String faculty = "ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 17);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 23, 22);
        Request request2 = new Request(name2, description2, faculty, resources, deadline, currentDateTime);
        String name3 = "name3";
        String description3 = "description3";
        Resources resources3 = new Resources(6, 5, 1);
        LocalDate deadline3 = LocalDate.of(2022, 12, 17);
        Request request3 = new Request(name3, description3, faculty, resources3, deadline3, currentDateTime);
        repo.save(request);
        repo.save(request2);
        repo.save(request3);
        assertThat(repo.getAllRequestsWithThisDeadline(LocalDate.of(2022, 12, 17)).get(0).getId())
                .isEqualTo(request2.getId());
        assertThat(repo.getAllRequestsWithThisDeadline(LocalDate.of(2022, 12, 17)).get(1).getId())
                .isEqualTo(request3.getId());

    }
}