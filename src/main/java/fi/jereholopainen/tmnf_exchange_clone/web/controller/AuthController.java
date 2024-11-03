package fi.jereholopainen.tmnf_exchange_clone.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.jereholopainen.tmnf_exchange_clone.model.AppUser;
import fi.jereholopainen.tmnf_exchange_clone.model.Role;
import fi.jereholopainen.tmnf_exchange_clone.service.RoleService;
import fi.jereholopainen.tmnf_exchange_clone.service.UserService;
import fi.jereholopainen.tmnf_exchange_clone.web.dto.RegistrationRequest;
import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;

    public AuthController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        AppUser newUser = new AppUser();
        newUser.setUsername(registrationRequest.getUsername()); // set username
        newUser.setPasswordHash(registrationRequest.getPassword()); // set password

        Role userRole = roleService.findByName("USER").orElseThrow(() -> new RuntimeException("Error: Role is not found.")); // get the user role and throw an exception if it is not found
        
        newUser.setRoles(Set.of(userRole)); // set the roles for the user
        userService.save(newUser); // save the user to the database
        return new ResponseEntity<>("User: '" + newUser.getUsername() + "' registered successfully", HttpStatus.CREATED); // return a 201 created response
    }
}
