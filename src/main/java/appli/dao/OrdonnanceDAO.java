package appli.dao;

import appli.model.Ordonnance;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceDAO {

    public Ordonnance findById(int id) {
        String sql = "SELECT * FROM ordonnances WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrdonnance(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'ordonnance : " + e.getMessage());
        }
        return null;
    }

    public Ordonnance findByNumeroOrdonnance(String numeroOrdonnance) {
        String sql = "SELECT * FROM ordonnances WHERE numero_ordonnance = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroOrdonnance);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrdonnance(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'ordonnance par numero : " + e.getMessage());
        }
        return null;
    }

    public List<Ordonnance> findByDossierId(int dossierId) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnances WHERE dossier_id = ? ORDER BY date_prescription DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dossierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ordonnances par dossier : " + e.getMessage());
        }
        return ordonnances;
    }

    public List<Ordonnance> findByHospitalisationId(int hospitalisationId) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnances WHERE hospitalisation_id = ? ORDER BY date_prescription DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hospitalisationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ordonnances par hospitalisation : " + e.getMessage());
        }
        return ordonnances;
    }

    public List<Ordonnance> findByMedecinId(int medecinId) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnances WHERE medecin_id = ? ORDER BY date_prescription DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medecinId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ordonnances par medecin : " + e.getMessage());
        }
        return ordonnances;
    }

    public List<Ordonnance> findAll() {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnances ORDER BY date_prescription DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des ordonnances : " + e.getMessage());
        }
        return ordonnances;
    }

    public List<Ordonnance> findByStatut(Ordonnance.Statut statut) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnances WHERE statut = ? ORDER BY date_prescription DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ordonnances.add(mapResultSetToOrdonnance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ordonnances par statut : " + e.getMessage());
        }
        return ordonnances;
    }

    public int insert(Ordonnance ordonnance) {
        String sql = "INSERT INTO ordonnances (numero_ordonnance, dossier_id, hospitalisation_id, medecin_id, date_prescription, date_debut, date_fin, statut, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ordonnance.getNumeroOrdonnance());
            stmt.setInt(2, ordonnance.getDossierId());
            if (ordonnance.getHospitalisationId() != null) {
                stmt.setInt(3, ordonnance.getHospitalisationId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, ordonnance.getMedecinId());
            stmt.setTimestamp(5, Timestamp.valueOf(ordonnance.getDatePrescription()));
            stmt.setDate(6, ordonnance.getDateDebut() != null ? Date.valueOf(ordonnance.getDateDebut()) : null);
            stmt.setDate(7, ordonnance.getDateFin() != null ? Date.valueOf(ordonnance.getDateFin()) : null);
            stmt.setString(8, ordonnance.getStatut().name());
            stmt.setString(9, ordonnance.getNotes());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'ordonnance : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Ordonnance ordonnance) {
        String sql = "UPDATE ordonnances SET numero_ordonnance = ?, dossier_id = ?, hospitalisation_id = ?, medecin_id = ?, date_prescription = ?, date_debut = ?, date_fin = ?, statut = ?, notes = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ordonnance.getNumeroOrdonnance());
            stmt.setInt(2, ordonnance.getDossierId());
            if (ordonnance.getHospitalisationId() != null) {
                stmt.setInt(3, ordonnance.getHospitalisationId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, ordonnance.getMedecinId());
            stmt.setTimestamp(5, Timestamp.valueOf(ordonnance.getDatePrescription()));
            stmt.setDate(6, ordonnance.getDateDebut() != null ? Date.valueOf(ordonnance.getDateDebut()) : null);
            stmt.setDate(7, ordonnance.getDateFin() != null ? Date.valueOf(ordonnance.getDateFin()) : null);
            stmt.setString(8, ordonnance.getStatut().name());
            stmt.setString(9, ordonnance.getNotes());
            stmt.setInt(10, ordonnance.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de l'ordonnance : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM ordonnances WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'ordonnance : " + e.getMessage());
        }
        return false;
    }

    private Ordonnance mapResultSetToOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setId(rs.getInt("id"));
        ordonnance.setNumeroOrdonnance(rs.getString("numero_ordonnance"));
        ordonnance.setDossierId(rs.getInt("dossier_id"));
        int hospitalisationId = rs.getInt("hospitalisation_id");
        if (!rs.wasNull()) {
            ordonnance.setHospitalisationId(hospitalisationId);
        }
        ordonnance.setMedecinId(rs.getInt("medecin_id"));
        Timestamp datePrescription = rs.getTimestamp("date_prescription");
        if (datePrescription != null) {
            ordonnance.setDatePrescription(datePrescription.toLocalDateTime());
        }
        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) {
            ordonnance.setDateDebut(dateDebut.toLocalDate());
        }
        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            ordonnance.setDateFin(dateFin.toLocalDate());
        }
        String statut = rs.getString("statut");
        if (statut != null) {
            ordonnance.setStatut(Ordonnance.Statut.valueOf(statut));
        }
        ordonnance.setNotes(rs.getString("notes"));
        return ordonnance;
    }
}
