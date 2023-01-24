package nl.tudelft.sem.resources.services;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import lombok.SneakyThrows;
import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.resources.database.NodeRepository;
import nl.tudelft.sem.resources.database.ResourceAllocationRepository;
import nl.tudelft.sem.resources.database.UsedResourceRepository;
import nl.tudelft.sem.resources.domain.node.NameAlreadyInUseException;
import nl.tudelft.sem.resources.domain.node.NodeRepositoryService;
import nl.tudelft.sem.resources.domain.node.ResourceNode;
import nl.tudelft.sem.resources.domain.resources.ResourceRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class NodeRepositoryServiceTest {
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private NodeRepositoryService nodeRepositoryService;


    @BeforeEach
    void setUp() {
        nodeRepository.deleteAll();
        nodeRepository.flush();

    }

    @Test
    void addNode() throws Exception {
        Node node = new Node("node", new URL("http://localhost"), "token",
            new ResourcesModel(10, 6, 4), "EEMCS");
        ResourceNode resourceNode = nodeRepositoryService.addNode(node, "John");
        assertEquals("node", resourceNode.getName());
    }

    @Test
    void addNodeException() throws Exception {
        Node node = new Node("node", new URL("http://localhost"), "token",
            new ResourcesModel(10, 6, 4), "EEMCS");
        nodeRepositoryService.addNode(node, "John");
        assertThatThrownBy(() ->
            nodeRepositoryService.addNode(node, "John")).isInstanceOf(NameAlreadyInUseException.class);
    }

    @Test
    void getAllNodes() throws Exception {
        Node node = new Node("node", new URL("http://localhost"), "token",
            new ResourcesModel(10, 6, 4), "EEMCS");
        nodeRepositoryService.addNode(node, "John");
        Collection<Node> nodes = nodeRepositoryService.getAllNodes();
        assertEquals(1, nodes.size());
        assertEquals("node", nodes.iterator().next().getName());
    }
}