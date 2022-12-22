package nl.tudelft.sem.resources.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nl.tudelft.sem.common.models.request.ResourcesModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Setter
@Embeddable
@NoArgsConstructor
public class ResourcesDatabaseModel extends ResourcesModel {
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
}
