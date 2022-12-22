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

    public boolean enoughAvailable(ResourcesModel availableResources) {
        if (this.cpu > availableResources.cpu || this.gpu > availableResources.gpu
                || this.ram > availableResources.ram) {
            return false;
        }
        return true;
    }
}
