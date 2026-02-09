package appli.dao;

import appli.model.Hospitalisation;
import appli.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalisationDAO {

    public Hospitalisation findById(int id) {
        String sql = "SELECT * FROM hospitalisations WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToHospitalisation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'hospitalisation : " + e.getMessage());
        }
        return null;
    }

    public List<Hospitalisation> findByDossierId(int dossierId) {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisations WHERE dossier_id = ? ORDER BY date_entree DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dossierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hospitalisations.add(mapResultSetToHospitalisation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des hospitalisations par dossier : " + e.getMessage());
        }
        return hospitalisations;
    }

    public List<Hospitalisation> findAll() {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisations ORDER BY date_entree DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hospitalisations.add(mapResultSetToHospitalisation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des hospitalisations : " + e.getMessage());
        }
        return hospitalisations;
    }

    public List<Hospitalisation> findEnCours() {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisations WHERE statut = 'EN_COURS' ORDER BY date_entree";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hospitalisations.add(mapResultSetToHospitalisation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des hospitalisations en cours : " + e.getMessage());
        }
        return hospitalisations;
    }

    public List<Hospitalisation> findByMedecinId(int medecinId) {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisations WHERE medecin_id = ? ORDER BY date_entree DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medecinId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hospitalisations.add(mapResultSetToHospitalisation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des hospitalisations par medecin : " + e.getMessage());
        }
        return hospitalisations;
    }

    public int insert(Hospitalisation hospitalisation) {
        String sql = "INSERT INTO hospitalisations (dossier_id, chambre_id, date_entree, date_sortie_prevue, motif_hospitalisation, diagnostic, traitement, observations, statut, medecin_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, hospitalisation.getDossierId());
            stmt.setInt(2, hospitalisation.getChambreId());
            stmt.setTimestamp(3, Timestamp.valueOf(hospitalisation.getDateEntree()));
            stmt.setDate(4, hospitalisation.getDateSortiePrevue() != null ? Date.valueOf(hospitalisation.getDateSortiePrevue()) : null);
            stmt.setString(5, hospitalisation.getMotifHospitalisation());
            stmt.setString(6, hospitalisation.getDiagnostic());
            stmt.setString(7, hospitalisation.getTraitement());
            stmt.setString(8, hospitalisation.getObservations());
            stmt.setString(9, hospitalisation.getStatut().name());
            stmt.setInt(10, hospitalisation.getMedecinId());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'hospitalisation : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Hospitalisation hospitalisation) {
        String sql = "UPDATE hospitalisations SET dossier_id = ?, chambre_id = ?, date_entree = ?, date_sortie_prevue = ?, date_sortie_effective = ?, motif_hospitalisation = ?, diagnostic = ?, traitement = ?, observations = ?, statut = ?, medecin_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hospitalisation.getDossierId());
            stmt.setInt(2, hospitalisation.getChambreId());
            stmt.setTimestamp(3, Timestamp.valueOf(hospitalisation.getDateEntree()));
            stmt.setDate(4, hospitalisation.getDateSortiePrevue() != null ? Date.valueOf(hospitalisation.getDateSortiePrevue()) : null);
            stmt.setTimestamp(5, hospitalisation.getDateSortieEffective() != null ? Timestamp.valueOf(hospitalisation.getDateSortieEffective()) : null);
            stmt.setString(6, hospitalisation.getMotifHospitalisation());
            stmt.setString(7, hospitalisation.getDiagnostic());
            stmt.setString(8, hospitalisation.getTraitement());
            stmt.setString(9, hospitalisation.getObservations());
            stmt.setString(10, hospitalisation.getStatut().name());
            stmt.setInt(11, hospitalisation.getMedecinId());
            stmt.setInt(12, hospitalisation.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de l'hospitalisation : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM hospitalisations WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'hospitalisation : " + e.getMessage());
        }
        return false;
    }

    private Hospitalisation mapResultSetToHospitalisation(ResultSet rs) throws SQLException {
        Hospitalisation hospitalisation = new Hospitalisation();
        hospitalisation.setId(rs.getInt("id"));
        hospitalisation.setDossierId(rs.getInt("dossier_id"));
        hospitalisation.setChambreId(rs.getInt("chambre_id"));
        Timestamp dateEntree = rs.getTimestamp("date_entree");
        if (dateEntree != null) {
            hospitalisation.setDateEntree(dateEntree.toLocalDateTime());
        }
        Date dateSortiePrevue = rs.getDate("date_sortie_prevue");
        if (dateSortiePrevue != null) {
            hospitalisation.setDateSortiePrevue(dateSortiePrevue.toLocalDate());
        }
        Timestamp dateSortieEffective = rs.getTimestamp("date_sortie_effective");
        if (dateSortieEffective != null) {
            hospitalisation.setDateSortieEffective(dateSortieEffective.toLocalDateTime());
        }
        hospitalisation.setMotifHospitalisation(rs.getString("motif_hospitalisation"));
        hospitalisation.setDiagnostic(rs.getString("diagnostic"));
        hospitalisation.setTraitement(rs.getString("traitement"));
        hospitalisation.setObservations(rs.getString("observations"));
        hospitalisation.setStatut(Hospitalisation.Statut.valueOf(rs.getString("statut")));
        hospitalisation.setMedecinId(rs.getInt("medecin_id"));
        return hospitalisation;
    }
}
