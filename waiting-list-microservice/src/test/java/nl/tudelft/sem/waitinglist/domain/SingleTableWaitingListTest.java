package nl.tudelft.sem.waitinglist.domain;

import java.time.LocalDate;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
class SingleTableWaitingListTest {
    @Autowired
    private RequestRepository repo;
    @Autowired
    private SingleTableWaitingList waitingList;
    private Request request;

    @BeforeEach
    void beforeEach() {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        request = new Request(name, description, faculty, resources, deadline, currentDate);

        repo.deleteAll();
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
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDate);
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
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDate);
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
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDate);
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
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        Request request2 = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);
        repo.save(request2);
        repo.save(request);
        repo.save(request2);
        assertThat(waitingList.getAllRequestsByFaculty("ewi").contains(request2));
        assertThat(waitingList.getAllRequestsByFaculty("ewi").size() == 2);
    }

}