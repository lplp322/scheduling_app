package nl.tudelft.sem.template.schedule.domain;

import nl.tudelft.sem.template.schedule.domain.request.Request;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.template.schedule.domain.request.Resources;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the ScheduledRequest class.
 */
@SpringBootTest
public class ScheduledRequestTest {

    ScheduledRequest scheduledRequest;

    Request request;
    Resources resources;

    long id;
    String name;
    String description;
    String faculty;
    int cpuUsage;
    int gpuUsage;
    int memoryUsage;
    LocalDate date;

    /**
     * Sets up a ScheduledRequest object to do tests on.
     */
    @BeforeEach
    void setup() {
        id = 2;
        name = "John";
        description = "Needs a lot of GPU resources";
        faculty = "IO";
        cpuUsage = 5;
        gpuUsage = 5;
        memoryUsage = 0;
        resources = new Resources(cpuUsage, gpuUsage, memoryUsage);
        request = new Request(name, description, faculty, resources);
        date = LocalDate.of(2022, 12, 19);
        scheduledRequest = new ScheduledRequest(id, request, date);
    }

    /**
     * Test if the id of the request is correctly set upon construction.
     */
    @Test
    void testConstructorId() {
        assertThat(scheduledRequest.getId()).isEqualTo(id);
    }

    /**
     * Test if the request information of the scheduled request is correctly set upon construction.
     */
    @Test
    void testConstructorRequest() {
        assertThat(scheduledRequest.getRequest()).isEqualTo(request);
    }

    /**
     * Test if the date of the request is correctly set upon construction.
     */
    @Test
    void testConstructorDate() {
        assertThat(scheduledRequest.getDate()).isEqualTo(date);
    }

    /**
     * Test if a ScheduledRequest object is correctly converted in a RequestModelSchedule object.
     */
    @Test
    void testConvert() {
        RequestModelSchedule requestModel = new RequestModelSchedule(id, name, description, faculty,
                new ResourcesModel(cpuUsage, gpuUsage, memoryUsage), date);
        assertThat(scheduledRequest.convert()).isEqualTo(requestModel);
    }

    @Test
    void testSameRequestsAreEqual() {
        assertTrue(request.equals(request));
    }
}
