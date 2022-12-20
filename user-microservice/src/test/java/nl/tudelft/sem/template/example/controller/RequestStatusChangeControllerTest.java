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
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestStatusChangeControllerTest {
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
    @Transactional
    public void testStatusChange() {
        RequestModel request = new RequestModel();
        request.setName("John");
        request.setFaculty("EEMCS");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String serialisedRequest = objectMapper.writeValueAsString(request);
            System.out.println(serialisedRequest);
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
        try {
            ChangeRequestStatus status = new ChangeRequestStatus(1L, RequestStatus.REJECTED);
            String serialisedStatus = objectMapper.writeValueAsString(status);
            String serialisedLong = objectMapper.writeValueAsString(1L);
            mockMvc.perform(post("/request-status/change-status").contentType(MediaType.APPLICATION_JSON)
                .content(serialisedStatus)).andExpect(status().isOk()).andExpect(content().string("Changed"));
            MvcResult result = mockMvc.perform(get("/request-status").header("Authorization", "Bearer MockedToken")
                .contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(serialisedLong)).andExpect(status().isOk()).andReturn();
            RequestStatus res = objectMapper.readValue(result.getResponse().getContentAsString(), RequestStatus.class);
            assertEquals(RequestStatus.REJECTED, res);
        } catch (Exception e) {
            assertEquals(1, 0);
        }

    }

    @Test
    @Transactional
    public void testStatusChangeException() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ChangeRequestStatus status = new ChangeRequestStatus(-1L, RequestStatus.REJECTED);
            String serialisedStatus = objectMapper.writeValueAsString(status);
            String serialisedLong = objectMapper.writeValueAsString(1L);
            mockMvc.perform(post("/request-status/change-status").contentType(MediaType.APPLICATION_JSON)
                .content(serialisedStatus)).andExpect(result -> assertTrue(result.getResolvedException()
                instanceof ResponseStatusException));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }
}
