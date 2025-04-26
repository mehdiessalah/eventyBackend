package backend.service;

import backend.model.Users;
import backend.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public Users createAppUser(Users appUser) {
        return appUserRepository.save(appUser);
    }

    @Transactional(readOnly = true)
    public Users getAppUser(UUID id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppUser not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Users getCurrentUser(Authentication authentication) {
        String userId = ((Jwt) authentication.getPrincipal()).getSubject();
        return getAppUser(UUID.fromString(userId));
    }

    @Transactional
    public Users updateAppUser(UUID id, Users updatedUser) {
        Users existing = getAppUser(id);
        existing.setName(updatedUser.getName());
        existing.setEmail(updatedUser.getEmail());
        existing.setPhone(updatedUser.getPhone());
        existing.setRole(updatedUser.getRole());
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