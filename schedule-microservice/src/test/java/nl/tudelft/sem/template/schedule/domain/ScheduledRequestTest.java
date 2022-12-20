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

    @BeforeEach
    void setup() {
        id = 2;
        name = "Video processing";
        description = "Needs a lot of GPU resources";
        faculty = "IO";
        cpuUsage = 5;
        gpuUsage = 5;
        memoryUsage = 0;
        date = LocalDate.of(2022, 12, 19);
        request = new ScheduledRequest(id, name, description, faculty, cpuUsage,
                gpuUsage, memoryUsage, date);
    }

    @Test
    void testConstructorId() {
        assertThat(request.getId()).isEqualTo(id);
    }

    @Test
    void testConstructorName() {
        assertThat(request.getName()).isEqualTo(name);
    }

    @Test
    void testConstructorDescription() {
        assertThat(request.getDescription()).isEqualTo(description);
    }

    @Test
    void testConstructorFaculty() {
        assertThat(request.getFaculty()).isEqualTo(faculty);
    }

    @Test
    void testConstructorCpu() {
        assertThat(request.getCpuUsage()).isEqualTo(cpuUsage);
    }

    @Test
    void testConstructorGpu() {
        assertThat(request.getGpuUsage()).isEqualTo(gpuUsage);
    }

    @Test
    void testConstructorMemory() {
        assertThat(request.getMemoryUsage()).isEqualTo(memoryUsage);
    }

    @Test
    void testConstructorDate() {
        assertThat(request.getDate()).isEqualTo(date);
    }

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
