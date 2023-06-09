package nl.tudelft.sem.template.schedule.database;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.schedule.domain.request.Request;
import nl.tudelft.sem.template.schedule.domain.request.RequestRepository;
import nl.tudelft.sem.template.schedule.domain.request.Resources;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Tests for the integration of the database with the microservice.
 */
@DataJpaTest
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestRepositoryTest {

    @Autowired
    RequestRepository repo;

    ScheduledRequest request1;
    ScheduledRequest request2;
    ScheduledRequest request3;
    ScheduledRequest request4;
    ScheduledRequest request5;

    /**
     * Set up a few request to use in testing the repository by saving and fetching them.
     */
    @BeforeEach
    void setup() {
        //Request 1
        long id1 = 0;
        String name1 = "Research project";
        String description1 = "Needs a lot of resources";
        String faculty1 = "TN";
        int cpuUsage1 = 6;
        int gpuUsage1 = 2;
        int memoryUsage1 = 3;
        Resources resources1 = new Resources(cpuUsage1, gpuUsage1, memoryUsage1);
        Request request1 = new Request(name1, description1, faculty1, resources1);
        LocalDate deadline1 = LocalDate.of(2022, 12, 19);
        this.request1 = new ScheduledRequest(id1, request1, deadline1);

        //Request 2
        long id2 = 1;
        String name2 = "Small calculations";
        String description2 = "Only needs CPU resources";
        String faculty2 = "TW";
        int cpuUsage2 = 3;
        int gpuUsage2 = 0;
        int memoryUsage2 = 0;
        Resources resources2 = new Resources(cpuUsage2, gpuUsage2, memoryUsage2);
        Request request2 = new Request(name2, description2, faculty2, resources2);
        LocalDate deadline2 = LocalDate.of(2022, 12, 19);
        this.request2 = new ScheduledRequest(id2, request2, deadline2);

        //Request 3
        long id3 = 2;
        String name3 = "Video processing";
        String description3 = "Needs a lot of GPU resources";
        String faculty3 = "IO";
        int cpuUsage3 = 5;
        int gpuUsage3 = 5;
        int memoryUsage3 = 0;
        Resources resources3 = new Resources(cpuUsage3, gpuUsage3, memoryUsage3);
        Request request3 = new Request(name3, description3, faculty3, resources3);
        LocalDate deadline3 = LocalDate.of(2022, 12, 19);
        this.request3 = new ScheduledRequest(id3, request3, deadline3);

        //Request 4
        long id4 = 3;
        String name4 = "Gaming";
        String description4 = "Needs a lot of memory resources";
        String faculty4 = "CSE";
        int cpuUsage4 = 8;
        int gpuUsage4 = 2;
        int memoryUsage4 = 6;
        Resources resources4 = new Resources(cpuUsage4, gpuUsage4, memoryUsage4);
        Request request4 = new Request(name4, description4, faculty4, resources4);
        LocalDate deadline4 = LocalDate.of(2022, 12, 20);
        this.request4 = new ScheduledRequest(id4, request4, deadline4);

        //Request 5
        long id5 = 4;
        String name5 = "Big calculations";
        String description5 = "Needs a bit of all resources";
        String faculty5 = "CSE";
        int cpuUsage5 = 9;
        int gpuUsage5 = 3;
        int memoryUsage5 = 4;
        Resources resources5 = new Resources(cpuUsage5, gpuUsage5, memoryUsage5);
        Request request5 = new Request(name5, description5, faculty5, resources5);
        LocalDate deadline5 = LocalDate.of(2022, 12, 20);
        this.request5 = new ScheduledRequest(id5, request5, deadline5);
    }

    /**
     * Test if the right requests are fetched from a specific date.
     */
    @Test
    @Transactional
    void testFindByDate() {
        repo.save(request1);
        repo.save(request4);
        repo.save(request3);
        List<ScheduledRequest> requestList = repo.findByDate(LocalDate.of(2022, 12, 19));
        assertThat(requestList).containsExactlyInAnyOrder(request1, request3);
    }

    /**
     * Test if no requests are fetched from a specific date where no requests are scheduled.
     */
    @Test
    @Transactional
    void testFindByDateEmpty() {
        repo.save(request1);
        repo.save(request2);
        repo.save(request3);
        List<ScheduledRequest> requestList = repo.findByDate(LocalDate.of(2022, 12, 20));
        assertThat(requestList).isEmpty();
    }
}
