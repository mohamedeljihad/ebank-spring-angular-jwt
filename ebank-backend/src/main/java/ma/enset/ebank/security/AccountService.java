package ma.enset.ebank.security;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Gestion des comptes utilisateurs et de leurs mots de passe. */
@Service
@Transactional
@AllArgsConstructor
public class AccountService {
    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser addNewUser(String username, String password, String email, String confirmPassword) {
        if (appUserRepository.existsById(username)) throw new IllegalArgumentException("Cet utilisateur existe déjà");
        if (!password.equals(confirmPassword)) throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        AppUser user = AppUser.builder().username(username).password(passwordEncoder.encode(password)).email(email).build();
        return appUserRepository.save(user);
    }

    public AppRole addNewRole(String role) {
        return appRoleRepository.findById(role).orElseGet(() -> appRoleRepository.save(new AppRole(role)));
    }

    public void addRoleToUser(String username, String role) {
        AppUser user = appUserRepository.findById(username).orElseThrow();
        AppRole appRole = appRoleRepository.findById(role).orElseThrow();
        if (!user.getRoles().contains(appRole)) user.getRoles().add(appRole);
    }

    public void removeRoleFromUser(String username, String role) {
        AppUser user = appUserRepository.findById(username).orElseThrow();
        user.getRoles().removeIf(r -> r.getRole().equals(role));
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        AppUser user = appUserRepository.findById(username).orElseThrow();
        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        if (newPassword == null || newPassword.length() < 4)
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 4 caractères");
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public List<AppUser> listUsers() {
        return appUserRepository.findAll();
    }
}
