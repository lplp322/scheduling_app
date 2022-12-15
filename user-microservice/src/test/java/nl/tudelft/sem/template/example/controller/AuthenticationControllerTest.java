package nl.tudelft.sem.template.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationInterface;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationRequestModel;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationResponseModel;
import nl.tudelft.sem.template.example.feigninterfaces.RegistrationRequestModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationInterface authenticationInterface;

    @Test
    public void testRegister() {
        try {
            RegistrationRequestModel registr = new RegistrationRequestModel();
            ResponseEntity val = new ResponseEntity<>("Here", HttpStatus.OK);
            ;
            when(authenticationInterface.register(any())).thenReturn(val);
            ObjectMapper objectMapper = new ObjectMapper();
            String serialisedRegister = objectMapper.writeValueAsString(registr);
            mockMvc.perform(post("/login/register").contentType(MediaType.APPLICATION_JSON)
                .content(serialisedRegister)).andExpect(status().isOk()).andExpect(content().string("Here"));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }

    @Test
    public void testAuthenticate() {
        try {
            AuthenticationRequestModel request = new AuthenticationRequestModel();
            AuthenticationResponseModel response = new AuthenticationResponseModel();
            response.setToken("123");
            ResponseEntity val = new ResponseEntity<AuthenticationResponseModel>(response, HttpStatus.OK);
            ;
            when(authenticationInterface.authenticate(any())).thenReturn(val);
            ObjectMapper objectMapper = new ObjectMapper();
            String serialisedRegister = objectMapper.writeValueAsString(request);
            mockMvc.perform(post("/login/authenticate").contentType(MediaType.APPLICATION_JSON)
                    .content(serialisedRegister)).andExpect(status().isOk()).andExpect(jsonPath("token").exists())
                .andExpect(jsonPath("token").value("123"));
        } catch (Exception e) {
            assertEquals(1, 0);
        }
    }
}
