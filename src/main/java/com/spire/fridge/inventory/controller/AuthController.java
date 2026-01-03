package com.spire.fridge.inventory.controller;

import com.spire.fridge.inventory.entity.ERole;
import com.spire.fridge.inventory.entity.Role;
import com.spire.fridge.inventory.entity.User;
import com.spire.fridge.inventory.dto.payload.request.LoginRequest;
import com.spire.fridge.inventory.dto.payload.request.SignupRequest;
import com.spire.fridge.inventory.dto.payload.response.JwtResponse;
import com.spire.fridge.inventory.dto.payload.response.MessageResponse;
import com.spire.fridge.inventory.repository.RoleRepository;
import com.spire.fridge.inventory.repository.UserRepository;
import com.spire.fridge.inventory.security.jwt.JwtUtils;
import com.spire.fridge.inventory.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@CrossOrigin(origins = "https://food-manager.jp", maxAge = 3600, allowCredentials = "true")
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8080"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthenticationManager authenticationManager;

    UserRepository userRepository;

    RoleRepository roleRepository;

    PasswordEncoder encoder;

    JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtUtils.generateToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            roles
    ));
}
//    @RequestMapping(value = {"/signup", "/signin"}, method = RequestMethod.OPTIONS)
//    public ResponseEntity<?> handleOptions() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Access-Control-Allow-Origin", "http://localhost:3000");
//        headers.add("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
//        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//
//        return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername()))
        {
            return ResponseEntity.badRequest().body(new MessageResponse("既に同じユーザー名で登録されています。"));
        }

        logger.info("User registration started for username: {}", signupRequest.getUsername());


        User user = new User(signupRequest.getUsername(), encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).
            orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            roles.add(userRole);

        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found")
                        );
                        roles.add(adminRole);
                        break;

                    case "user":
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found."));

                        roles.add(userRole);
                        break;
                }
            });
        }


        user.setRoles(roles);
        userRepository.save(user);

        logger.info("User registration completed for username: {}", signupRequest.getUsername());

        return ResponseEntity.ok(new MessageResponse("正常に登録されました!"));

}
}
