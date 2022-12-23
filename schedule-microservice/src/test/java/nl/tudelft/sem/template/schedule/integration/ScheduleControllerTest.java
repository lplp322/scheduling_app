package nl.tudelft.sem.template.schedule.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import nl.tudelft.sem.common.models.providers.TimeProvider;
import nl.tudelft.sem.common.models.request.DateModel;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.template.schedule.authentication.AuthManager;
import nl.tudelft.sem.template.schedule.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.ocsp.Req;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the endpoints of the schedule microservice.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTime", "mockScheduleService", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TimeProvider mockTime;

    @Autowired
    private ScheduleService mockScheduleService;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    /**
     * Test if a request is scheduled successfully when the request is correct.
     */
    @Test
    void scheduleRequestSuccessfully() throws Exception {
        // Create a request.
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        // Mock function to get the current date and time.
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 24, 23, 55);
        when(mockTime.now()).thenReturn(currentDateTime);

        // Serialise request to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(requestModel);

        // Send HTTP request and verify response status.
        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                        .andExpect(status().isOk())
                        .andReturn();

        // Verify that the request is scheduled.
        Mockito.verify(mockScheduleService).scheduleRequest(requestModel);
    }

    /**
     * Test if a bad request is returned when the request should be scheduled on the current date.
     */
    @Test
    void scheduleRequestWrongDate() throws Exception {
        // Create a request.
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        // Mock function to get the current date and time.
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 25, 0, 0);
        when(mockTime.now()).thenReturn(currentDateTime);

        // Serialise request to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(requestModel);

        // Send HTTP request and verify response status.
        String error = mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResolvedException().getMessage();

        // Assert that the correct error message is send.
        assertTrue(StringUtils.contains(error, "You cannot schedule any requests for this date anymore."));
    }

    /**
     * Test if a bad request is returned when the request should be scheduled on the next date,
     * but the request is scheduled in the last 5 minutes of the current date.
     */
    @Test
    void scheduleRequestJustTooLate() throws Exception {
        // Create a request.
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        // Mock function to get the current date and time.
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 24, 23, 56);
        when(mockTime.now()).thenReturn(currentDateTime);

        // Serialise request to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(requestModel);

        // Send HTTP request and verify response status.
        String error = mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException().getMessage();

        // Assert that the correct error message is send.
        assertTrue(StringUtils.contains(error, "You cannot schedule any requests for this date anymore."));
    }

    /**
     * Test if a bad request is returned when there is no date given for the request to be scheduled on.
     */
    @Test
    void scheduleRequestNoDate() throws Exception {
        // Create a request.
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = null;
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        // Mock function to get the current date and time.
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 25, 0, 0);
        when(mockTime.now()).thenReturn(currentDateTime);

        // Serialise request to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(requestModel);

        // Send HTTP request and verify response status.
        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Test if there are no requests returned when the schedule is asked of a date where no requests are scheduled.
     */
    @Test
    void getScheduledRequestsEmpty() throws Exception {
        // Mock the authorization checking.
        when(mockAuthenticationManager.getNetId()).thenReturn("John");
        Collection<SimpleGrantedAuthority> roleList = new ArrayList<>();
        roleList.add(new SimpleGrantedAuthority("sysadmin"));
        Mockito.doReturn(roleList).when(mockAuthenticationManager).getRoles();
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("John");

        // Create date to get the schedule from.
        LocalDate date = LocalDate.of(2022, 12, 25);
        DateModel requestModel = new DateModel(date);

        // Mock function to get the scheduled requests.
        when(mockScheduleService.getSchedule(date)).thenReturn(new ArrayList<>());

        // Serialise request to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(requestModel);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(get("/schedule")
                        .header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

        // Assert that there are no returned requests.
        String dateString = JsonPath.read(result.getResponse().getContentAsString(), "$.date");
        LocalDate returnedDate = LocalDate.parse(dateString);
        JSONArray returnedRequests = JsonPath.read(result.getResponse().getContentAsString(),
                "$.requests");
        assertThat(returnedDate).isEqualTo(date);
        assertThat(returnedRequests.size()).isEqualTo(0);
    }

    /**
     * Test if bad request is returned when the user requesting is not a sysadmin.
     */
    @Test
    void getScheduledRequestsNotAuthorized() throws Exception {
        // Mock the authorization checking.
        when(mockAuthenticationManager.getNetId()).thenReturn("John");
        Collection<SimpleGrantedAuthority> roleList = new ArrayList<>();
        roleList.add(new SimpleGrantedAuthority("employee_EEMCS"));
        roleList.add(new SimpleGrantedAuthority("admin_EEMCS"));
        roleList.add(new SimpleGrantedAuthority("employee_IDE"));
        Mockito.doReturn(roleList).when(mockAuthenticationManager).getRoles();
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("John");

        // Create date to get the schedule from.
        LocalDate date = LocalDate.of(2022, 12, 25);
        DateModel requestModel = new DateModel(date);

        // Mock function to get the scheduled requests.
        when(mockScheduleService.getSchedule(date)).thenReturn(new ArrayList<>());

        // Serialise request to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(requestModel);

        // Send HTTP request and verify response status.
        String error = mockMvc.perform(get("/schedule")
                        .header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException().getMessage();

        // Assert that the correct error message is send.
        assertTrue(StringUtils.contains(error, "Only sysadmins can view the schedules."));
    }

    /**
     * Test if the correct requests are returned when the schedule is asked of a specific date.
     */
    @Test
    void getScheduledRequests() throws Exception {
        // Mock the authorization checking.
        when(mockAuthenticationManager.getNetId()).thenReturn("John");
        Collection<SimpleGrantedAuthority> roleList = new ArrayList<>();
        roleList.add(new SimpleGrantedAuthority("sysadmin"));
        Mockito.doReturn(roleList).when(mockAuthenticationManager).getRoles();
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("John");

        // Create request 1.
        List<ScheduledRequest> scheduledRequests = new ArrayList<>();
        Set<RequestModelSchedule> requestModels = new HashSet<>();
        long id1 = 0;
        String name1 = "Research project";
        String description1 = "Needs a lot of resources";
        String faculty1 = "TN";
        int cpuUsage1 = 6;
        int gpuUsage1 = 2;
        int memoryUsage1 = 3;
        LocalDate deadline1 = LocalDate.of(2022, 12, 19);
        requestModels.add(new RequestModelSchedule(id1, name1, description1, faculty1,
                new ResourcesModel(cpuUsage1, gpuUsage1, memoryUsage1), deadline1));
        scheduledRequests.add(new ScheduledRequest(id1, name1, description1, faculty1,
                cpuUsage1, gpuUsage1, memoryUsage1, deadline1));

        // Create request 2.
        long id2 = 1;
        String name2 = "Small calculations";
        String description2 = "Only needs CPU resources";
        String faculty2 = "TW";
        int cpuUsage2 = 3;
        int gpuUsage2 = 0;
        int memoryUsage2 = 0;
        LocalDate deadline2 = LocalDate.of(2022, 12, 19);
        requestModels.add(new RequestModelSchedule(id2, name2, description2, faculty2,
                new ResourcesModel(cpuUsage2, gpuUsage2, memoryUsage2), deadline2));
        scheduledRequests.add(new ScheduledRequest(id2, name2, description2, faculty2,
                cpuUsage2, gpuUsage2, memoryUsage2, deadline2));

        // Create date to get the schedule from.
        LocalDate date = LocalDate.of(2022, 12, 25);
        DateModel requestModel = new DateModel(date);

        // Mock function to get the scheduled requests.
        when(mockScheduleService.getSchedule(date)).thenReturn(scheduledRequests);

        // Serialise request to use in the HTTP request.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String serialised = objectMapper.writeValueAsString(requestModel);

        // Send HTTP request and verify response status.
        MvcResult result = mockMvc.perform(get("/schedule")
                        .header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

        // Assert that the correct requests are returned.
        String dateString = JsonPath.read(result.getResponse().getContentAsString(), "$.date");
        LocalDate returnedDate = LocalDate.parse(dateString);
        String requestsString = JsonPath.read(result.getResponse().getContentAsString(),
                "$.requests").toString();
        List<RequestModelSchedule> returnedRequests = objectMapper.readValue(requestsString,
                new TypeReference<List<RequestModelSchedule>>() {});
        assertThat(returnedDate).isEqualTo(date);
        for (RequestModelSchedule request : returnedRequests) {
            System.out.println(request);
            assertThat(requestModels).contains(request);
        }
    }
}
