package appli.dao;

import appli.model.Alerte;
import appli.model.Alerte.Niveau;
import appli.model.Alerte.TypeAlerte;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlerteDAO {

    public int insert(Alerte alerte) {
        String sql = "INSERT INTO alertes (type_alerte, niveau, titre, message, entite, entite_id, date_creation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, alerte.getTypeAlerte().name());
            stmt.setString(2, alerte.getNiveau().name());
            stmt.setString(3, alerte.getTitre());
            stmt.setString(4, alerte.getMessage());
            stmt.setString(5, alerte.getEntite());
            if (alerte.getEntiteId() != null) {
                stmt.setInt(6, alerte.getEntiteId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setTimestamp(7, Timestamp.valueOf(
                    alerte.getDateCreation() != null ? alerte.getDateCreation() : LocalDateTime.now()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'alerte : " + e.getMessage());
        }
        return -1;
    }

    public List<Alerte> findAll() {
        List<Alerte> alertes = new ArrayList<>();
        String sql = "SELECT * FROM alertes ORDER BY date_creation DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alertes.add(mapResultSetToAlerte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des alertes : " + e.getMessage());
        }
        return alertes;
    }

    public List<Alerte> findNonResolues() {
        List<Alerte> alertes = new ArrayList<>();
        String sql = "SELECT * FROM alertes WHERE date_resolution IS NULL ORDER BY " +
                "FIELD(niveau, 'CRITICAL', 'WARNING', 'INFO'), date_creation DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alertes.add(mapResultSetToAlerte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des alertes non resolues : " + e.getMessage());
        }
        return alertes;
    }

    public List<Alerte> findByType(TypeAlerte type) {
        List<Alerte> alertes = new ArrayList<>();
        String sql = "SELECT * FROM alertes WHERE type_alerte = ? ORDER BY date_creation DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                alertes.add(mapResultSetToAlerte(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des alertes par type : " + e.getMessage());
        }
        return alertes;
    }

    public int countNonResolues() {
        String sql = "SELECT COUNT(*) FROM alertes WHERE date_resolution IS NULL";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des alertes non resolues : " + e.getMessage());
        }
        return 0;
    }

    public int countByNiveau(Niveau niveau) {
        String sql = "SELECT COUNT(*) FROM alertes WHERE niveau = ? AND date_resolution IS NULL";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, niveau.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des alertes par niveau : " + e.getMessage());
        }
        return 0;
    }

    public boolean existsNonResolue(TypeAlerte type, String entite, int entiteId) {
        String sql = "SELECT COUNT(*) FROM alertes WHERE type_alerte = ? AND entite = ? AND entite_id = ? AND date_resolution IS NULL";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            stmt.setString(2, entite);
            stmt.setInt(3, entiteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la verification d'alerte existante : " + e.getMessage());
        }
        return false;
    }

    public boolean marquerCommeLue(int alerteId, int userId) {
        String sql = "UPDATE alertes SET date_lecture = ?, lu_par = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, userId);
            stmt.setInt(3, alerteId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du marquage de l'alerte comme lue : " + e.getMessage());
        }
        return false;
    }

    public boolean resoudre(int alerteId, int userId, String notes) {
        String sql = "UPDATE alertes SET date_resolution = ?, resolu_par = ?, notes_resolution = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, userId);
            stmt.setString(3, notes);
            stmt.setInt(4, alerteId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la resolution de l'alerte : " + e.getMessage());
        }
        return false;
    }

    private Alerte mapResultSetToAlerte(ResultSet rs) throws SQLException {
        Alerte alerte = new Alerte();
        alerte.setId(rs.getInt("id"));

        String typeStr = rs.getString("type_alerte");
        if (typeStr != null) {
            try {
                alerte.setTypeAlerte(TypeAlerte.valueOf(typeStr));
            } catch (IllegalArgumentException ignored) {}
        }

        String niveauStr = rs.getString("niveau");
        if (niveauStr != null) {
            try {
                alerte.setNiveau(Niveau.valueOf(niveauStr));
            } catch (IllegalArgumentException ignored) {}
        }

        alerte.setTitre(rs.getString("titre"));
        alerte.setMessage(rs.getString("message"));
        alerte.setEntite(rs.getString("entite"));

        int entiteId = rs.getInt("entite_id");
        if (!rs.wasNull()) {
            alerte.setEntiteId(entiteId);
        }

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            alerte.setDateCreation(dateCreation.toLocalDateTime());
        }

        Timestamp dateLecture = rs.getTimestamp("date_lecture");
        if (dateLecture != null) {
            alerte.setDateLecture(dateLecture.toLocalDateTime());
        }

        int luParId = rs.getInt("lu_par");
        if (!rs.wasNull()) {
            alerte.setLuParId(luParId);
        }

        Timestamp dateResolution = rs.getTimestamp("date_resolution");
        if (dateResolution != null) {
            alerte.setDateResolution(dateResolution.toLocalDateTime());
        }

        int resoluParId = rs.getInt("resolu_par");
        if (!rs.wasNull()) {
            alerte.setResoluParId(resoluParId);
        }

        alerte.setNotesResolution(rs.getString("notes_resolution"));

        return alerte;
    }
}
