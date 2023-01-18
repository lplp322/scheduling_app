package nl.tudelft.sem.resources.domain.resources;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ResourceAllocationService {

    private final transient ResourceAllocationRepository resourceAllocationRepository;
    private final transient UsedResourceRepository usedResourceRepository;
    private static final String RELEASED = "released";

    @Autowired
    public ResourceAllocationService(ResourceAllocationRepository resourceAllocationRepository,
                                     UsedResourceRepository usedResourceRepository) {
        this.resourceAllocationRepository = resourceAllocationRepository;
        this.usedResourceRepository = usedResourceRepository;
    }

    /** Adds the resources of the given node to its faculty's allocated resources.
     *
     * @param node Node to add to the cluster
     */
    public void updateResourceAllocation(Node node) {
        ResourceAllocationModel allocatedResources = this.resourceAllocationRepository.findById(node.getFaculty())
                .orElse(new ResourceAllocationModel(node.getFaculty(), 0, 0, 0));
        allocatedResources.setResources(
                new ResourcesDatabaseModel(ResourceLogicUtils.addResources(allocatedResources
                        .getResources().toResourcesModel(), node.getResources())));
        resourceAllocationRepository.save(allocatedResources);
    }

    /** Registers the resources passed as used on the given day for the given faculty.
     *
     * @param date date on which to update resources
     * @param faculty faculty that uses the given resources
     * @param usedResources resources to be used
     * @return true if enough resources could be allocated, false otherwise
     */
    public boolean updateUsedResources(LocalDate date, String faculty, ResourcesModel usedResources) {
        ResourcesDatabaseModel facultyAllocatedResources = resourceAllocationRepository.findById(faculty)
                .orElse(new ResourceAllocationModel(faculty, 0, 0, 0)).getResources();
        ResourcesDatabaseModel facultyUsedResources = usedResourceRepository.findById(new ResourceId(faculty, date))
                .orElse(new UsedResourcesModel(faculty, date, 0, 0, 0)).getResources();
        ResourcesDatabaseModel releasedResources = usedResourceRepository.findById(new ResourceId(RELEASED, date))
                .orElse(new UsedResourcesModel(RELEASED, date, 0, 0, 0)).getResources();

        boolean res = ResourceLogicUtils.updateResources(usedResources, facultyUsedResources,
                facultyAllocatedResources, releasedResources);

        if (!res) {
            return false;
        }
        usedResourceRepository.save(new UsedResourcesModel(faculty, date, facultyUsedResources));
        usedResourceRepository.save(new UsedResourcesModel(RELEASED, date, releasedResources));
        return true;
    }

    /** Gets the resources available for the given faculty on the given date.
     * If the faculty does not exist throws NoSuchElementException.
     *
     * @param faculty faculty whose resources are requested
     * @param date date on which the resources are requested
     * @return requested resources
     */
    public ResourcesModel getAvailableResources(String faculty, LocalDate date) {
        ResourcesDatabaseModel facultyAllocatedResources = resourceAllocationRepository.findById(faculty)
                .orElseThrow().getResources();
        ResourcesDatabaseModel facultyUsedResources = usedResourceRepository.findById(new ResourceId(faculty, date))
                .orElse(new UsedResourcesModel(faculty, date, 0, 0, 0)).getResources();
        ResourcesDatabaseModel releasedResources = usedResourceRepository.findById(new ResourceId(RELEASED, date))
                .orElse(new UsedResourcesModel(RELEASED, date, 0, 0, 0)).getResources();

        return ResourceLogicUtils.subtractResources(ResourceLogicUtils.addResources(
                facultyAllocatedResources.toResourcesModel(), releasedResources.toResourcesModel()),
                facultyUsedResources.toResourcesModel());
    }

    /** Method for retrieving all released resources on a date.
     *
     * @param date date on which to retrieve resources.
     * @return released resources on that day.
     */
    public ResourcesModel getAvailableResources(LocalDate date) {
        return usedResourceRepository.findById(new ResourceId(RELEASED, date))
                .orElse(new UsedResourcesModel(RELEASED, date, 0, 0, 0)).getResources().toResourcesModel();
    }
}
