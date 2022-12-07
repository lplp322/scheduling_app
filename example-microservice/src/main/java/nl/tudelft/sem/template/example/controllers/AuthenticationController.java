package nl.tudelft.sem.template.example.controllers;


import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationInterface;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationRequestModel;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationResponseModel;
import nl.tudelft.sem.template.example.feigninterfaces.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
public class AuthenticationController {
    @Autowired
    private AuthenticationInterface authMicroservice;

    /**
     * Instantiates a new controller.
     *
     *
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request) {
        return authMicroservice.register(request);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseModel> authenticate(@RequestBody AuthenticationRequestModel request){
        return authMicroservice.authenticate(request);
    }
}
