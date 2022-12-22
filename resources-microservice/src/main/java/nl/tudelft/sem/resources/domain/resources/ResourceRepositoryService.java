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

    private final ResourceAllocationRepository resourceAllocationRepository;
    private final UsedResourceRepository usedResourceRepository;

    @Autowired
    public ResourceRepositoryService(ResourceAllocationRepository resourceAllocationRepository,
                                     UsedResourceRepository usedResourceRepository) {
        this.resourceAllocationRepository = resourceAllocationRepository;
        this.usedResourceRepository = usedResourceRepository;
    }

    public void updateResourceAllocation(Node node) {

    }

    public boolean updateCpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources){
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

    public boolean updateGpu(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources){
        if (usedResources.getGpu() + facultyUsedResources.getGpu() <= facultyAllocatedResources.getGpu()) {
            facultyUsedResources.setGpu(usedResources.getGpu() + facultyUsedResources.getGpu());
        } else if (usedResources.getGpu() + facultyUsedResources.getGpu() - facultyAllocatedResources.getGpu()
                <= releasedResources.getGpu()) {
            releasedResources.setRam(releasedResources.getGpu() - usedResources.getGpu() - facultyUsedResources.getGpu()
                    + facultyAllocatedResources.getGpu());
            facultyUsedResources.setGpu(facultyAllocatedResources.getGpu());
        } else {
            return false;
        }
        return true;
    }

    public boolean updateRam(ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources){
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

    public boolean updateUsedResources(LocalDate date, String faculty, ResourcesModel usedResources) {
        ResourcesDatabaseModel facultyAllocatedResources = resourceAllocationRepository.findById(faculty)
                .orElse(new ResourceAllocationModel(faculty, 0, 0, 0)).getResources();
        ResourcesDatabaseModel facultyUsedResources = usedResourceRepository.findById(new ResourceId(faculty, date))
                .orElse(new UsedResourcesModel(faculty, date, 0, 0, 0)).getResources();
        ResourcesDatabaseModel releasedResources = usedResourceRepository.findById(new ResourceId("released", date))
                .orElse(new UsedResourcesModel("released", date, 0, 0, 0)).getResources();

        boolean res = updateCpu(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources)
                && updateGpu(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources)
                && updateRam(usedResources, facultyUsedResources, facultyAllocatedResources, releasedResources);

        if(!res)
            return false;

        usedResourceRepository.save(new UsedResourcesModel(faculty, date, facultyUsedResources));
        usedResourceRepository.save(new UsedResourcesModel("released", date, releasedResources));
        return true;
    }

    public ResourcesModel getAvailableResources(String faculty, LocalDate date) {
        int cpu;
        int gpu;
        int ram;

        ResourcesDatabaseModel facultyAllocatedResources = resourceAllocationRepository.findById(faculty)
                .orElseThrow().getResources();
        ResourcesDatabaseModel facultyUsedResources = usedResourceRepository.findById(new ResourceId(faculty, date))
                .orElse(new UsedResourcesModel(faculty, date, 0, 0, 0)).getResources();
        ResourcesDatabaseModel releasedResources = usedResourceRepository.findById(new ResourceId("released", date))
                .orElse(new UsedResourcesModel("released", date, 0, 0, 0)).getResources();

        cpu = facultyAllocatedResources.getCpu() - facultyUsedResources.getCpu() + releasedResources.getCpu();
        gpu = facultyAllocatedResources.getGpu() - facultyUsedResources.getGpu() + releasedResources.getGpu();
        ram = facultyAllocatedResources.getRam() - facultyUsedResources.getRam() + releasedResources.getRam();

        return new ResourcesModel(cpu, gpu, ram);
    }
}
