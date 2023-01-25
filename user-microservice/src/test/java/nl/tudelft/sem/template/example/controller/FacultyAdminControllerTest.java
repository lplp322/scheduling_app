package nl.tudelft.sem.template.example.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import nl.tudelft.sem.template.example.requests.RequestService;
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

import java.util.ArrayList;
import java.util.Collection;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FacultyAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WaitingListInterface waitingListInterface;

    @MockBean
    private RequestService requestService;

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
    public void testDeleteRequestApproved() {
        try {
            when(waitingListInterface.rejectRequest(1L)).thenReturn(ResponseEntity.ok("Yes"));
            mockMvc.perform(delete("/faculty-admin/reject-request").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("1")).andExpect(status().isOk());
            verify(requestService, times(1)).updateRequestStatus(1L, RequestStatus.REJECTED);
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void testDeleteRequestRejected() {
        try {
            when(waitingListInterface.rejectRequest(1L)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            mockMvc.perform(delete("/faculty-admin/reject-request").header("Authorization", "Bearer MockedToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content("1")).andExpect(result -> assertTrue(result.getResolvedException()
                instanceof ResponseStatusException));
            verify(requestService, never()).updateRequestStatus(1L, RequestStatus.REJECTED);
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void testAcceptedRequestAccepted() {
        try {
            when(waitingListInterface.acceptRequest(any())).thenReturn(ResponseEntity.ok("Yes"));
            mockMvc.perform(post("/faculty-admin/accept-request").header("Authorization", "Bearer MockedToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{"
                                    + "\"id\": 1, "
                                    + "\"plannedDate\": \"2022-12-23\" "
                                    + "}"))
                    .andExpect(status().isOk());
            verify(requestService, times(1)).updateRequestStatus(1L, RequestStatus.ACCEPTED);
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void testAcceptedRequestRejected() {
        try {
            when(waitingListInterface.acceptRequest(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            mockMvc.perform(post("/faculty-admin/accept-request").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{"
                            + "\"id\": 1, "
                            + "\"plannedDate\": \"2022-12-23\" "
                            + "}"))
                    .andExpect(result -> assertTrue(result.getResolvedException()
                    instanceof ResponseStatusException));
            verify(requestService, never()).updateRequestStatus(1L, RequestStatus.ACCEPTED);
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void testGetRequestByFaculty() {
        try {
            when(waitingListInterface.getRequestsByFaculty(any())).thenReturn(ResponseEntity.ok("string"));
            mockMvc.perform(get("/faculty-admin/get-requests-by-faculty").header("Authorization", "Bearer MockedToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("faculty"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            assertEquals(1, 0);
        }
        try {
            when(waitingListInterface.getRequestsByFaculty(any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            mockMvc.perform(get("/faculty-admin/get-requests-by-faculty").header("Authorization", "Bearer MockedToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("wrong faculty"))
                    .andExpect(result -> assertTrue(result.getResolvedException()
                            instanceof ResponseStatusException));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    @Transactional
    public void testAcceptRequestBadRequest() {
        try {
            when(waitingListInterface.acceptRequest(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            mockMvc.perform(post("/faculty-admin/accept-request").header("Authorization", "Bearer MockedToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{"
                                    + "\"id\": 1, "
                                    + "\"plannedDate\": \"2022-12-23\" "
                                    + "}"))
                    .andExpect(status().isBadRequest());
            verify(requestService, never()).updateRequestStatus(1L, RequestStatus.ACCEPTED);
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }
}
