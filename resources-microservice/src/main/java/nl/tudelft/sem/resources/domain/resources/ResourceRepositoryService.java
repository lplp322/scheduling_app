package nl.tudelft.sem.resources.domain.resources;

import nl.tudelft.sem.common.models.Faculty;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ResourceRepositoryService {

    private final ResourceAllocationRepository resourceAllocationRepository;
    private final UsedResourceRepository usedResourceRepository;

    @Autowired
    public ResourceRepositoryService(ResourceAllocationRepository resourceAllocationRepository,
                                     UsedResourceRepository usedResourceRepository) {
        this.resourceAllocationRepository = resourceAllocationRepository;
        this.usedResourceRepository = usedResourceRepository;
    }

    public void updateResourceAllocation(ResourcesModel newTotal) {
        int nrOfFaculties = Faculty.values().length;
        int cpu = newTotal.getCpu() / nrOfFaculties;
        int remainingCpu = newTotal.getCpu() % nrOfFaculties;
        int gpu = newTotal.getGpu() / nrOfFaculties;
        int remainingGpu = newTotal.getGpu() % nrOfFaculties;
        int ram = newTotal.getRam() / nrOfFaculties;
        int remainingRam = newTotal.getRam() % nrOfFaculties;

        for(Faculty i : Faculty.values()) {
            resourceAllocationRepository.save(new ResourceAllocationModel(i.name(), cpu, gpu, ram));
        }
        resourceAllocationRepository.save(new ResourceAllocationModel("unallocated", remainingCpu, remainingGpu, remainingRam));
    }

    public boolean updateCpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel unallocatedResources,
                             ResourcesDatabaseModel releasedResources){
        if (usedResources.getCpu() + facultyUsedResources.getCpu() <= facultyAllocatedResources.getCpu()) {
            facultyUsedResources.setCpu(usedResources.getCpu() + facultyUsedResources.getCpu());
        } else if (usedResources.getCpu() + facultyUsedResources.getCpu() - facultyAllocatedResources.getCpu()
                <= releasedResources.getCpu() + unallocatedResources.getCpu()) {
            releasedResources.setCpu(releasedResources.getCpu() - usedResources.getCpu() - facultyUsedResources.getCpu()
                    + facultyAllocatedResources.getCpu());
            facultyUsedResources.setCpu(facultyAllocatedResources.getCpu());
        } else {
            return false;
        }
        return true;
    }

    public boolean updateGpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel unallocatedResources,
                             ResourcesDatabaseModel releasedResources){
        if (usedResources.getGpu() + facultyUsedResources.getGpu() <= facultyAllocatedResources.getGpu()) {
            facultyUsedResources.setGpu(usedResources.getGpu() + facultyUsedResources.getGpu());
        } else if (usedResources.getGpu() + facultyUsedResources.getGpu() - facultyAllocatedResources.getGpu()
                <= releasedResources.getGpu() + unallocatedResources.getGpu()) {
            releasedResources.setRam(releasedResources.getGpu() - usedResources.getGpu() - facultyUsedResources.getGpu()
                    + facultyAllocatedResources.getGpu());
            facultyUsedResources.setGpu(facultyAllocatedResources.getGpu());
        } else {
            return false;
        }
        return true;
    }

    public boolean updateRam(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel unallocatedResources,
                             ResourcesDatabaseModel releasedResources){
        if (usedResources.getRam() + facultyUsedResources.getRam() <= facultyAllocatedResources.getRam()) {
            facultyUsedResources.setRam(usedResources.getRam() + facultyUsedResources.getRam());
        } else if (usedResources.getRam() + facultyUsedResources.getRam() - facultyAllocatedResources.getRam()
                <= releasedResources.getRam() + unallocatedResources.getRam()) {
            releasedResources.setRam(releasedResources.getRam() - usedResources.getRam() - facultyUsedResources.getRam()
            + facultyAllocatedResources.getRam());
            facultyUsedResources.setRam(facultyAllocatedResources.getRam());
        } else {
            return false;
        }
        return true;
    }

    public boolean updateUsedResources(LocalDate date, Faculty faculty, ResourcesModel usedResources) {
        ResourcesDatabaseModel facultyAllocatedResources = resourceAllocationRepository.findById(faculty.name())
                .orElse(new ResourceAllocationModel(faculty.name(), 0, 0, 0)).getResources();
        ResourcesDatabaseModel facultyUsedResources = usedResourceRepository.findById(new ResourceId(faculty.name(), date))
                .orElse(new UsedResourcesModel(faculty.name(), date, 0, 0, 0)).getResources();
        ResourcesDatabaseModel unallocatedResources = resourceAllocationRepository.findById("unallocated")
                .orElse(new ResourceAllocationModel("unallocated", 0, 0, 0)).getResources();
        ResourcesDatabaseModel releasedResources = usedResourceRepository.findById(new ResourceId("released", date))
                .orElse(new UsedResourcesModel("released", date, 0, 0, 0)).getResources();

        boolean res = updateCpu(usedResources, facultyUsedResources, facultyAllocatedResources, unallocatedResources, releasedResources)
                && updateGpu(usedResources, facultyUsedResources, facultyAllocatedResources, unallocatedResources, releasedResources)
                && updateRam(usedResources, facultyUsedResources, facultyAllocatedResources, unallocatedResources, releasedResources);

        if(!res)
            return false;

        usedResourceRepository.save(new UsedResourcesModel(faculty.name(), date, facultyUsedResources));
        usedResourceRepository.save(new UsedResourcesModel("released", date, releasedResources));
        return true;
    }

}
