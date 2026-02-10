package appli.dao;

import appli.model.JournalAction;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JournalActionDAO {

    public JournalAction findById(int id) {
        String sql = "SELECT * FROM journal_actions WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToJournalAction(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'action : " + e.getMessage());
        }
        return null;
    }

    public List<JournalAction> findAll() {
        List<JournalAction> actions = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                actions.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des actions : " + e.getMessage());
        }
        return actions;
    }

    public List<JournalAction> findByUserId(int userId) {
        List<JournalAction> actions = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions WHERE user_id = ? ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                actions.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des actions par utilisateur : " + e.getMessage());
        }
        return actions;
    }

    public List<JournalAction> findByTypeAction(JournalAction.TypeAction typeAction) {
        List<JournalAction> actions = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions WHERE type_action = ? ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, typeAction.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                actions.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des actions par type : " + e.getMessage());
        }
        return actions;
    }

    public List<JournalAction> findByDateRange(LocalDateTime debut, LocalDateTime fin) {
        List<JournalAction> actions = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions WHERE date_action BETWEEN ? AND ? ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                actions.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des actions par date : " + e.getMessage());
        }
        return actions;
    }

    public List<JournalAction> findByEntite(String entite, Integer entiteId) {
        List<JournalAction> actions = new ArrayList<>();
        String sql;
        if (entiteId != null) {
            sql = "SELECT * FROM journal_actions WHERE entite = ? AND entite_id = ? ORDER BY date_action DESC";
        } else {
            sql = "SELECT * FROM journal_actions WHERE entite = ? ORDER BY date_action DESC";
        }
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entite);
            if (entiteId != null) {
                stmt.setInt(2, entiteId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                actions.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des actions par entite : " + e.getMessage());
        }
        return actions;
    }

    public int insert(JournalAction action) {
        String sql = "INSERT INTO journal_actions (user_id, type_action, description, date_action, adresse_ip, user_agent, " +
                "entite, entite_id, donnees_avant, donnees_apres) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (action.getUserId() != null) {
                stmt.setInt(1, action.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, action.getTypeAction().name());
            stmt.setString(3, action.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(action.getDateAction()));
            stmt.setString(5, action.getAdresseIP());
            stmt.setString(6, action.getUserAgent());
            stmt.setString(7, action.getEntite());
            if (action.getEntiteId() != null) {
                stmt.setInt(8, action.getEntiteId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.setString(9, action.getDonneesAvant());
            stmt.setString(10, action.getDonneesApres());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'action : " + e.getMessage());
        }
        return -1;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM journal_actions WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'action : " + e.getMessage());
        }
        return false;
    }

    public boolean deleteOlderThan(LocalDateTime date) {
        String sql = "DELETE FROM journal_actions WHERE date_action < ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(date));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des anciennes actions : " + e.getMessage());
        }
        return false;
    }

    private JournalAction mapResultSetToJournalAction(ResultSet rs) throws SQLException {
        JournalAction action = new JournalAction();
        action.setId(rs.getInt("id"));

        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            action.setUserId(userId);
        }

        String typeAction = rs.getString("type_action");
        if (typeAction != null) {
            action.setTypeAction(JournalAction.TypeAction.valueOf(typeAction));
        }

        action.setDescription(rs.getString("description"));

        Timestamp dateAction = rs.getTimestamp("date_action");
        if (dateAction != null) {
            action.setDateAction(dateAction.toLocalDateTime());
        }

        action.setAdresseIP(rs.getString("adresse_ip"));
        action.setUserAgent(rs.getString("user_agent"));
        action.setEntite(rs.getString("entite"));

        int entiteId = rs.getInt("entite_id");
        if (!rs.wasNull()) {
            action.setEntiteId(entiteId);
        }

        action.setDonneesAvant(rs.getString("donnees_avant"));
        action.setDonneesApres(rs.getString("donnees_apres"));

        return action;
    }
}
