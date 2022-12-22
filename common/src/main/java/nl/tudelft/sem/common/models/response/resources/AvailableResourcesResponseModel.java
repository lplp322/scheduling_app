package nl.tudelft.sem.common.models.response.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;

@Data
@AllArgsConstructor
public class AvailableResourcesResponseModel {
    int cpu;
    int gpu;
    int ram;

    public AvailableResourcesResponseModel(ResourcesModel resourcesModel) {
        this.cpu = resourcesModel.getCpu();
        this.gpu = resourcesModel.getGpu();
        this.ram = resourcesModel.getRam();
    }
}
