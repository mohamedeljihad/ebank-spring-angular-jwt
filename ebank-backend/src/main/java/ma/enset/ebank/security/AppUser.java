package ma.enset.ebank.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/** Utilisateur de l'application (stocké en base, mot de passe encodé BCrypt). */
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {
    @Id
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    private List<AppRole> roles = new ArrayList<>();
}
