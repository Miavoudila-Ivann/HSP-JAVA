package appli.security;

import appli.model.User;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Controleur d'acces base sur les roles (RBAC).
 * Definit les permissions par role via la {@link Fonctionnalite} enum
 * et expose des methodes statiques pour verifier les droits de l'utilisateur courant.
 *
 * <p>Utilisation typique :
 * <pre>
 *   if (RoleGuard.hasPermission(Fonctionnalite.GESTION_PATIENTS)) { ... }
 * </pre>
 */
public class RoleGuard {

    /**
     * Enumeration de toutes les fonctionnalites protegees de l'application.
     * Chaque role se voit attribuer un sous-ensemble de ces fonctionnalites.
     */
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
        EXPORT_DONNEES,
        CONSULTATION_STATISTIQUES,
        GESTION_RENDEZ_VOUS,
        GESTION_COMMANDES,
        GESTION_ORDONNANCES,
        GESTION_FOURNISSEURS,
        GESTION_CHAMBRES,
        CONSULTATION_LOGIN_LOG
    }

    /**
     * Table de correspondance role → ensemble des fonctionnalites autorisees.
     * Initialisee une seule fois au chargement de la classe.
     */
    private static final Map<User.Role, Set<Fonctionnalite>> PERMISSIONS = new HashMap<>();

    static {
        // L'admin a acces a toutes les fonctionnalites
        PERMISSIONS.put(User.Role.ADMIN, EnumSet.allOf(Fonctionnalite.class));

        // La secretaire gere les patients, le triage et les rendez-vous
        PERMISSIONS.put(User.Role.SECRETAIRE, EnumSet.of(
                Fonctionnalite.GESTION_PATIENTS,
                Fonctionnalite.CONSULTATION_PATIENTS,
                Fonctionnalite.GESTION_DOSSIERS,
                Fonctionnalite.TRIAGE,
                Fonctionnalite.CONSULTATION_STATISTIQUES,
                Fonctionnalite.GESTION_RENDEZ_VOUS
        ));

        // Le medecin traite les dossiers, prescrit et hospitalise
        PERMISSIONS.put(User.Role.MEDECIN, EnumSet.of(
                Fonctionnalite.CONSULTATION_PATIENTS,
                Fonctionnalite.GESTION_HOSPITALISATIONS,
                Fonctionnalite.GESTION_DOSSIERS,
                Fonctionnalite.DEMANDE_PRODUITS,
                Fonctionnalite.CONSULTATION_STATISTIQUES,
                Fonctionnalite.EXPORT_DONNEES,
                Fonctionnalite.GESTION_RENDEZ_VOUS,
                Fonctionnalite.GESTION_ORDONNANCES
        ));

        // Le gestionnaire administre le stock, les commandes et les fournisseurs
        PERMISSIONS.put(User.Role.GESTIONNAIRE, EnumSet.of(
                Fonctionnalite.GESTION_STOCK,
                Fonctionnalite.VALIDATION_DEMANDES,
                Fonctionnalite.CONSULTATION_STATISTIQUES,
                Fonctionnalite.EXPORT_DONNEES,
                Fonctionnalite.GESTION_COMMANDES,
                Fonctionnalite.GESTION_FOURNISSEURS
        ));
    }

    private RoleGuard() {}

    /**
     * Verifie si un utilisateur donne a la permission pour une fonctionnalite.
     *
     * @param user            l'utilisateur a verifier
     * @param fonctionnalite  la fonctionnalite demandee
     * @return {@code true} si l'utilisateur a la permission
     */
    public static boolean hasPermission(User user, Fonctionnalite fonctionnalite) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        Set<Fonctionnalite> permissions = PERMISSIONS.get(user.getRole());
        return permissions != null && permissions.contains(fonctionnalite);
    }

    /**
     * Verifie si l'utilisateur de la session courante a la permission pour une fonctionnalite.
     *
     * @param fonctionnalite la fonctionnalite demandee
     * @return {@code true} si l'utilisateur courant a la permission
     */
    public static boolean hasPermission(Fonctionnalite fonctionnalite) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        return hasPermission(currentUser, fonctionnalite);
    }

    /**
     * Retourne l'ensemble des fonctionnalites associees a un role.
     *
     * @param role le role dont on veut les permissions
     * @return ensemble des fonctionnalites (vide si le role est inconnu)
     */
    public static Set<Fonctionnalite> getPermissions(User.Role role) {
        return PERMISSIONS.getOrDefault(role, EnumSet.noneOf(Fonctionnalite.class));
    }

    /**
     * Verifie si l'utilisateur courant peut acceder a une vue donnee (par son nom).
     * Appele par le {@link appli.util.Router} avant chaque navigation.
     *
     * @param viewName le nom de la vue (ex: "patients", "stock")
     * @return {@code true} si l'acces est autorise
     */
    public static boolean canAccessView(String viewName) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return "login".equals(viewName) || "inscription".equals(viewName) || "totp_verify".equals(viewName);
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
            case "triage"  -> hasPermission(Fonctionnalite.TRIAGE);
            case "dossier" -> hasPermission(Fonctionnalite.GESTION_HOSPITALISATIONS)
                           || hasPermission(Fonctionnalite.GESTION_DOSSIERS);
            case "statistiques" -> hasPermission(Fonctionnalite.CONSULTATION_STATISTIQUES);
            case "rendezvous" -> hasPermission(Fonctionnalite.GESTION_RENDEZ_VOUS);
            case "commandes" -> hasPermission(Fonctionnalite.GESTION_COMMANDES);
            case "ordonnances" -> hasPermission(Fonctionnalite.GESTION_ORDONNANCES);
            case "fournisseurs" -> hasPermission(Fonctionnalite.GESTION_FOURNISSEURS);
            case "chambres" -> hasPermission(Fonctionnalite.GESTION_CHAMBRES);
            case "loginlog" -> hasPermission(Fonctionnalite.CONSULTATION_LOGIN_LOG);
            case "totp_verify", "totp_setup" -> true;
            default -> false;
        };
    }
}
