package nl.tudelft.sem.common.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Information about the amount of resources, for example for the amount needed by a request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ResourcesModel {

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
     * Checks if there are enough available resources for the resources in this resource model.
     *
     * @param availableResources The available resources.
     * @return Whether there are enough available resources.
     */
    public boolean enoughAvailable(ResourcesModel availableResources) {
        if (this.cpu > availableResources.cpu || this.gpu > availableResources.gpu
                || this.ram > availableResources.ram) {
            return false;
        }
        return true;
    }
}
