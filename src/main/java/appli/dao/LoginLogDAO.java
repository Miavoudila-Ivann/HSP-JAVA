package appli.dao;

import appli.model.JournalAction;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoginLogDAO {

    public List<JournalAction> findAll() {
        List<JournalAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions WHERE type_action IN ('CONNEXION','DECONNEXION','ECHEC_CONNEXION') ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des logs de connexion : " + e.getMessage());
        }
        return logs;
    }

    public List<JournalAction> findByUserId(int userId) {
        List<JournalAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions WHERE type_action IN ('CONNEXION','DECONNEXION','ECHEC_CONNEXION') AND user_id = ? ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des logs de connexion par utilisateur : " + e.getMessage());
        }
        return logs;
    }

    public List<JournalAction> findByDateRange(LocalDateTime debut, LocalDateTime fin) {
        List<JournalAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions WHERE type_action IN ('CONNEXION','DECONNEXION','ECHEC_CONNEXION') AND date_action BETWEEN ? AND ? ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des logs de connexion par date : " + e.getMessage());
        }
        return logs;
    }

    public List<JournalAction> findEchecsConnexion() {
        List<JournalAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM journal_actions WHERE type_action = 'ECHEC_CONNEXION' ORDER BY date_action DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                logs.add(mapResultSetToJournalAction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des echecs de connexion : " + e.getMessage());
        }
        return logs;
    }

    public int insertConnexion(JournalAction log) {
        String sql = "INSERT INTO journal_actions (user_id, type_action, description, date_action, adresse_ip, user_agent) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null) {
                stmt.setInt(1, log.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, log.getTypeAction().name());
            stmt.setString(3, log.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getDateAction()));
            stmt.setString(5, log.getAdresseIP());
            stmt.setString(6, log.getUserAgent());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du log de connexion : " + e.getMessage());
        }
        return -1;
    }

    public int countEchecsRecents(int userId, int minutes) {
        String sql = "SELECT COUNT(*) FROM journal_actions WHERE user_id = ? AND type_action = 'ECHEC_CONNEXION' AND date_action > DATE_SUB(NOW(), INTERVAL ? MINUTE)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, minutes);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des echecs de connexion recents : " + e.getMessage());
        }
        return 0;
    }

    private JournalAction mapResultSetToJournalAction(ResultSet rs) throws SQLException {
        JournalAction action = new JournalAction();
        action.setId(rs.getInt("id"));
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            action.setUserId(userId);
        }
        action.setTypeAction(JournalAction.TypeAction.valueOf(rs.getString("type_action")));
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
        return action;
    }
}
