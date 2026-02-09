package appli.repository;

import appli.dao.JournalActionDAO;
import appli.model.JournalAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class JournalActionRepository {

    private final JournalActionDAO journalActionDAO = new JournalActionDAO();

    public Optional<JournalAction> getById(int id) {
        return Optional.ofNullable(journalActionDAO.findById(id));
    }

    public List<JournalAction> getAll() {
        return journalActionDAO.findAll();
    }

    public List<JournalAction> getByUserId(int userId) {
        return journalActionDAO.findByUserId(userId);
    }

    public List<JournalAction> getByTypeAction(JournalAction.TypeAction typeAction) {
        return journalActionDAO.findByTypeAction(typeAction);
    }

    public List<JournalAction> getByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return journalActionDAO.findByDateRange(debut, fin);
    }

    public JournalAction save(JournalAction action) {
        int id = journalActionDAO.insert(action);
        action.setId(id);
        return action;
    }

    public void delete(int id) {
        journalActionDAO.delete(id);
    }

    public void deleteOlderThan(LocalDateTime date) {
        journalActionDAO.deleteOlderThan(date);
    }
}
