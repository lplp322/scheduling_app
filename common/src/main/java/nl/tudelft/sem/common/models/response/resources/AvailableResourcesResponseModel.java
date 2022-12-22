package nl.tudelft.sem.common.models.response.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.common.models.request.ResourcesModel;

@Data
@AllArgsConstructor
public class AvailableResourcesResponseModel {
    int cpu;
    int gpu;
    int ram;

    /** Constructor for an available resources response model from a resources model.
     *
     * @param resourcesModel resources model to convert to response model
     */
    public AvailableResourcesResponseModel(ResourcesModel resourcesModel) {
        this.cpu = resourcesModel.getCpu();
        this.gpu = resourcesModel.getGpu();
        this.ram = resourcesModel.getRam();
    }
}
