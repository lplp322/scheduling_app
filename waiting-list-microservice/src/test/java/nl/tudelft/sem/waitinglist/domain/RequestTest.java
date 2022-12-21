package nl.tudelft.sem.waitinglist.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;

import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.RequestModelWaitingListId;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import org.junit.jupiter.api.Test;

class RequestTest {
    private final String name = "name";
    private final String description = "description";
    private final String faculty = "faculty";
    private final Resources resources = new Resources(6, 5, 1);
    private final LocalDate deadline = LocalDate.of(2022, 12, 15);
    private final LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 15, 24);

    private final ResourcesModel resourcesModel = new ResourcesModel(6, 5, 1);
    private final RequestModelWaitingList requestModel = new RequestModelWaitingList(name, description,
            faculty, resourcesModel, deadline);

    @Test
    void nullName() {
        assertThatThrownBy(() -> new Request(null, description, faculty, resources, deadline, currentDateTime))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void blankName() {
        assertThatThrownBy(() -> new Request(" ", description, faculty, resources, deadline, currentDateTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullDescription() {
        assertThatThrownBy(() -> new Request(name, null, faculty, resources, deadline, currentDateTime))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void blankDescription() {
        assertThatThrownBy(() -> new Request(name, "", faculty, resources, deadline, currentDateTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullFaculty() {
        assertThatThrownBy(() -> new Request(name, description, null, resources, deadline, currentDateTime))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void blankFaculty() {
        assertThatThrownBy(() -> new Request(name, description, "", resources, deadline, currentDateTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullResources() {
        assertThatThrownBy(() -> new Request(name, description, faculty, null, deadline, currentDateTime))
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
        assertThatThrownBy(() -> new Request(name, description, faculty, resources,
                currentDateTime.toLocalDate(), currentDateTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deadlineInPast() {
        LocalDate pastDate = currentDateTime.minusDays(1).toLocalDate();
        assertThatThrownBy(() -> new Request(name, description, faculty, resources, pastDate, currentDateTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructCorrectObject() {
        Request request = new Request(name, description, faculty, resources, deadline, currentDateTime);
        correctFields(request);
    }

    @Test
    void deadlineCanBeNull() {
        Request request = new Request(name, description, faculty, resources, null, currentDateTime);
        assertThat(request.getDeadline()).isNull();
    }

    @Test
    void nullRequestModel() {
        assertThatThrownBy(() -> new Request((RequestModelWaitingList) null, currentDateTime))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullRequestModelWithId() {
        assertThatThrownBy(() -> new Request((RequestModelWaitingListId) null, currentDateTime))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void fromRequestModel() {
        Request request = new Request(requestModel, currentDateTime);
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