package nl.tudelft.sem.waitinglist.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import nl.tudelft.sem.waitinglist.domain.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
}