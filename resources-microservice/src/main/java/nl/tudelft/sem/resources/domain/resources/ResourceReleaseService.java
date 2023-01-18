package nl.tudelft.sem.resources.domain.resources;

import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class ResourceReleaseService {

    private final transient ResourceAllocationRepository resourceAllocationRepository;
    private final transient UsedResourceRepository usedResourceRepository;
    private static final String RELEASED = "released";

    @Autowired
    public ResourceReleaseService(ResourceAllocationRepository resourceAllocationRepository,
                                  UsedResourceRepository usedResourceRepository) {
        this.resourceAllocationRepository = resourceAllocationRepository;
        this.usedResourceRepository = usedResourceRepository;
    }

    @Scheduled(cron = "59 59 17 * * *")
    private void releaseDaily() {
        releaseAll(LocalDate.now().plusDays(1));
    }

    /** Releases all remaining resources for a specific day, to be used when 6 hours are left until the next day.
     *
     * @param day the day on which to release all remaining resources.
     */
    public void releaseAll(LocalDate day) {
        UsedResourcesModel releasedResources = usedResourceRepository.findById(new ResourceId(RELEASED, day))
                .orElse(new UsedResourcesModel(RELEASED, day, 0, 0, 0));
        Collection<ResourceAllocationModel> allocatedResources = resourceAllocationRepository.findAll();
        for (ResourceAllocationModel i : allocatedResources) {
            UsedResourcesModel usedResources = usedResourceRepository.findById(new ResourceId(i.getFaculty(), day))
                    .orElse(new UsedResourcesModel(i.getFaculty(), day, 0, 0, 0));
            releasedResources.setResources(new ResourcesDatabaseModel(
                    ResourceLogicUtils.subtractResources(ResourceLogicUtils.addResources(
                            releasedResources.getResources().toResourcesModel(), i.getResources().toResourcesModel()),
                            usedResources.getResources().toResourcesModel())));

            usedResources.setResources(i.getResources());
            usedResourceRepository.save(usedResources);
        }
        usedResourceRepository.save(releasedResources);
    }

    /** Method for releasing resources.
     *
     * @param releasedResources Resources to release.
     * @param faculty Faculty from which to release resources.
     * @param from Date from which those resources are released.
     * @param until Date until which those resources are released.
     * @return true if resources could be released on every day, false otherwise
     */
    public boolean releaseResources(ResourcesModel releasedResources, String faculty, LocalDate from, LocalDate until) {
        ResourcesDatabaseModel facultyAllocatedResources = resourceAllocationRepository.findById(faculty) //NOPMD
                .orElseThrow().getResources(); //NOPMD

        ArrayList<UsedResourcesModel> res = new ArrayList<>(); //NOPMD
        for (; from.isBefore(until) || from.equals(until); from = from.plusDays(1)) {

            UsedResourcesModel facultyUsedResources = usedResourceRepository.findById(new ResourceId(faculty, from))
                    .orElse(new UsedResourcesModel(faculty, from, 0, 0, 0));

            if (!ResourceLogicUtils.canRelease(facultyAllocatedResources.toResourcesModel(),
                    facultyUsedResources.getResources().toResourcesModel(), releasedResources)) {
                return false;
            }
            facultyUsedResources.setResources(new ResourcesDatabaseModel(ResourceLogicUtils.addResources(
                    facultyUsedResources.getResources().toResourcesModel(), releasedResources)));

            UsedResourcesModel oldReleasedResources = usedResourceRepository.findById(new ResourceId(RELEASED, from))
                    .orElse(new UsedResourcesModel(RELEASED, from, 0, 0, 0));

            oldReleasedResources.setResources(new ResourcesDatabaseModel(ResourceLogicUtils.addResources(
                    oldReleasedResources.getResources().toResourcesModel(), releasedResources)));

            res.add(facultyUsedResources);
            res.add(oldReleasedResources);
        }

        usedResourceRepository.saveAll(res);
        return true;
    }
}
