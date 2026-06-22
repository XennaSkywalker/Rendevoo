package com.example.user_service;

import com.example.util.TokenDTO;
import com.example.util.TokenRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("auth/login")
    public ResponseEntity<TokenDTO> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.authenticateUser(loginDTO);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponseDTO> signupUser(@Valid @RequestBody SignupDTO signupDTO) {
        return userService.signupUser(signupDTO);
    }

    @PostMapping("/auth/verify")
    public String verify(@RequestBody TokenRequest token) {
        return userService.verifyToken(token.getToken());
    }
}
