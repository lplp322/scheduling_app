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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.authentication.JwtTokenVerifier;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
public class RequestReceivingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WaitingListInterface waitingListInterface;

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
    public void testRegisterNormal() {
        RequestModelWaitingList request = new RequestModelWaitingList();
        request.setName("John");
        request.setFaculty("EEMCS");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            when(waitingListInterface.addRequest(any())).thenReturn(ResponseEntity.ok(new AddResponseModel(1L)));
            mockMvc
                .perform(post("/request").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Your request was created. Request ID: 1"));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    public void testRegisterWithException() {
        RequestModelWaitingList request = new RequestModelWaitingList();
        request.setName("John");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            Exception e = new Exception("Test this");
            when(waitingListInterface.addRequest(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e));
            mockMvc
                .perform(post("/request").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRequest)).andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ResponseStatusException));

        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    public void testAddRequestBadRequest() throws Exception {
        RequestModelWaitingList request = new RequestModelWaitingList();
        request.setName("John");
        request.setFaculty("EEMCS");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialisedRequest = objectMapper.writeValueAsString(request);
        when(waitingListInterface.addRequest(any()))
            .thenReturn(new ResponseEntity<>(new AddResponseModel(1L), HttpStatus.BAD_REQUEST));
        mockMvc
            .perform(post("/request").header("Authorization", "Bearer MockedToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialisedRequest))
            .andExpect(status().isOk())
            .andExpect(content()
                .string("Your request returned: 400 BAD_REQUEST With body: AddResponseModel(id=1)"));
    }
}
