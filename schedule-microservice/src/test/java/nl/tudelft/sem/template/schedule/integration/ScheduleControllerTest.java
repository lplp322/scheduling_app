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
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.resources.UpdateAvailableResourcesRequestModel;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import nl.tudelft.sem.template.schedule.external.ResourcesInterface;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.ocsp.Req;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTime", "mockScheduleService", "mockResourcesInterface"})
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
    private ResourcesInterface resourcesInterface;

    @Test
    void scheduleRequestSuccessfully() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 24, 23, 55);
        when(mockTime.now()).thenReturn(currentDateTime);
        when(resourcesInterface.getAvailableResources(new AvailableResourcesRequestModel(faculty, deadline)))
                .thenReturn(ResponseEntity.ok(new ResourcesModel(5, 2, 3)));
        when(resourcesInterface.updateAvailableResources(new UpdateAvailableResourcesRequestModel(deadline,
                faculty, cpu, gpu, ram))).thenReturn(ResponseEntity.ok().build());

        String serialised = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                        .andExpect(status().isOk())
                        .andReturn();

        Mockito.verify(mockScheduleService).scheduleRequest(requestModel);
    }

    @Test
    void scheduleRequestWithInvalidResources() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 4;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 24, 23, 55);
        when(mockTime.now()).thenReturn(currentDateTime);

        String serialised = objectMapper.writeValueAsString(requestModel);

        String error = mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException().getMessage();

        assertTrue(StringUtils.contains(error,
                "You cannot schedule a request requiring more GPU and memory resources than CPU resources"));
    }

    @Test
    void scheduleRequestWrongDate() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 25, 0, 0);
        when(mockTime.now()).thenReturn(currentDateTime);

        String serialised = objectMapper.writeValueAsString(requestModel);

        String error = mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResolvedException().getMessage();

        assertTrue(StringUtils.contains(error, "You cannot schedule any requests for this date anymore."));
    }

    @Test
    void scheduleRequestJustTooLate() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 24, 23, 56);
        when(mockTime.now()).thenReturn(currentDateTime);

        String serialised = objectMapper.writeValueAsString(requestModel);

        String error = mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException().getMessage();

        assertTrue(StringUtils.contains(error, "You cannot schedule any requests for this date anymore."));
    }

    @Test
    void scheduleRequestNoDate() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = null;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 25, 0, 0);
        when(mockTime.now()).thenReturn(currentDateTime);

        String serialised = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void scheduleRequestNotEnoughResources() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 24, 23, 55);
        when(mockTime.now()).thenReturn(currentDateTime);
        when(resourcesInterface.getAvailableResources(new AvailableResourcesRequestModel(faculty, deadline)))
                .thenReturn(ResponseEntity.ok(new ResourcesModel(4, 2, 3)));

        String serialised = objectMapper.writeValueAsString(requestModel);

        String error = mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException().getMessage();

        assertTrue(StringUtils.contains(error,
                "There are not enough resources available on this date for this request."));
    }

    @Test
    void scheduleRequestUpdateResourcesFails() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 2;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelSchedule requestModel = new RequestModelSchedule(0, name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 24, 23, 55);
        when(mockTime.now()).thenReturn(currentDateTime);
        when(resourcesInterface.getAvailableResources(new AvailableResourcesRequestModel(faculty, deadline)))
                .thenReturn(ResponseEntity.ok(new ResourcesModel(5, 2, 3)));
        when(resourcesInterface.updateAvailableResources(new UpdateAvailableResourcesRequestModel(deadline,
                faculty, cpu, gpu, ram))).thenThrow(ResponseStatusException.class);

        String serialised = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void getScheduledRequestsEmpty() throws Exception {
        LocalDate date = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        DateModel requestModel = new DateModel(date);

        when(mockScheduleService.getSchedule(date)).thenReturn(new ArrayList<>());

        String serialised = objectMapper.writeValueAsString(requestModel);

        MvcResult result = mockMvc.perform(get("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

        String dateString = JsonPath.read(result.getResponse().getContentAsString(), "$.date");
        LocalDate returnedDate = LocalDate.parse(dateString);
        JSONArray returnedRequests = JsonPath.read(result.getResponse().getContentAsString(),
                "$.requests");

        assertThat(returnedDate).isEqualTo(date);
        assertThat(returnedRequests.size()).isEqualTo(0);
    }

    @Test
    void getScheduledRequests() throws Exception {
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

        LocalDate date = LocalDate.of(2022, 12, 25);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        DateModel requestModel = new DateModel(date);

        when(mockScheduleService.getSchedule(date)).thenReturn(scheduledRequests);

        String serialised = objectMapper.writeValueAsString(requestModel);

        MvcResult result = mockMvc.perform(get("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

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
