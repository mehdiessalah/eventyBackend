package backend.controller;

import backend.model.Users;
import backend.service.AppUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class AppUserController {
    private final AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<AuthResponse> createAppUser(@Valid @RequestBody RegisterRequest registerRequest) {
        Users created = appUserService.createAppUser(registerRequest);
        return new ResponseEntity<>(new AuthResponse(created.getAuthToken(), created), HttpStatus.CREATED);
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        Users user = appUserService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(user.getAuthToken(), user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getAppUser(@PathVariable UUID id) {
        Users user = appUserService.getAppUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
        Users user = appUserService.getCurrentUser(authentication);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateAppUser(@PathVariable UUID id, @Valid @RequestBody Users appUser) {
        Users updated = appUserService.updateAppUser(id, appUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable UUID id) {
        appUserService.deleteAppUser(id);
        return ResponseEntity.noContent().build();
    }

    public static class LoginRequest {
        @NotNull
        @Email
        private String email;

        @NotNull
        @Size(min = 8, max = 255)
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        @NotNull
        @Email
        private String email;

        @NotNull
        @Size(min = 8, max = 255)
        private String password;

        @NotNull
        @Size(min = 1, max = 255)
        private String firstName;

        @NotNull
        @Size(min = 1, max = 255)
        private String lastName;

        @NotNull
        @Size(min = 1, max = 50)
        private String role;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class AuthResponse {
        private String token;
        private Users user;

        public AuthResponse(String token, Users user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Users getUser() {
            return user;
        }

        public void setUser(Users user) {
            this.user = user;
        }
    }
}