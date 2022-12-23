package nl.tudelft.sem.template.schedule.domain;

import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.ResourcesModel;
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

    ScheduledRequest request;

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
        date = LocalDate.of(2022, 12, 19);
        request = new ScheduledRequest(id, name, description, faculty, cpuUsage,
                gpuUsage, memoryUsage, date);
    }

    /**
     * Test if the id of the request is correctly set upon construction.
     */
    @Test
    void testConstructorId() {
        assertThat(request.getId()).isEqualTo(id);
    }

    /**
     * Test if the name of the request is correctly set upon construction.
     */
    @Test
    void testConstructorName() {
        assertThat(request.getName()).isEqualTo(name);
    }

    /**
     * Test if the description of the request is correctly set upon construction.
     */
    @Test
    void testConstructorDescription() {
        assertThat(request.getDescription()).isEqualTo(description);
    }

    /**
     * Test if the faculty of the request is correctly set upon construction.
     */
    @Test
    void testConstructorFaculty() {
        assertThat(request.getFaculty()).isEqualTo(faculty);
    }

    /**
     * Test if the CPU usage of the request is correctly set upon construction.
     */
    @Test
    void testConstructorCpu() {
        assertThat(request.getCpuUsage()).isEqualTo(cpuUsage);
    }

    /**
     * Test if the GPU usage of the request is correctly set upon construction.
     */
    @Test
    void testConstructorGpu() {
        assertThat(request.getGpuUsage()).isEqualTo(gpuUsage);
    }

    /**
     * Test if the memory usage of the request is correctly set upon construction.
     */
    @Test
    void testConstructorMemory() {
        assertThat(request.getMemoryUsage()).isEqualTo(memoryUsage);
    }

    /**
     * Test if the date of the request is correctly set upon construction.
     */
    @Test
    void testConstructorDate() {
        assertThat(request.getDate()).isEqualTo(date);
    }

    /**
     * Test if a ScheduledRequest object is correctly converted in a RequestModelSchedule object.
     */
    @Test
    void testConvert() {
        RequestModelSchedule requestModel = new RequestModelSchedule(id, name, description, faculty,
                new ResourcesModel(cpuUsage, gpuUsage, memoryUsage), date);
        assertThat(request.convert()).isEqualTo(requestModel);
    }

    @Test
    void testSameRequestsAreEqual() {
        assertTrue(request.equals(request));
    }
}
