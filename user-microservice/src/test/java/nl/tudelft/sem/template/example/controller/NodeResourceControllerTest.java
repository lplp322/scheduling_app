package nl.tudelft.sem.template.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.Collection;
import nl.tudelft.sem.common.models.request.resources.PostNodeRequestModel;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.example.feigninterfaces.ResourcesInterface;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
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
public class NodeResourceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    private transient ResourcesInterface resourcesInterface;

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
    public void testAddNode() {
        PostNodeRequestModel data = new PostNodeRequestModel();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(data);
            when(resourcesInterface.addNode(any())).thenReturn(ResponseEntity.ok("Here"));
            mockMvc
                .perform(post("/resources/add-node").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Here"));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    public void testAddNodeException() {
        PostNodeRequestModel data = new PostNodeRequestModel();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(data);
            when(resourcesInterface.addNode(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check"));
            mockMvc
                .perform(post("/resources/add-node").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(result -> assertTrue(result.getResolvedException()
                    instanceof ResponseStatusException));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }
}
