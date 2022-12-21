package nl.tudelft.sem.template.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
public class RequestReceivingStrategyTest {
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
        when(mockAuthenticationManager.getNetId()).thenReturn("ivank");
        Collection<SimpleGrantedAuthority> roleList = new ArrayList<>();
        roleList.add(new SimpleGrantedAuthority("employee_CSE"));
        roleList.add(new SimpleGrantedAuthority("admin_CSE"));
        Mockito.doReturn(roleList).when(mockAuthenticationManager).getRoles();
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ivank");
    }

    @Captor
    ArgumentCaptor<RequestModel> requestCaptor;

    @Test
    public void testRequestFromText() {
        String text = "ivank,testThis,CSE,3,2,1,2023-10-12";
        try {
            when(waitingListInterface.addRequest(any())).thenReturn(ResponseEntity.ok(new AddResponseModel(1L)));
            mockMvc
                .perform(post("/request").header("Authorization", "Bearer MockedToken")
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .content(text))
                .andExpect(status().isOk())
                .andExpect(content().string("Your request was created. Request ID: 1"));
            verify(waitingListInterface).addRequest(requestCaptor.capture());
            RequestModel model = requestCaptor.getValue();
            assertEquals(model.getName(), "ivank");
            assertEquals(model.getDescription(), "testThis");
            assertEquals(model.getFaculty(), "CSE");
            assertEquals(model.getResources().getCpu(), 3);
            assertEquals(model.getResources().getGpu(), 2);
            assertEquals(model.getResources().getRam(), 1);
            assertEquals(model.getDeadline(), LocalDate.parse("2023-10-12"));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    public void testRegisterNormal() {
        RequestModel request = new RequestModel("ivank", "testThis", "CSE",
            new ResourcesModel(3, 2, 1), LocalDate.parse("2023-10-12"));
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
            verify(waitingListInterface).addRequest(requestCaptor.capture());
            RequestModel model = requestCaptor.getValue();
            assertEquals(model.getName(), "ivank");
            assertEquals(model.getDescription(), "testThis");
            assertEquals(model.getFaculty(), "CSE");
            assertEquals(model.getResources().getCpu(), 3);
            assertEquals(model.getResources().getGpu(), 2);
            assertEquals(model.getResources().getRam(), 1);
            assertEquals(model.getDeadline(), LocalDate.parse("2023-10-12"));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }
}
