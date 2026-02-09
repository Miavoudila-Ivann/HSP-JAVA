package appli.dao;

import appli.model.DossierPriseEnCharge;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DossierPriseEnChargeDAO {

    public DossierPriseEnCharge findById(int id) {
        String sql = "SELECT * FROM dossiers_prise_en_charge WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDossier(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du dossier : " + e.getMessage());
        }
        return null;
    }

    public List<DossierPriseEnCharge> findByPatientId(int patientId) {
        List<DossierPriseEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossiers_prise_en_charge WHERE patient_id = ? ORDER BY date_creation DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dossiers.add(mapResultSetToDossier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des dossiers par patient : " + e.getMessage());
        }
        return dossiers;
    }

    public List<DossierPriseEnCharge> findAll() {
        List<DossierPriseEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossiers_prise_en_charge ORDER BY date_creation DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dossiers.add(mapResultSetToDossier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des dossiers : " + e.getMessage());
        }
        return dossiers;
    }

    public List<DossierPriseEnCharge> findByStatut(DossierPriseEnCharge.Statut statut) {
        List<DossierPriseEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossiers_prise_en_charge WHERE statut = ? ORDER BY niveau_gravite DESC, date_creation";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dossiers.add(mapResultSetToDossier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des dossiers par statut : " + e.getMessage());
        }
        return dossiers;
    }

    public List<DossierPriseEnCharge> findEnAttenteTriage() {
        List<DossierPriseEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossiers_prise_en_charge WHERE statut = 'EN_ATTENTE' ORDER BY niveau_gravite DESC, date_creation";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dossiers.add(mapResultSetToDossier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des dossiers en attente : " + e.getMessage());
        }
        return dossiers;
    }

    public int insert(DossierPriseEnCharge dossier) {
        String sql = "INSERT INTO dossiers_prise_en_charge (patient_id, date_creation, motif_admission, niveau_gravite, symptomes, antecedents, allergies, traitement_en_cours, statut, medecin_responsable_id, cree_par) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dossier.getPatientId());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, dossier.getMotifAdmission());
            stmt.setString(4, dossier.getNiveauGravite().name());
            stmt.setString(5, dossier.getSymptomes());
            stmt.setString(6, dossier.getAntecedents());
            stmt.setString(7, dossier.getAllergies());
            stmt.setString(8, dossier.getTraitementEnCours());
            stmt.setString(9, dossier.getStatut().name());
            if (dossier.getMedecinResponsableId() != null) {
                stmt.setInt(10, dossier.getMedecinResponsableId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }
            if (dossier.getCreePar() != null) {
                stmt.setInt(11, dossier.getCreePar());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du dossier : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(DossierPriseEnCharge dossier) {
        String sql = "UPDATE dossiers_prise_en_charge SET patient_id = ?, motif_admission = ?, niveau_gravite = ?, symptomes = ?, antecedents = ?, allergies = ?, traitement_en_cours = ?, statut = ?, medecin_responsable_id = ?, date_cloture = ?, notes_cloture = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dossier.getPatientId());
            stmt.setString(2, dossier.getMotifAdmission());
            stmt.setString(3, dossier.getNiveauGravite().name());
            stmt.setString(4, dossier.getSymptomes());
            stmt.setString(5, dossier.getAntecedents());
            stmt.setString(6, dossier.getAllergies());
            stmt.setString(7, dossier.getTraitementEnCours());
            stmt.setString(8, dossier.getStatut().name());
            if (dossier.getMedecinResponsableId() != null) {
                stmt.setInt(9, dossier.getMedecinResponsableId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setTimestamp(10, dossier.getDateCloture() != null ? Timestamp.valueOf(dossier.getDateCloture()) : null);
            stmt.setString(11, dossier.getNotesCloture());
            stmt.setInt(12, dossier.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du dossier : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM dossiers_prise_en_charge WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du dossier : " + e.getMessage());
        }
        return false;
    }

    private DossierPriseEnCharge mapResultSetToDossier(ResultSet rs) throws SQLException {
        DossierPriseEnCharge dossier = new DossierPriseEnCharge();
        dossier.setId(rs.getInt("id"));
        dossier.setPatientId(rs.getInt("patient_id"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            dossier.setDateCreation(dateCreation.toLocalDateTime());
        }
        dossier.setMotifAdmission(rs.getString("motif_admission"));
        String niveau = rs.getString("niveau_gravite");
        if (niveau != null) {
            dossier.setNiveauGravite(DossierPriseEnCharge.NiveauGravite.valueOf(niveau));
        }
        dossier.setSymptomes(rs.getString("symptomes"));
        dossier.setAntecedents(rs.getString("antecedents"));
        dossier.setAllergies(rs.getString("allergies"));
        dossier.setTraitementEnCours(rs.getString("traitement_en_cours"));
        dossier.setStatut(DossierPriseEnCharge.Statut.valueOf(rs.getString("statut")));
        int medecinId = rs.getInt("medecin_responsable_id");
        if (!rs.wasNull()) {
            dossier.setMedecinResponsableId(medecinId);
        }
        int creePar = rs.getInt("cree_par");
        if (!rs.wasNull()) {
            dossier.setCreePar(creePar);
        }
        Timestamp dateCloture = rs.getTimestamp("date_cloture");
        if (dateCloture != null) {
            dossier.setDateCloture(dateCloture.toLocalDateTime());
        }
        dossier.setNotesCloture(rs.getString("notes_cloture"));
        return dossier;
    }
}
