package nl.tudelft.sem.waitinglist.domain;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import nl.tudelft.sem.common.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModel;

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

    @Column(name = "planned_date")
    @Getter
    private LocalDate plannedDate;

    @Column(name = "status")
    @Getter
    private RequestStatus status;

    /**
     * Creates a new request object.
     *
     * @param name request name
     * @param description request description
     * @param faculty request faculty
     * @param resources requested resources
     * @param deadline request deadline
     */
    public Request(@NonNull String name, @NonNull String description, @NonNull String faculty,
                   @NonNull Resources resources, LocalDate deadline, @NonNull LocalDate currentDate) {

        if (name.isBlank()) {
            throw new IllegalArgumentException("Request name cannot be blank");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("Request description cannot be blank");
        }
        if (faculty.isBlank()) {
            throw new IllegalArgumentException("Request faculty cannot be blank");
        }
        if (deadline != null && (deadline.isBefore(currentDate) || deadline.isEqual(currentDate))) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }

        this.name = name;
        this.description = description;
        this.faculty = faculty;
        this.resources = resources;
        this.deadline = deadline;
        this.status = RequestStatus.PENDING;
    }

    /**
     * Creates a new request from a request model.
     *
     * @param requestModel request model
     */
    public Request(@NonNull RequestModel requestModel, @NonNull LocalDate currentDate) {
        this(requestModel.getName(), requestModel.getDescription(), requestModel.getFaculty(),
                new Resources(requestModel.getResources()), requestModel.getDeadline(), currentDate);
    }
}
