package appli.service;

import appli.model.JournalAction;
import appli.model.JournalAction.TypeAction;
import appli.model.User;
import appli.repository.JournalActionRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de journalisation conforme RGPD.
 * Gere login_log (connexions) et audit_log (actions CRUD).
 */
public class JournalService {

    private final JournalActionRepository journalRepository = new JournalActionRepository();

    // ========================================
    // LOGIN LOG : connexions / deconnexions
    // ========================================

    /**
     * Journalise une connexion reussie.
     */
    public void logConnexionReussie(User user) {
        JournalAction action = creerAction(user, TypeAction.CONNEXION,
                "Connexion reussie - " + user.getNomComplet() + " (" + user.getRole().getLibelle() + ")");
        action.setEntite("User");
        action.setEntiteId(user.getId());
        journalRepository.save(action);
    }

    /**
     * Journalise un echec de connexion (email inconnu, mot de passe incorrect, compte verrouille...).
     */
    public void logConnexionEchec(User user, String details) {
        JournalAction action = creerAction(user, TypeAction.ECHEC_CONNEXION,
                "Echec connexion - " + details);
        if (user != null) {
            action.setEntite("User");
            action.setEntiteId(user.getId());
        }
        journalRepository.save(action);
    }

    /**
     * Retro-compatibilite : journalise une connexion (succes ou echec).
     */
    public void logConnexion(User user, boolean success, String details) {
        if (success) {
            logConnexionReussie(user);
        } else {
            logConnexionEchec(user, details);
        }
    }

    // ========================================
    // AUDIT LOG : actions CRUD / metier
    // ========================================

    /**
     * Journalise une action simple (sans entite ciblee).
     */
    public void logAction(User user, TypeAction typeAction, String description) {
        logAction(user, typeAction, description, null, null);
    }

    /**
     * Journalise une action sur une entite specifique.
     */
    public void logAction(User user, TypeAction typeAction, String description,
                          String entite, Integer entiteId) {
        logAction(user, typeAction, description, entite, entiteId, null, null);
    }

    /**
     * Journalise une action avec snapshots avant/apres (RGPD : tracabilite des modifications).
     */
    public void logAction(User user, TypeAction typeAction, String description,
                          String entite, Integer entiteId,
                          String donneesAvant, String donneesApres) {
        JournalAction action = creerAction(user, typeAction, description);
        action.setEntite(entite);
        action.setEntiteId(entiteId);
        action.setDonneesAvant(donneesAvant);
        action.setDonneesApres(donneesApres);
        journalRepository.save(action);
    }

    // ========================================
    // CONSULTATION
    // ========================================

    public List<JournalAction> getAll() {
        return journalRepository.getAll();
    }

    public List<JournalAction> getByUserId(int userId) {
        return journalRepository.getByUserId(userId);
    }

    public List<JournalAction> getByTypeAction(TypeAction typeAction) {
        return journalRepository.getByTypeAction(typeAction);
    }

    public List<JournalAction> getByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return journalRepository.getByDateRange(debut, fin);
    }

    /**
     * Purge les logs plus anciens que le nombre de jours indique (RGPD : droit a l'effacement).
     */
    public void purgeOldLogs(int joursRetention) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursRetention);
        journalRepository.deleteOlderThan(dateLimite);
    }

    // ========================================
    // UTILITAIRES
    // ========================================

    private JournalAction creerAction(User user, TypeAction typeAction, String description) {
        JournalAction action = new JournalAction();
        action.setUserId(user != null ? user.getId() : null);
        action.setTypeAction(typeAction);
        action.setDescription(description);
        action.setAdresseIP(getLocalIP());
        action.setUserAgent("HSP-JavaFX/1.0");
        action.setDateAction(LocalDateTime.now());
        return action;
    }

    private String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
