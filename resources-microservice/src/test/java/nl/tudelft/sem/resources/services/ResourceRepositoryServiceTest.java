package nl.tudelft.sem.resources.services;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.ResourcesDatabaseModel;
import nl.tudelft.sem.resources.domain.resources.ResourceAllocationModel;
import nl.tudelft.sem.resources.domain.resources.ResourceId;
import nl.tudelft.sem.resources.domain.resources.ResourceRepositoryService;
import nl.tudelft.sem.resources.domain.resources.UsedResourcesModel;
import org.apache.tomcat.jni.Local;
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
        usedResourceRepository.deleteAll();
        usedResourceRepository.flush();
        resourceAllocationRepository.deleteAll();
        resourceAllocationRepository.flush();
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

    @Test
    void releaseAll() throws MalformedURLException {
        resourceRepositoryService.updateResourceAllocation(node);
        Node node2 = new Node("node2", new URL("http://localhost"), "token2", new ResourcesModel(8, 2, 2), "IDE");
        resourceRepositoryService.updateResourceAllocation(node2);
        resourceRepositoryService.releaseAll(LocalDate.now());
        Optional<UsedResourcesModel> released = usedResourceRepository.findById(new ResourceId("released", LocalDate.now()));
        assertTrue(released.isPresent());
        assertEquals(new ResourcesModel(18, 8, 6), released.get().getResources().toResourceModel());
        assertEquals(new ResourcesModel(18, 8, 6),
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));
        assertEquals(new ResourcesModel(18, 8, 6), resourceRepositoryService.getAvailableResources("IDE", LocalDate.now()));
    }

    @Test
    void releaseResources() throws MalformedURLException {
        resourceRepositoryService.updateResourceAllocation(node);
        Node node2 = new Node("node2", new URL("http://localhost"), "token2", new ResourcesModel(8, 2, 2), "IDE");
        resourceRepositoryService.updateResourceAllocation(node2);
        assertEquals(new ResourcesModel(10, 6, 4),
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));
        assertEquals(new ResourcesModel(8, 2, 2), resourceRepositoryService.getAvailableResources("IDE", LocalDate.now()));
        assertTrue(resourceRepositoryService.releaseResources(
                new ResourcesModel(4, 2, 2), "EEMCS", LocalDate.now(), LocalDate.now().plusDays(3)));
        assertEquals(new ResourcesModel(10, 6, 4),
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now()));
        assertEquals(new ResourcesModel(12, 4, 4),
                resourceRepositoryService.getAvailableResources("IDE", LocalDate.now()));
        assertEquals(new ResourcesModel(10, 6, 4),
                resourceRepositoryService.getAvailableResources("EEMCS", LocalDate.now().plusDays(3)));
        assertEquals(new ResourcesModel(12, 4, 4),
                resourceRepositoryService.getAvailableResources("IDE", LocalDate.now().plusDays(3)));
        assertEquals(new ResourcesModel(8, 2, 2),
                resourceRepositoryService.getAvailableResources("IDE", LocalDate.now().plusDays(4)));
    }


    @Test
    void getReleasedResources() throws MalformedURLException {
        resourceRepositoryService.updateResourceAllocation(node);
        Node node2 = new Node("node2", new URL("http://localhost"), "token2", new ResourcesModel(8, 2, 2), "IDE");
        resourceRepositoryService.updateResourceAllocation(node2);
        resourceRepositoryService.releaseAll(LocalDate.now());
        Optional<UsedResourcesModel> released = usedResourceRepository.findById(new ResourceId("released", LocalDate.now()));
        assertTrue(released.isPresent());
        assertEquals(new ResourcesModel(18, 8, 6), released.get().getResources().toResourceModel());
        assertEquals(new ResourcesModel(18, 8, 6), resourceRepositoryService.getAvailableResources(LocalDate.now()));
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