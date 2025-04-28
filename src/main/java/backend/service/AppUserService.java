package backend.service;

import backend.controller.AppUserController;
import backend.model.Users;
import backend.repository.AppUserRepository;
import backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Users createAppUser(AppUserController.RegisterRequest registerRequest) {
        Users appUser = new Users();
        appUser.setEmail(registerRequest.getEmail());
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setFirstName(registerRequest.getFirstName());
        appUser.setLastName(registerRequest.getLastName());
        appUser.setRole(registerRequest.getRole());
        String token = jwtUtil.generateToken(appUser.getEmail(), appUser.getRole());
        appUser.setAuthToken(token);
        return appUserRepository.save(appUser);
    }

    @Transactional(readOnly = true)
    public Users getAppUser(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppUser not found with id: " + id));
    }

    @Transactional
    public Users authenticate(String email, String password) {
        Users user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
            user.setAuthToken(token);
            return appUserRepository.save(user);
        }
        throw new RuntimeException("Invalid password");
    }

    @Transactional(readOnly = true)
    public Users getCurrentUser(Authentication authentication) {
        String userId = ((Jwt) authentication.getPrincipal()).getSubject();
        return getAppUser(UUID.fromString(userId));
    }

    @Transactional
    public Users updateAppUser(UUID id, Users updatedUser) {
        Users existing = getAppUser(id);
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setEmail(updatedUser.getEmail());
        existing.setRole(updatedUser.getRole());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return appUserRepository.save(existing);
    }

    @Transactional
    public void deleteAppUser(UUID id) {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("AppUser not found with id: " + id);
        }
        appUserRepository.deleteById(id);
    }
}