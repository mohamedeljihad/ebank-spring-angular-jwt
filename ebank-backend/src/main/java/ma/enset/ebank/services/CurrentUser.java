package ma.enset.ebank.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Identifiant de l'utilisateur authentifié (sub du JWT), enregistré avec chaque client, compte et opération. */
public final class CurrentUser {
    private CurrentUser() { }

    public static String username() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null || !auth.isAuthenticated()) ? "system" : auth.getName();
    }
}
