package nl.tudelft.sem.waitinglist.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}