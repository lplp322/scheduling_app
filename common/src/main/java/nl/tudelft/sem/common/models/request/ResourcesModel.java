package nl.tudelft.sem.common.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourcesModel {
    private int cpu;
    private int gpu;
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
