package nl.tudelft.sem.waitinglist.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import nl.tudelft.sem.waitinglist.domain.Request;
import nl.tudelft.sem.waitinglist.domain.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockClock"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WaitingListControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestRepository repo;

    @Autowired
    private Clock clock;

    @Test
    void addRequestSuccessfully() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModel requestModel = new RequestModel(name, description, faculty, resourcesModel, deadline);

        LocalDate currentDate = LocalDate.of(2022, 12, 10);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        String serialised = objectMapper.writeValueAsString(requestModel);

        MvcResult result = mockMvc.perform(post("/add-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialised))
                .andExpect(status().isOk())
                .andReturn();

        AddResponseModel response = objectMapper.readValue(result.getResponse().getContentAsString(),
                AddResponseModel.class);
        assertThat(response.getId()).isEqualTo(1);

        Request saved = repo.findById(response.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo(name);
        assertThat(saved.getDescription()).isEqualTo(description);
        assertThat(saved.getFaculty()).isEqualTo(faculty);
        assertThat(saved.getResources().getCpu()).isEqualTo(cpu);
        assertThat(saved.getResources().getGpu()).isEqualTo(gpu);
        assertThat(saved.getResources().getRam()).isEqualTo(ram);
        assertThat(saved.getDeadline()).isEqualTo(deadline);
    }

    @Test
    void addInvalidRequest() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "faculty";
        int cpu = 4;
        int gpu = 5;
        int ram = 5;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModel requestModel = new RequestModel(name, description, faculty, resourcesModel, deadline);

        LocalDate currentDate = LocalDate.of(2022, 12, 10);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        String serialised = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(post("/add-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestListSuccessfully() throws Exception {
        String name = "name2";
        String description = "description2";
        String faculty = "ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        MvcResult result = mockMvc.perform(get("/get-requests-by-faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ewi"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].name").value("name2"))
                        .andReturn();
    }

    @Test
    void getRequestListTwoDifferentFaculty() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        String name2 = "name2";
        String description2 = "description2";
        String faculty2 = "not-ewi";
        Request request2 = new Request(name2, description2, faculty2, resources, deadline, currentDate);
        repo.save(request2);
        repo.save(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        MvcResult result = mockMvc.perform(get("/get-requests-by-faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ewi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andReturn();
    }
    @Test
    void getRequestListEmptyList() throws Exception {
        String name = "name2";
        String description = "description2";
        String faculty = "not-ewi";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDate currentDate = LocalDate.of(2022, 12, 14);
        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        MvcResult result = mockMvc.perform(get("/get-requests-by-faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("ewi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();
    }
}