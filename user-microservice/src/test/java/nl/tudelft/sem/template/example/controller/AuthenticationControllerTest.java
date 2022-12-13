package nl.tudelft.sem.template.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationInterface;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationRequestModel;
import nl.tudelft.sem.template.example.feigninterfaces.RegistrationRequestModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mockAuthMicroservice"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationInterface authenticationInterface;

    @Test
    public void testRegister() throws Exception {
    //        RegistrationRequestModel registr = new RegistrationRequestModel();
    //        ResponseEntity val = new ResponseEntity<>("Here", HttpStatus.OK);;
    //        when(authenticationInterface.register(any())).thenReturn(val);
    //        ObjectMapper objectMapper = new ObjectMapper();
    //        String serialisedRegister = objectMapper.writeValueAsString(registr);
    //        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
    //            .content(serialisedRegister)).andExpect(status().isOk()).andExpect(content().string("Here"));
    }
}
