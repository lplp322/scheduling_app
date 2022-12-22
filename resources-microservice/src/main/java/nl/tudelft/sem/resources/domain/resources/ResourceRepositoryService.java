package nl.tudelft.sem.resources.domain.resources;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ResourceRepositoryService {

    private final transient ResourceAllocationRepository resourceAllocationRepository;
    private final transient UsedResourceRepository usedResourceRepository;
    private static final String RELEASED = "released";

    @Autowired
    public ResourceRepositoryService(ResourceAllocationRepository resourceAllocationRepository,
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
        allocatedResources.getResources().setCpu(allocatedResources.getResources().getCpu() + node.getResources().getCpu());
        allocatedResources.getResources().setGpu(allocatedResources.getResources().getGpu() + node.getResources().getGpu());
        allocatedResources.getResources().setRam(allocatedResources.getResources().getRam() + node.getResources().getRam());
        resourceAllocationRepository.save(allocatedResources);
    }

    private boolean updateCpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources) {
        if (usedResources.getCpu() + facultyUsedResources.getCpu() <= facultyAllocatedResources.getCpu()) {
            facultyUsedResources.setCpu(usedResources.getCpu() + facultyUsedResources.getCpu());
        } else if (usedResources.getCpu() + facultyUsedResources.getCpu() - facultyAllocatedResources.getCpu()
                <= releasedResources.getCpu()) {
            releasedResources.setCpu(releasedResources.getCpu() - usedResources.getCpu() - facultyUsedResources.getCpu()
                    + facultyAllocatedResources.getCpu());
            facultyUsedResources.setCpu(facultyAllocatedResources.getCpu());
        } else {
            return false;
        }
        return true;
    }

    private boolean updateGpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources) {
        if (usedResources.getGpu() + facultyUsedResources.getGpu() <= facultyAllocatedResources.getGpu()) {
            facultyUsedResources.setGpu(usedResources.getGpu() + facultyUsedResources.getGpu());
        } else if (usedResources.getGpu() + facultyUsedResources.getGpu() - facultyAllocatedResources.getGpu()
                <= releasedResources.getGpu()) {
            releasedResources.setGpu(releasedResources.getGpu() - usedResources.getGpu() - facultyUsedResources.getGpu()
                    + facultyAllocatedResources.getGpu());
            facultyUsedResources.setGpu(facultyAllocatedResources.getGpu());
        } else {
            return false;
        }
        return true;
    }

    private boolean updateRam(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources) {
        if (usedResources.getRam() + facultyUsedResources.getRam() <= facultyAllocatedResources.getRam()) {
            facultyUsedResources.setRam(usedResources.getRam() + facultyUsedResources.getRam());
        } else if (usedResources.getRam() + facultyUsedResources.getRam() - facultyAllocatedResources.getRam()
                <= releasedResources.getRam()) {
            releasedResources.setRam(releasedResources.getRam() - usedResources.getRam() - facultyUsedResources.getRam()
                + facultyAllocatedResources.getRam());
            facultyUsedResources.setRam(facultyAllocatedResources.getRam());
        } else {
            return false;
        }
        return true;
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

        boolean res = updateCpu(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources)
                && updateGpu(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources)
                && updateRam(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources);

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
        int cpu;
        int gpu;
        int ram;

        ResourcesDatabaseModel facultyAllocatedResources = resourceAllocationRepository.findById(faculty)
                .orElseThrow().getResources();
        ResourcesDatabaseModel facultyUsedResources = usedResourceRepository.findById(new ResourceId(faculty, date))
                .orElse(new UsedResourcesModel(faculty, date, 0, 0, 0)).getResources();
        ResourcesDatabaseModel releasedResources = usedResourceRepository.findById(new ResourceId(RELEASED, date))
                .orElse(new UsedResourcesModel(RELEASED, date, 0, 0, 0)).getResources();

        cpu = facultyAllocatedResources.getCpu() - facultyUsedResources.getCpu() + releasedResources.getCpu();
        gpu = facultyAllocatedResources.getGpu() - facultyUsedResources.getGpu() + releasedResources.getGpu();
        ram = facultyAllocatedResources.getRam() - facultyUsedResources.getRam() + releasedResources.getRam();

        return new ResourcesModel(cpu, gpu, ram);
    }
}
