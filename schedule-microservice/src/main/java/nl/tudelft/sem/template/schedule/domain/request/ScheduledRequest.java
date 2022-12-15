package nl.tudelft.sem.template.schedule.domain.request;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.RequestModel;
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
    private int id;

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

    @Column(name = "net_id", nullable = false)
    private String netId;

    /**
     * Create new scheduled request.
     *
     * @param netId The NetId of the user that made the request.
     * @param date The date the request is scheduled on.
     * @param cpuUsage The amount of CPU resources that is requested.
     * @param gpuUsage The amount of GPU resources that is requested.
     * @param memoryUsage The amount of memory resources that is requested.
     */
    public ScheduledRequest(String name, String description, int cpuUsage, int gpuUsage,
                            int memoryUsage, LocalDate date, String netId) {
        this.name = name;
        this.description = description;
        this.netId = netId;
        this.date = date;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;
        this.recordThat(new RequestWasScheduledEvent(this));
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

    public String getNetId() {
        return netId;
    }

    public RequestModel convert() {
        return new RequestModel(this.name, this.description, this.faculty,
                new ResourcesModel(this.cpuUsage, this.gpuUsage, this.memoryUsage), this.date, this.netId);
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
