package appli.security;

import appli.model.User;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoleGuard {

    public enum Fonctionnalite {
        GESTION_UTILISATEURS,
        GESTION_PATIENTS,
        CONSULTATION_PATIENTS,
        GESTION_HOSPITALISATIONS,
        GESTION_DOSSIERS,
        TRIAGE,
        GESTION_STOCK,
        DEMANDE_PRODUITS,
        VALIDATION_DEMANDES,
        CONSULTATION_JOURNAL,
        EXPORT_DONNEES
    }

    private static final Map<User.Role, Set<Fonctionnalite>> PERMISSIONS = new HashMap<>();

    static {
        PERMISSIONS.put(User.Role.ADMIN, EnumSet.allOf(Fonctionnalite.class));

        PERMISSIONS.put(User.Role.SECRETAIRE, EnumSet.of(
                Fonctionnalite.GESTION_PATIENTS,
                Fonctionnalite.CONSULTATION_PATIENTS,
                Fonctionnalite.GESTION_DOSSIERS,
                Fonctionnalite.TRIAGE
        ));

        PERMISSIONS.put(User.Role.MEDECIN, EnumSet.of(
                Fonctionnalite.CONSULTATION_PATIENTS,
                Fonctionnalite.GESTION_HOSPITALISATIONS,
                Fonctionnalite.GESTION_DOSSIERS,
                Fonctionnalite.DEMANDE_PRODUITS
        ));

        PERMISSIONS.put(User.Role.GESTIONNAIRE, EnumSet.of(
                Fonctionnalite.GESTION_STOCK,
                Fonctionnalite.VALIDATION_DEMANDES
        ));
    }

    private RoleGuard() {}

    public static boolean hasPermission(User user, Fonctionnalite fonctionnalite) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        Set<Fonctionnalite> permissions = PERMISSIONS.get(user.getRole());
        return permissions != null && permissions.contains(fonctionnalite);
    }

    public static boolean hasPermission(Fonctionnalite fonctionnalite) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        return hasPermission(currentUser, fonctionnalite);
    }

    public static Set<Fonctionnalite> getPermissions(User.Role role) {
        return PERMISSIONS.getOrDefault(role, EnumSet.noneOf(Fonctionnalite.class));
    }

    public static boolean canAccessView(String viewName) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return "login".equals(viewName);
        }

        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }

        return switch (viewName) {
            case "login" -> true;
            case "dashboard" -> true;
            case "patients" -> hasPermission(Fonctionnalite.CONSULTATION_PATIENTS);
            case "hospitalisations" -> hasPermission(Fonctionnalite.GESTION_HOSPITALISATIONS);
            case "stock" -> hasPermission(Fonctionnalite.GESTION_STOCK);
            case "demandes" -> hasPermission(Fonctionnalite.DEMANDE_PRODUITS) || hasPermission(Fonctionnalite.VALIDATION_DEMANDES);
            case "utilisateurs" -> hasPermission(Fonctionnalite.GESTION_UTILISATEURS);
            case "journal" -> hasPermission(Fonctionnalite.CONSULTATION_JOURNAL);
            default -> false;
        };
    }
}
