package nl.tudelft.sem.template.schedule.domain.request;

import java.time.LocalDate;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A DDD entity representing a (scheduled) request in our domain.
 */
@Entity
@Table(name = "scheduledRequests")
@NoArgsConstructor
public class ScheduledRequest {

    /**
     * Identifier for the scheduled request.
     */
    @Id
    @Column(name = "id", nullable = false)
    @Getter
    private long id;

    @Column(name = "request", nullable = false)
    @Getter
    private Request request;

    @Column(name = "date", nullable = false)
    @Getter
    private LocalDate date;

    /**
     * Create new scheduled request.
     *
     * @param id ID of request that is the same at every microservice.
     * @param request Request that is scheduled.
     * @param date Planned date of the request.
     */
    public ScheduledRequest(long id, Request request, LocalDate date) {
        this.id = id;
        this.request = request;
        this.date = date;
    }

    public RequestModelSchedule convert() {
        return new RequestModelSchedule(this.id, this.request.getName(), this.request.getDescription(),
                this.request.getFaculty(), this.request.getResources().convert(), this.date);
    }

    /**
     * Equality is only based on the id of the request.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduledRequest request = (ScheduledRequest) o;
        return id == (request.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
