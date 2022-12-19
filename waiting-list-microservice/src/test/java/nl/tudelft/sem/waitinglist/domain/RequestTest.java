package nl.tudelft.sem.waitinglist.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import org.junit.jupiter.api.Test;

class RequestTest {
    private final String name = "name";
    private final String description = "description";
    private final String faculty = "faculty";
    private final Resources resources = new Resources(6, 5, 1);
    private final LocalDate deadline = LocalDate.of(2022, 12, 15);
    private final LocalDate currentDate = LocalDate.of(2022, 12, 14);

    private final ResourcesModel resourcesModel = new ResourcesModel(6, 5, 1);
    private final RequestModel requestModel = new RequestModel(name, description, faculty, resourcesModel, deadline);

    @Test
    void nullName() {
        assertThatThrownBy(() -> new Request(null, description, faculty, resources, deadline, currentDate))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void blankName() {
        assertThatThrownBy(() -> new Request(" ", description, faculty, resources, deadline, currentDate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullDescription() {
        assertThatThrownBy(() -> new Request(name, null, faculty, resources, deadline, currentDate))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void blankDescription() {
        assertThatThrownBy(() -> new Request(name, "", faculty, resources, deadline, currentDate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullFaculty() {
        assertThatThrownBy(() -> new Request(name, description, null, resources, deadline, currentDate))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void blankFaculty() {
        assertThatThrownBy(() -> new Request(name, description, "", resources, deadline, currentDate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullResources() {
        assertThatThrownBy(() -> new Request(name, description, faculty, null, deadline, currentDate))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullCurrentDate() {
        assertThatThrownBy(() -> new Request(name, description, faculty, resources, deadline, null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Request(requestModel, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deadlineSameAsCurrentDate() {
        assertThatThrownBy(() -> new Request(name, description, faculty, resources, currentDate, currentDate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deadlineInPast() {
        LocalDate pastDate = currentDate.minusDays(1);
        assertThatThrownBy(() -> new Request(name, description, faculty, resources, pastDate, currentDate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructCorrectObject() {
        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        correctFields(request);
    }

    @Test
    void deadlineCanBeNull() {
        Request request = new Request(name, description, faculty, resources, null, currentDate);
        assertThat(request.getDeadline()).isNull();
    }

    @Test
    void nullRequestModel() {
        assertThatThrownBy(() -> new Request(null, currentDate))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void fromRequestModel() {
        Request request = new Request(requestModel, currentDate);
        correctFields(request);
    }

    void correctFields(Request request) {
        assertThat(request.getName()).isEqualTo(name);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getFaculty()).isEqualTo(faculty);
        assertThat(request.getDeadline()).isEqualTo(deadline);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
    }
}