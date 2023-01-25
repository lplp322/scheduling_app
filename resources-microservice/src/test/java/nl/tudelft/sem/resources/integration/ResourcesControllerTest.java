package nl.tudelft.sem.resources.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.resources.ReleaseRequestModel;
import nl.tudelft.sem.common.models.request.resources.UpdateAvailableResourcesRequestModel;
import nl.tudelft.sem.resources.authentication.AuthManager;
import nl.tudelft.sem.resources.authentication.JwtTokenVerifier;
import nl.tudelft.sem.resources.domain.node.NameAlreadyInUseException;
import nl.tudelft.sem.resources.domain.node.NodeRepositoryService;
import nl.tudelft.sem.resources.domain.resources.ResourceRepositoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the endpoints of the schedule microservice.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "mockResourceRepositoryService",
        "mockNodeRepositoryService"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ResourcesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepositoryService mockResourceRepositoryService;

    @Autowired
    private NodeRepositoryService mockNodeRepositoryService;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Test
    void addNodeSuccessfullyTest() throws Exception {
        // Create node for request.
        String name = "node";
        URL url = new URL("http://localhost");
        String token = "token";
        ResourcesModel resources = new ResourcesModel(1, 2, 3);
        String faculty = "EEMCS";
        Node node = new Node(name, url, token, resources, faculty);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(node);

        // Mock function to get the netID of the authentication manager.
        when(mockAuthenticationManager.getNetId()).thenReturn("User");

        // Send HTTP request and verify response status.
        mockMvc.perform(post("/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

        // Verify that the node is added and resources are updated.
        Mockito.verify(mockNodeRepositoryService).addNode(node, "User");
        Mockito.verify(mockResourceRepositoryService).updateResourceAllocation(node);
    }

    @Test
    void addNodeWithUsedNameTest() throws Exception {
        // Create node for request.
        String name = "node";
        URL url = new URL("http://localhost");
        String token = "token";
        ResourcesModel resources = new ResourcesModel(1, 2, 3);
        String faculty = "EEMCS";
        Node node = new Node(name, url, token, resources, faculty);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(node);

        // Mock function to get the netID of the authentication manager and get exception when adding node.
        when(mockAuthenticationManager.getNetId()).thenReturn("User");
        doThrow(new NameAlreadyInUseException(node.getName())).when(mockNodeRepositoryService).addNode(node, "User");

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(post("/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Verify that the correct exception is thrown.
        assertThat(result.getResponse().getErrorMessage()).isEqualTo("node");
    }

    @Test
    void getAvailableResourcesFacultyTest() throws Exception {
        // Create request.
        String faculty = "EEMCS";
        LocalDate date = LocalDate.of(2023, 10, 11);
        AvailableResourcesRequestModel request = new AvailableResourcesRequestModel(faculty, date);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(request);

        // Mock function to get available resources.
        ResourcesModel resources = new ResourcesModel(1, 2, 3);
        when(mockResourceRepositoryService.getAvailableResources(faculty, date)).thenReturn(resources);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(post("/get-available-resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

        // Verify that the correct resources are returned.
        int cpu = JsonPath.read(result.getResponse().getContentAsString(), "$.cpu");
        int gpu = JsonPath.read(result.getResponse().getContentAsString(), "$.gpu");
        int ram = JsonPath.read(result.getResponse().getContentAsString(), "$.ram");
        assertThat(cpu).isEqualTo(1);
        assertThat(gpu).isEqualTo(2);
        assertThat(ram).isEqualTo(3);
    }

    @Test
    void getAvailableResourcesReleasedTest() throws Exception {
        // Create request.
        String faculty = "released";
        LocalDate date = LocalDate.of(2023, 10, 11);
        AvailableResourcesRequestModel request = new AvailableResourcesRequestModel(faculty, date);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(request);

        // Mock function to get available resources.
        ResourcesModel resources = new ResourcesModel(1, 2, 3);
        when(mockResourceRepositoryService.getAvailableResources(date)).thenReturn(resources);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(post("/get-available-resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

        // Verify that the correct resources are returned.
        int cpu = JsonPath.read(result.getResponse().getContentAsString(), "$.cpu");
        int gpu = JsonPath.read(result.getResponse().getContentAsString(), "$.gpu");
        int ram = JsonPath.read(result.getResponse().getContentAsString(), "$.ram");
        assertThat(cpu).isEqualTo(1);
        assertThat(gpu).isEqualTo(2);
        assertThat(ram).isEqualTo(3);
    }

    @Test
    void getAvailableResourcesNonExistingFacultyTest() throws Exception {
        // Create request.
        String faculty = "Non-existing";
        LocalDate date = LocalDate.of(2023, 10, 11);
        AvailableResourcesRequestModel request = new AvailableResourcesRequestModel(faculty, date);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(request);

        // Mock function to get available resources.
        doThrow(NoSuchElementException.class).when(mockResourceRepositoryService).getAvailableResources(faculty, date);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(post("/get-available-resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isNotFound())
                .andReturn();

        // Verify that the correct resources are returned.
        assertThat(result.getResponse().getErrorMessage()).isEqualTo("Faculty does not exist");
    }

    @Test
    void updateAvailableResourcesSuccessfullyTest() throws Exception {
        // Create request.
        LocalDate date = LocalDate.of(2023, 10, 11);
        String faculty = "EEMCS";
        int cpu = 1;
        int gpu = 2;
        int ram = 3;
        UpdateAvailableResourcesRequestModel request = new UpdateAvailableResourcesRequestModel(date, faculty,
                cpu, gpu, ram);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(request);

        // Mock function to update available resources.
        when(mockResourceRepositoryService.updateUsedResources(date, faculty, new ResourcesModel(cpu, gpu, ram)))
                .thenReturn(true);

        // Send HTTP request and verify response status.
        mockMvc.perform(post("/available-resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void updateAvailableResourcesInvalidTest() throws Exception {
        // Create request.
        LocalDate date = LocalDate.of(2023, 10, 11);
        String faculty = "EEMCS";
        int cpu = -1;
        int gpu = -2;
        int ram = -3;
        UpdateAvailableResourcesRequestModel request = new UpdateAvailableResourcesRequestModel(date, faculty,
                cpu, gpu, ram);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(request);

        // Mock function to update available resources.
        when(mockResourceRepositoryService.updateUsedResources(date, faculty, new ResourcesModel(cpu, gpu, ram)))
                .thenReturn(false);

        // Send HTTP request and verify response status.
        mockMvc.perform(post("/available-resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
    }

    @Test
    void getNodesAsOnlySysadminTest() throws Exception {
        // Mock functions to check roles for authentication and getting all nodes.
        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("sysadmin"));
        Mockito.doReturn(roles).when(mockAuthenticationManager).getRoles();

        Collection nodes = new ArrayList();
        nodes.add(new Node("node1", new URL("http://localhost"), "token1",
                new ResourcesModel(1, 2, 3), "EEMCS"));
        nodes.add(new Node("node2", new URL("http://localhost"), "token2",
                new ResourcesModel(6, 5, 4), "ID"));
        when(mockNodeRepositoryService.getAllNodes()).thenReturn(nodes);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(get("/nodes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        /// Assert that the correct requests are returned.
        String nodesString = JsonPath.read(result.getResponse().getContentAsString(),
                "$.nodes").toString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Node> returnedNodes = objectMapper.readValue(nodesString,
                new TypeReference<List<Node>>() {});
        for (Node node : returnedNodes) {
            assertThat(nodes).contains(node);
        }
    }

    @Test
    void getNodesAsSysadminWithMultipleRolesTest() throws Exception {
        // Mock functions to check roles for authentication and getting all nodes.
        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("employee_EEMCS"));
        roles.add(new SimpleGrantedAuthority("sysadmin"));
        roles.add(new SimpleGrantedAuthority("admin_EEMCS"));
        Mockito.doReturn(roles).when(mockAuthenticationManager).getRoles();

        Collection nodes = new ArrayList();
        nodes.add(new Node("node1", new URL("http://localhost"), "token1",
                new ResourcesModel(1, 2, 3), "EEMCS"));
        nodes.add(new Node("node2", new URL("http://localhost"), "token2",
                new ResourcesModel(6, 5, 4), "ID"));
        when(mockNodeRepositoryService.getAllNodes()).thenReturn(nodes);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(get("/nodes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        /// Assert that the correct requests are returned.
        String nodesString = JsonPath.read(result.getResponse().getContentAsString(),
                "$.nodes").toString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Node> returnedNodes = objectMapper.readValue(nodesString,
                new TypeReference<List<Node>>() {});
        for (Node node : returnedNodes) {
            assertThat(nodes).contains(node);
        }
    }

    @Test
    void getNoNodesNotSysadminTest() throws Exception {
        // Mock functions to check roles for authentication and getting all nodes.
        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("employee_EEMCS"));
        roles.add(new SimpleGrantedAuthority("admin_EEMCS"));
        Mockito.doReturn(roles).when(mockAuthenticationManager).getRoles();

        Collection nodes = new ArrayList();
        nodes.add(new Node("node1", new URL("http://localhost"), "token1",
                new ResourcesModel(1, 2, 3), "EEMCS"));
        nodes.add(new Node("node2", new URL("http://localhost"), "token2",
                new ResourcesModel(6, 5, 4), "ID"));
        when(mockNodeRepositoryService.getAllNodes()).thenReturn(nodes);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(get("/nodes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        /// Assert that the correct requests are returned.
        assertThat(result.getResponse().getErrorMessage()).isEqualTo("You are not authorized to make this request!");
    }

    @Test
    void releaseResourcesSuccessfullyTest() throws Exception {
        // Create request.
        LocalDate date = LocalDate.of(2023, 10, 11);
        String faculty = "EEMCS";
        int cpu = 1;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resources = new ResourcesModel(cpu, gpu, ram);
        ReleaseRequestModel request = new ReleaseRequestModel(resources, faculty, date, date);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(request);

        // Mock function to update available resources.
        when(mockResourceRepositoryService.releaseResources(resources, faculty, date, date))
                .thenReturn(true);

        // Send HTTP request and verify response status.
        mockMvc.perform(post("/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void releaseResourcesInvalidTest() throws Exception {
        // Create request.
        LocalDate date = LocalDate.of(2023, 10, 11);
        String faculty = "EEMCS";
        int cpu = -1;
        int gpu = -2;
        int ram = -3;
        ResourcesModel resources = new ResourcesModel(cpu, gpu, ram);
        ReleaseRequestModel request = new ReleaseRequestModel(resources, faculty, date, date);

        // Serialise node to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(request);

        // Mock function to update available resources.
        when(mockResourceRepositoryService.releaseResources(resources, faculty, date, date))
                .thenReturn(false);

        // Send HTTP request and verify response status.
        mockMvc.perform(post("/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
