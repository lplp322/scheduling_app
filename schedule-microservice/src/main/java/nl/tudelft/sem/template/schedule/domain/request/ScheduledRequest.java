package nl.tudelft.sem.template.schedule.domain.request;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.template.schedule.domain.HasEvents;

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
public class ScheduledRequest extends HasEvents {

    /**
     * Identifier for the scheduled request.
     */
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "faculty", nullable = false)
    private String faculty;

    @Column(name = "cpu_usage", nullable = false)
    private int cpuUsage;

    @Column(name = "gpu_usage")
    private int gpuUsage;

    @Column(name = "memory_usage")
    private int memoryUsage;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Create new scheduled request.
     *
     * @param id ID of request that is the same at every microservice.
     * @param name Name of the request.
     * @param description Description of the request.
     * @param faculty Faculty where the request is from.
     * @param cpuUsage Requested CPU resources.
     * @param gpuUsage Requested GPU resources.
     * @param memoryUsage Requested memory resources.
     * @param date Planned date of the request.
     */
    public ScheduledRequest(long id, String name, String description, String faculty, int cpuUsage, int gpuUsage,
                            int memoryUsage, LocalDate date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.faculty = faculty;
        this.date = date;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;
        this.recordThat(new RequestWasScheduledEvent(this));
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFaculty() {
        return faculty;
    }

    public int getCpuUsage() {
        return cpuUsage;
    }

    public int getGpuUsage() {
        return gpuUsage;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public LocalDate getDate() {
        return date;
    }

    public RequestModelSchedule convert() {
        return new RequestModelSchedule(this.id, this.name, this.description, this.faculty,
                new ResourcesModel(this.cpuUsage, this.gpuUsage, this.memoryUsage), this.date);
    }

    /**
     * Equality is only based on the identifier.
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
