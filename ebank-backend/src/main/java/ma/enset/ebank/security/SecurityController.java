package ma.enset.ebank.security;

import lombok.AllArgsConstructor;
import ma.enset.ebank.dtos.ChangePasswordDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final AccountService accountService;
    private final long expirationMinutes;

    public SecurityController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder,
                              AccountService accountService,
                              @Value("${jwt.expiration-minutes:30}") long expirationMinutes) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.accountService = accountService;
        this.expirationMinutes = expirationMinutes;
    }

    /** Authentifie l'utilisateur et renvoie un access token JWT (claim "scope" = rôles). */
    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).map(a -> a.replace("ROLE_", ""))
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(username)
                .claim("scope", scope)
                .build();
        JwtEncoderParameters params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
        return Map.of("access-token", jwtEncoder.encode(params).getTokenValue());
    }

    @GetMapping("/profile")
    public Authentication profile(Authentication authentication) {
        return authentication;
    }

    /** Un utilisateur authentifié change son propre mot de passe. */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordDTO dto, Authentication authentication) {
        accountService.changePassword(authentication.getName(), dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié"));
    }

    // -------- administration des utilisateurs (ADMIN)
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<AppUser> users() {
        return accountService.listUsers();
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public AppUser addUser(@RequestParam String username, @RequestParam String password,
                           @RequestParam(required = false) String email, @RequestParam(defaultValue = "USER") String role) {
        AppUser user = accountService.addNewUser(username, password, email, password);
        accountService.addRoleToUser(username, role);
        return user;
    }
}
