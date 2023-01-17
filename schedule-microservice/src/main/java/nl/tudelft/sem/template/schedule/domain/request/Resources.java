package nl.tudelft.sem.template.schedule.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.ResourcesModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor
@Embeddable
public class Resources {

    @Column(name = "cpu_resources")
    @Getter
    private int cpu;

    @Column(name = "gpu_resources")
    @Getter
    private int gpu;

    @Column(name = "memory_resources")
    @Getter
    private int ram;

    /**
     * Creates a new resources object.
     *
     * @param cpu CPU resources
     * @param gpu GPU resources
     * @param ram memory resources
     */
    public Resources(int cpu, int gpu, int ram) {
        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
    }

    public ResourcesModel convert() {
        return new ResourcesModel(this.cpu, this.gpu, this.ram);
    }
}
