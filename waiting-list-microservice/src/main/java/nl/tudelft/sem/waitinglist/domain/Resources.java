package nl.tudelft.sem.waitinglist.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import nl.tudelft.sem.common.models.request.ResourcesModel;

@Embeddable
@NoArgsConstructor
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
        if (cpu <= 0) {
            throw new IllegalArgumentException("CPU resources must be positive");
        }
        if (gpu < 0) {
            throw new IllegalArgumentException("GPU resources cannot be negative");
        }
        if (ram <= 0) {
            throw new IllegalArgumentException("Memory resources must be positive");
        }
        if (gpu > cpu) {
            throw new IllegalArgumentException("GPU resources cannot be greater than CPU resources");
        }
        if (ram > cpu) {
            throw new IllegalArgumentException("Memory resources cannot be greater than CPU resources");
        }

        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
    }

    /**
     * Creates a new resources object from a resources model.
     *
     * @param resourcesModel resources model
     */
    public Resources(@NonNull ResourcesModel resourcesModel) {
        this(resourcesModel.getCpu(), resourcesModel.getGpu(), resourcesModel.getRam());
    }

    /**
     * Checks if the resources are smaller than a specific resource.
     *
     * @param other - Resources - resources which is compared.
     * @return boolean - false if not smaller
     */
    public boolean isResourceSmaller(Resources other) {
        if (this.cpu >= other.cpu && this.gpu >= other.cpu && this.ram >= other.ram) {
            return false;
        }
        else {
            return true;
        }
    }
}
