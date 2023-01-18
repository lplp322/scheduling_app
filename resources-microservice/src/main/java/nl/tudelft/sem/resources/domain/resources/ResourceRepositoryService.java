package nl.tudelft.sem.resources.domain.resources;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class ResourceRepositoryService {

    private final transient ResourceAllocationService resourceAllocationService;
    private final transient ResourceReleaseService resourceReleaseService;

    @Autowired
    public ResourceRepositoryService(ResourceAllocationService resourceAllocationService,
                                     ResourceReleaseService resourceReleaseService) {
        this.resourceAllocationService = resourceAllocationService;
        this.resourceReleaseService = resourceReleaseService;
    }

    public void updateResourceAllocation(Node node) {
        resourceAllocationService.updateResourceAllocation(node);
    }

    public boolean updateUsedResources(LocalDate date, String faculty, ResourcesModel usedResources) {
        return resourceAllocationService.updateUsedResources(date, faculty, usedResources);
    }

    public ResourcesModel getAvailableResources(String faculty, LocalDate date) {
        return resourceAllocationService.getAvailableResources(faculty, date);
    }

    public ResourcesModel getAvailableResources(LocalDate date) {
        return resourceAllocationService.getAvailableResources(date);
    }

    public void releaseAll(LocalDate day) {
        resourceReleaseService.releaseAll(day);
    }

    public boolean releaseResources(ResourcesModel releasedResources, String faculty, LocalDate from, LocalDate until) {
        return resourceReleaseService.releaseResources(releasedResources, faculty, from, until);
    }

}
