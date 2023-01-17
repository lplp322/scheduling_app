package nl.tudelft.sem.template.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.resources.ReleaseRequestModel;
import nl.tudelft.sem.common.models.response.resources.AvailableResourcesResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.example.feigninterfaces.ResourcesInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReleaseResourceFacultyAdminTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ResourcesInterface resourcesInterface;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    /**
     * Configuration before tests.
     */
    @BeforeEach
    public void configure() {
        when(mockAuthenticationManager.getNetId()).thenReturn("John");
        Collection<SimpleGrantedAuthority> roleList = new ArrayList<>();
        roleList.add(new SimpleGrantedAuthority("employee_EEMCS"));
        roleList.add(new SimpleGrantedAuthority("admin_EEMCS"));
        Mockito.doReturn(roleList).when(mockAuthenticationManager).getRoles();
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("John");
    }

    @Test
    @Transactional
    public void releaseResourceTest() {
        ReleaseRequestModel request = new ReleaseRequestModel();
        request.setFaculty("EEMCS");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            when(resourcesInterface.releaseResources(any())).thenReturn(ResponseEntity.ok("Here"));
            mockMvc
                .perform(post("/faculty-admin/release-resources").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Here"));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void releaseResourceUnauthorized() {
        ReleaseRequestModel request = new ReleaseRequestModel();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            when(resourcesInterface.releaseResources(any())).thenReturn(ResponseEntity.ok("Here"));
            mockMvc
                .perform(post("/faculty-admin/release-resources").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(result -> assertTrue(result.getResolvedException()
                instanceof ResponseStatusException)).andExpect(result ->
                    assertEquals("401 UNAUTHORIZED \"You are not authorized to make this request!\"",
                        result.getResolvedException()
                        .getMessage()));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void releaseResourceBadRequest() {
        ReleaseRequestModel request = new ReleaseRequestModel();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            when(resourcesInterface.releaseResources(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Here"));
            mockMvc
                .perform(post("/faculty-admin/release-resources").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(result -> assertTrue(result.getResolvedException()
                    instanceof ResponseStatusException));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void checkResources() {
        AvailableResourcesRequestModel request =
            new AvailableResourcesRequestModel("EEMCS", LocalDate.parse("2023-01-01"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            when(resourcesInterface.getAvailableResources(any()))
                .thenReturn(ResponseEntity.ok(new AvailableResourcesResponseModel(1, 1, 1)));
            mockMvc
                .perform(get("/faculty-admin/available-resources").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(status().isOk());
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void checkResourcesExeption() {
        AvailableResourcesRequestModel request =
            new AvailableResourcesRequestModel("EEMCS", LocalDate.parse("2023-01-01"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            when(resourcesInterface.getAvailableResources(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check"));
            mockMvc
                .perform(get("/faculty-admin/available-resources").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(result -> assertTrue(result.getResolvedException()
                    instanceof ResponseStatusException));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }
}
