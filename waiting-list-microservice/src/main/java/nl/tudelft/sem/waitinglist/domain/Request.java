package nl.tudelft.sem.waitinglist.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;

@Entity
@Table(name = "requests")
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Column(name = "name", nullable = false)
    @Getter
    private String name;

    @Column(name = "description", nullable = false)
    @Getter
    private String description;

    @Column(name = "faculty", nullable = false)
    @Getter
    private String faculty;

    @Column(name = "resources", nullable = false)
    @Getter
    private Resources resources;

    @Column(name = "deadline")
    @Getter
    private LocalDate deadline;

    @Column(name = "status")
    @Getter
    private RequestStatus status;

    /**
     * Creates a new request object.
     *
     * @param name        request name
     * @param description request description
     * @param faculty     request faculty
     * @param resources   requested resources
     * @param deadline    request deadline
     *
     * @throws IllegalArgumentException if one of the arguments is invalid
     */
    public Request(@NonNull String name, @NonNull String description, @NonNull String faculty,
                   @NonNull Resources resources, LocalDate deadline, @NonNull LocalDateTime currentDateTime) {

        this.name = RequestValidator.validateName(name);
        this.description = RequestValidator.validateDescription(description);
        this.faculty = RequestValidator.validateFaculty(faculty);
        this.resources = resources;
        this.deadline = RequestValidator.validateDeadline(deadline, currentDateTime);
        this.status = RequestStatus.PENDING;
    }

    /**
     * Creates a new request from a request model.
     *
     * @param requestModel request model
     */
    public Request(@NonNull RequestModelWaitingList requestModel, @NonNull LocalDateTime currentDateTime) {
        this(requestModel.getName(), requestModel.getDescription(), requestModel.getFaculty(),
                new Resources(requestModel.getResources()), requestModel.getDeadline(), currentDateTime);
    }

    /**
     * Sets the planned date of a request.
     *
     * @param plannedDate - date on which the request is planned.
     */
    public static LocalDate checkPlannedDate(LocalDate plannedDate, LocalDate currentDate, LocalDate deadline) {
        if (plannedDate.isAfter(deadline)) {
            throw new IllegalArgumentException("Planned date is after deadline");
        } else if (!plannedDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("Current date is after the planned date");
        } else {
            return plannedDate;
        }
    }
}
