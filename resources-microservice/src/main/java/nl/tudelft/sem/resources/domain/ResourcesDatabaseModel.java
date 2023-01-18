package nl.tudelft.sem.resources.domain;

import lombok.*;
import nl.tudelft.sem.common.models.request.ResourcesModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
@Setter
@Embeddable
@NoArgsConstructor
public class ResourcesDatabaseModel {
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
    public ResourcesDatabaseModel(int cpu, int gpu, int ram) {

        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
    }

    /**
     * Creates a new resources object from a resources model.
     *
     * @param resourcesModel resources model
     */
    public ResourcesDatabaseModel(@NonNull ResourcesModel resourcesModel) {
        this(resourcesModel.getCpu(), resourcesModel.getGpu(), resourcesModel.getRam());
    }

    public ResourcesModel toResourcesModel() {
        return new ResourcesModel(this.getCpu(), this.getGpu(), this.getRam());
    }
}
