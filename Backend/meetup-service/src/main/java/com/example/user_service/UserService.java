package com.example.user_service;

import com.example.exceptions.EmailExistsException;
import com.example.exceptions.InvalidCredentialsException;
import com.example.exceptions.InvalidPasswordException;
import com.example.exceptions.UsernameExistsException;
import com.example.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil = new JwtUtil();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //repo methods
    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public ResponseEntity<String> authenticateUser(LoginDTO loginDTO) {
        //make sure user exists
        UserEntity user = loadUserByUsername(loginDTO.getUsername());

        //make sure password is correct
        checkPassword(loginDTO, user);

        //return JWT token
        return ResponseEntity.status(HttpStatus.OK).body(jwtUtil.createToken(user.getId()));
    }

    private void checkPassword(LoginDTO loginDTO, UserEntity user) {
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash()))
            throw new InvalidPasswordException();
    }

    public ResponseEntity<UserResponseDTO> signupUser(SignupDTO signupDTO) {
        //first make sure email and username field or non empty
        //done using jakarta @NotNull and @Valid -> handles request body validation
        //jakarta.validation.ConstraintViolationException this is for null related issues at service and repository level

        //then make sure email and username dont already exist
        if (existsByUsername(signupDTO.getUsername()))
            throw new UsernameExistsException(signupDTO.getUsername());

        if (existsByEmail(signupDTO.getEmail()))
            throw new EmailExistsException(signupDTO.getUsername());

        //if both dont already exist, create new user
        UserEntity user = new UserEntity(signupDTO.getUsername(), signupDTO.getEmail(), passwordEncoder.encode(signupDTO.getPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user.getUsername(), user.getEmail()));
    }

    public String verifyToken(String token) {
        return jwtUtil.verifyToken(token);
    }
}
