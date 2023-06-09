package nl.tudelft.sem.waitinglist.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.waitinglist.authentication.AuthManager;
import nl.tudelft.sem.waitinglist.authentication.JwtTokenVerifier;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import nl.tudelft.sem.waitinglist.domain.Request;
import nl.tudelft.sem.waitinglist.domain.Resources;
import nl.tudelft.sem.waitinglist.external.SchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockClock", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WaitingListControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestRepository repo;

    @Autowired
    private Clock clock;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @MockBean
    private SchedulerService schedulerService;

    @MockBean
    private AuthManager authManager;


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
    void addRequestSuccessfully() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelWaitingList requestModel = new RequestModelWaitingList(name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 10, 23, 59, 59);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        String serialised = objectMapper.writeValueAsString(requestModel);

        MvcResult result = mockMvc.perform(post("/add-request").header("Authorization", "Bearer MockedToken")
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
    void addRequestUnauthorised() throws Exception {
        when(mockAuthenticationManager.getNetId()).thenReturn("Peter");
        Collection<SimpleGrantedAuthority> roleList = new ArrayList<>();
        roleList.add(new SimpleGrantedAuthority("employee_EEMCS"));
        Mockito.doReturn(roleList).when(mockAuthenticationManager).getRoles();
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("Peter");

        String name = "Peter";
        String description = "desc";
        String faculty = "IDE";
        int cpu = 10;
        int gpu = 5;
        int ram = 3;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);

        LocalDate deadline = LocalDate.of(2022, 12, 12);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelWaitingList requestModel = new RequestModelWaitingList(name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 10, 23, 59, 59);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        String serialised = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(post("/add-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isUnauthorized());

        assertThat(repo.findAll()).isEmpty();
    }

    @Test
    void addForNextDay5MinutesBefore() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelWaitingList requestModel = new RequestModelWaitingList(name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 11, 23, 54, 59);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        String serialised = objectMapper.writeValueAsString(requestModel);

        MvcResult result = mockMvc.perform(post("/add-request").header("Authorization", "Bearer MockedToken")
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
    void addRequestLessThan5MinutesBeforeDeadline() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelWaitingList requestModel = new RequestModelWaitingList(name, description, faculty,
                resourcesModel, deadline);

        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 11, 23, 55);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        String serialised = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(post("/add-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInvalidRequest() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 4;
        int gpu = 5;
        int ram = 5;
        ResourcesModel resourcesModel = new ResourcesModel(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RequestModelWaitingList requestModel = new RequestModelWaitingList(name, description, faculty,
                resourcesModel, deadline);

        LocalDate currentDate = LocalDate.of(2022, 12, 10);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant());

        String serialised = objectMapper.writeValueAsString(requestModel);

        mockMvc.perform(post("/add-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialised))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestListSuccessfully() throws Exception {
        String name = "John";
        String description = "description2";
        String faculty = "EEMCS";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 12, 12);
        Request request = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        MvcResult result = mockMvc.perform(post("/get-requests-by-faculty").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("EEMCS"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].name").value("John"))
                        .andReturn();
    }

    @Test
    void getRequestListTwoDifferentFaculty() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 23, 23);
        Request request = new Request(name, description, faculty, resources, deadline, currentDateTime);
        String name2 = "Alice";
        String description2 = "description2";
        String faculty2 = "IO";
        Request request2 = new Request(name2, description2, faculty2, resources, deadline, currentDateTime);
        repo.save(request2);
        repo.save(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        MvcResult result = mockMvc.perform(post("/get-requests-by-faculty").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("EEMCS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(name))
                .andReturn();
    }

    @Test
    void getRequestListEmptyList() throws Exception {
        String name = "Alice";
        String description = "description2";
        String faculty = "IO";
        Resources resources = new Resources(6, 5, 1);
        LocalDate deadline = LocalDate.of(2022, 12, 15);
        LocalDateTime currentDateTime = LocalDateTime.of(2022, 12, 14, 22, 22);
        Request request = new Request(name, description, faculty, resources, deadline, currentDateTime);
        repo.save(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDateTime.toInstant(ZoneOffset.UTC));

        MvcResult result = mockMvc.perform(post("/get-requests-by-faculty").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("EEMCS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();
    }

    @Test
    void getRequestListUnauthorised() throws Exception {
        mockMvc.perform(post("/get-requests-by-faculty").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("IDE"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectNoSuchId() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        mockMvc.perform(delete("/reject-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isBadRequest());

        assertThat(repo.existsById(1L)).isTrue();
    }

    @Test
    void rejectSuccessful() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        Request request2 = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request2);

        mockMvc.perform(delete("/reject-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1"))
                .andExpect(status().isOk());

        assertThat(repo.existsById(1L)).isFalse();
        assertThat(repo.existsById(2L)).isTrue();
    }

    @Test
    void rejectUnauthorised() throws Exception {
        String name = "Charles";
        String description = "description";
        String faculty = "IDE";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        mockMvc.perform(delete("/reject-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1"))
                .andExpect(status().isUnauthorized());

        assertThat(repo.existsById(1L)).isTrue();
    }

    @Test
    void acceptRequestNoSuchId() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2023, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        mockMvc.perform(post("/accept-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"id\": 2, "
                                + "\"plannedDate\": \"2023-12-10\" "
                                + "}"))
                .andExpect(status().isBadRequest());

        assertThat(repo.existsById(1L)).isTrue();
    }

    @Test
    void acceptRequestAccepted() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2023, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);
        assertThat(repo.existsById(1L)).isTrue();
        mockMvc.perform(post("/accept-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"id\": 1, "
                                + "\"plannedDate\": \"2023-12-10\" "
                                + "}"))
                .andExpect(status().isOk());
        assertThat(repo.existsById(1L)).isFalse();
    }

    @Test
    void acceptRequestDatePassedDeadline() throws Exception {
        String name = "name";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        mockMvc.perform(post("/accept-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"id\": 1, "
                                + "\"plannedDate\": \"2023-12-10\" "
                                + "}"))
                .andExpect(status().isBadRequest());

        assertThat(repo.existsById(1L)).isTrue();
    }

    @Test
    void acceptRequestDateBeforeCurrentDate() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        mockMvc.perform(post("/accept-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"id\": 1, "
                                + "\"plannedDate\": \"2022-12-09\" "
                                + "}"))
                .andExpect(status().isBadRequest());

        assertThat(repo.existsById(1L)).isTrue();
    }

    @Test
    void acceptRequestSchedulerRejects() throws Exception {
        String name = "John";
        String description = "description";
        String faculty = "EEMCS";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2022, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.badRequest().build());

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);

        mockMvc.perform(post("/accept-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"id\": 1, "
                                + "\"plannedDate\": \"2022-12-23\" "
                                + "}"))
                .andExpect(status().isBadRequest());

        assertThat(repo.existsById(1L)).isTrue();
    }

    @Test
    void adminAuthorityWrongFacultyTest() throws Exception {
        Collection<SimpleGrantedAuthority> roleList2 = new ArrayList<>();
        roleList2.add(new SimpleGrantedAuthority("employee_EWI"));
        roleList2.add(new SimpleGrantedAuthority("admin_EWI"));
        Mockito.doReturn(roleList2).when(authManager).getRoles();
        String name = "John";
        String description = "description";
        String faculty = "NOTEWI";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2023, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);
        assertThat(repo.existsById(1L)).isTrue();
        mockMvc.perform(post("/accept-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"id\": 1, "
                                + "\"plannedDate\": \"2023-12-10\" "
                                + "}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminAuthorityAuthorizedTest() throws Exception {
        Collection<SimpleGrantedAuthority> roleList2 = new ArrayList<>();
        roleList2.add(new SimpleGrantedAuthority("employee_ewi"));
        roleList2.add(new SimpleGrantedAuthority("admin_ewi"));
        Mockito.doReturn(roleList2).when(authManager).getRoles();
        String name = "John";
        String description = "description";
        String faculty = "ewi";
        int cpu = 5;
        int gpu = 5;
        int ram = 5;
        Resources resources = new Resources(cpu, gpu, ram);
        LocalDate deadline = LocalDate.of(2023, 12, 12);

        LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 22, 15);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(currentDate.toInstant(ZoneOffset.UTC));
        when(schedulerService.scheduleRequest(any())).thenReturn(ResponseEntity.ok().build());

        Request request = new Request(name, description, faculty, resources, deadline, currentDate);
        repo.save(request);
        assertThat(repo.existsById(1L)).isTrue();
        mockMvc.perform(post("/accept-request").header("Authorization", "Bearer MockedToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"id\": 1, "
                                + "\"plannedDate\": \"2023-12-10\" "
                                + "}"))
                .andExpect(status().isOk());
        assertThat(repo.existsById(1L)).isFalse();
    }
}
