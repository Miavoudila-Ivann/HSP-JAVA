package appli.service;

import appli.model.JournalAction;
import appli.model.User;
import appli.repository.JournalActionRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

public class JournalService {

    private final JournalActionRepository journalRepository = new JournalActionRepository();

    public void logConnexion(User user, boolean success, String details) {
        JournalAction action = new JournalAction();
        action.setUserId(user != null ? user.getId() : null);
        action.setTypeAction(JournalAction.TypeAction.CONNEXION);
        action.setDescription((success ? "Connexion reussie" : "Echec connexion") + " - " + details);
        action.setAdresseIP(getLocalIP());
        action.setDateAction(LocalDateTime.now());

        journalRepository.save(action);
    }

    public void logAction(User user, JournalAction.TypeAction typeAction, String description) {
        logAction(user, typeAction, description, null, null);
    }

    public void logAction(User user, JournalAction.TypeAction typeAction, String description,
                          String entite, Integer entiteId) {
        JournalAction action = new JournalAction();
        action.setUserId(user != null ? user.getId() : null);
        action.setTypeAction(typeAction);
        action.setDescription(description);
        action.setAdresseIP(getLocalIP());
        action.setDateAction(LocalDateTime.now());
        action.setEntite(entite);
        action.setEntiteId(entiteId);

        journalRepository.save(action);
    }

    public List<JournalAction> getAll() {
        return journalRepository.getAll();
    }

    public List<JournalAction> getByUserId(int userId) {
        return journalRepository.getByUserId(userId);
    }

    public List<JournalAction> getByTypeAction(JournalAction.TypeAction typeAction) {
        return journalRepository.getByTypeAction(typeAction);
    }

    public List<JournalAction> getByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return journalRepository.getByDateRange(debut, fin);
    }

    public void purgeOldLogs(int joursRetention) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursRetention);
        journalRepository.deleteOlderThan(dateLimite);
    }

    private String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
