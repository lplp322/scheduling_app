package nl.tudelft.sem.resources.services;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import nl.tudelft.sem.resources.domain.resources.ResourceAllocationModel;
import nl.tudelft.sem.resources.domain.resources.ResourceRepositoryService;
import nl.tudelft.sem.resources.domain.resources.UsedResourcesModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class ResourceRepositoryServiceTest {

    @Autowired
    private UsedResourceRepository usedResourceRepository;

    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;

    @Autowired
    private ResourceRepositoryService resourceRepositoryService;

    private Node node;

    @lombok.SneakyThrows
    @BeforeEach
    void setUp()  throws MalformedURLException {
        node = new Node("node", new URL("http://localhost"), "token",
                new ResourcesModel(10, 6, 4), "EEMCS");
    }

    @Test
    void updateResourceAllocation() {
        Optional<ResourceAllocationModel> allocatedResources = this.resourceAllocationRepository.findById(node.getFaculty());
        assertTrue(allocatedResources.isEmpty());

        resourceRepositoryService.updateResourceAllocation(node);
        allocatedResources = this.resourceAllocationRepository.findById(node.getFaculty());
        assertTrue(allocatedResources.isPresent());
        assertEquals(allocatedResources.get().getResources(), new ResourcesDatabaseModel(node.getResources()));

        resourceRepositoryService.updateResourceAllocation(node);
        allocatedResources = this.resourceAllocationRepository.findById(node.getFaculty());
        assertTrue(allocatedResources.isPresent());
        assertNotEquals(allocatedResources.get().getResources(), new ResourcesDatabaseModel(node.getResources()));

    }

    @Test
    void getAvailableResources() {
        assertThrows(NoSuchElementException.class, () ->
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));
        resourceRepositoryService.updateResourceAllocation(node);
        ResourcesModel expected = node.getResources();
        assertEquals(expected, resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));
        usedResourceRepository.save(new UsedResourcesModel("released", LocalDate.now(), 2, 0, 1));
        expected.setCpu(expected.getCpu() + 2);
        expected.setRam(expected.getRam() + 1);
        assertEquals(expected, resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));
    }

    //ResourcesModel usedResources, ResourcesDatabaseModel facultyUsedResources,
    //                             ResourcesDatabaseModel facultyAllocatedResources, ResourcesDatabaseModel releasedResources


    @Test
    void releaseAll() {
    }

    @Test
    void releaseResources() {
    }

    @Test
    void updateUsedResourcesEnoughFacultyResources() {

        //Test for faculty resources
        resourceRepositoryService.updateResourceAllocation(node);
        ResourcesModel usedResources = new ResourcesModel(5, 2, 2);
        assertTrue(resourceRepositoryService.updateUsedResources(LocalDate.now(), "EEMCS", usedResources));
        assertEquals(new ResourcesModel(5, 4, 2),
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));

        //Test part faculty part released resources
        UsedResourcesModel released = new UsedResourcesModel("released", LocalDate.now(), 7, 1, 5);
        usedResourceRepository.save(released);
        usedResources = new ResourcesModel(7, 3, 3);
        assertTrue(resourceRepositoryService.updateUsedResources(LocalDate.now(), "EEMCS", usedResources));
        assertEquals(new ResourcesModel(5, 2, 4),
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));

        //Test for released resources
        assertTrue(resourceRepositoryService.updateUsedResources(LocalDate.now(), "EEMCS", new ResourcesModel(5, 2, 4)));
        assertEquals(new ResourcesModel(0, 0, 0),
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));

        assertFalse(resourceRepositoryService.updateUsedResources(LocalDate.now(), "EEMCS", new ResourcesModel(5, 2, 4)));
    }

    @AfterEach
    void tearDown() {
        usedResourceRepository.deleteAll();
        usedResourceRepository.flush();
        resourceAllocationRepository.deleteAll();
        resourceAllocationRepository.flush();
    }

}