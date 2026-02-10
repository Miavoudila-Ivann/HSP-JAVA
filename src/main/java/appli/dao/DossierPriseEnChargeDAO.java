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

    public DossierPriseEnCharge findByNumeroDossier(String numeroDossier) {
        String sql = "SELECT * FROM dossiers_prise_en_charge WHERE numero_dossier = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroDossier);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDossier(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du dossier par numero : " + e.getMessage());
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

    public List<DossierPriseEnCharge> findByMedecinId(int medecinId) {
        List<DossierPriseEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossiers_prise_en_charge WHERE medecin_responsable_id = ? ORDER BY date_creation DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medecinId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dossiers.add(mapResultSetToDossier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des dossiers par medecin : " + e.getMessage());
        }
        return dossiers;
    }

    public int insert(DossierPriseEnCharge dossier) {
        String sql = "INSERT INTO dossiers_prise_en_charge (numero_dossier, patient_id, date_creation, date_admission, motif_admission, niveau_gravite, mode_arrivee, symptomes, constantes_vitales, antecedents, allergies, traitement_en_cours, statut, priorite_triage, medecin_responsable_id, cree_par, date_prise_en_charge, destination_sortie) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dossier.getNumeroDossier());
            stmt.setInt(2, dossier.getPatientId());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(4, dossier.getDateAdmission() != null ? Timestamp.valueOf(dossier.getDateAdmission()) : null);
            stmt.setString(5, dossier.getMotifAdmission());
            stmt.setString(6, dossier.getNiveauGravite().name());
            stmt.setString(7, dossier.getModeArrivee() != null ? dossier.getModeArrivee().name() : null);
            stmt.setString(8, dossier.getSymptomes());
            stmt.setString(9, dossier.getConstantesVitales());
            stmt.setString(10, dossier.getAntecedents());
            stmt.setString(11, dossier.getAllergies());
            stmt.setString(12, dossier.getTraitementEnCours());
            stmt.setString(13, dossier.getStatut().name());
            stmt.setInt(14, dossier.getPrioriteTriage());
            if (dossier.getMedecinResponsableId() != null) {
                stmt.setInt(15, dossier.getMedecinResponsableId());
            } else {
                stmt.setNull(15, Types.INTEGER);
            }
            if (dossier.getCreePar() != null) {
                stmt.setInt(16, dossier.getCreePar());
            } else {
                stmt.setNull(16, Types.INTEGER);
            }
            stmt.setTimestamp(17, dossier.getDatePriseEnCharge() != null ? Timestamp.valueOf(dossier.getDatePriseEnCharge()) : null);
            stmt.setString(18, dossier.getDestinationSortie() != null ? dossier.getDestinationSortie().name() : null);
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
        String sql = "UPDATE dossiers_prise_en_charge SET numero_dossier = ?, patient_id = ?, date_admission = ?, motif_admission = ?, niveau_gravite = ?, mode_arrivee = ?, symptomes = ?, constantes_vitales = ?, antecedents = ?, allergies = ?, traitement_en_cours = ?, statut = ?, priorite_triage = ?, medecin_responsable_id = ?, cree_par = ?, date_prise_en_charge = ?, date_cloture = ?, notes_cloture = ?, destination_sortie = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dossier.getNumeroDossier());
            stmt.setInt(2, dossier.getPatientId());
            stmt.setTimestamp(3, dossier.getDateAdmission() != null ? Timestamp.valueOf(dossier.getDateAdmission()) : null);
            stmt.setString(4, dossier.getMotifAdmission());
            stmt.setString(5, dossier.getNiveauGravite().name());
            stmt.setString(6, dossier.getModeArrivee() != null ? dossier.getModeArrivee().name() : null);
            stmt.setString(7, dossier.getSymptomes());
            stmt.setString(8, dossier.getConstantesVitales());
            stmt.setString(9, dossier.getAntecedents());
            stmt.setString(10, dossier.getAllergies());
            stmt.setString(11, dossier.getTraitementEnCours());
            stmt.setString(12, dossier.getStatut().name());
            stmt.setInt(13, dossier.getPrioriteTriage());
            if (dossier.getMedecinResponsableId() != null) {
                stmt.setInt(14, dossier.getMedecinResponsableId());
            } else {
                stmt.setNull(14, Types.INTEGER);
            }
            if (dossier.getCreePar() != null) {
                stmt.setInt(15, dossier.getCreePar());
            } else {
                stmt.setNull(15, Types.INTEGER);
            }
            stmt.setTimestamp(16, dossier.getDatePriseEnCharge() != null ? Timestamp.valueOf(dossier.getDatePriseEnCharge()) : null);
            stmt.setTimestamp(17, dossier.getDateCloture() != null ? Timestamp.valueOf(dossier.getDateCloture()) : null);
            stmt.setString(18, dossier.getNotesCloture());
            stmt.setString(19, dossier.getDestinationSortie() != null ? dossier.getDestinationSortie().name() : null);
            stmt.setInt(20, dossier.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du dossier : " + e.getMessage());
        }
        return false;
    }

    public boolean updateStatut(int id, DossierPriseEnCharge.Statut statut) {
        String sql = "UPDATE dossiers_prise_en_charge SET statut = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du statut du dossier : " + e.getMessage());
        }
        return false;
    }

    public List<DossierPriseEnCharge> findOpenCasesSorted() {
        List<DossierPriseEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossiers_prise_en_charge " +
                "WHERE statut IN ('EN_ATTENTE', 'EN_COURS', 'EN_OBSERVATION') " +
                "ORDER BY niveau_gravite DESC, priorite_triage ASC, date_creation ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dossiers.add(mapResultSetToDossier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des dossiers ouverts : " + e.getMessage());
        }
        return dossiers;
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
        dossier.setNumeroDossier(rs.getString("numero_dossier"));
        dossier.setPatientId(rs.getInt("patient_id"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            dossier.setDateCreation(dateCreation.toLocalDateTime());
        }
        Timestamp dateAdmission = rs.getTimestamp("date_admission");
        if (dateAdmission != null) {
            dossier.setDateAdmission(dateAdmission.toLocalDateTime());
        }
        dossier.setMotifAdmission(rs.getString("motif_admission"));
        String niveau = rs.getString("niveau_gravite");
        if (niveau != null) {
            dossier.setNiveauGravite(DossierPriseEnCharge.NiveauGravite.valueOf(niveau));
        }
        String modeArrivee = rs.getString("mode_arrivee");
        if (modeArrivee != null) {
            dossier.setModeArrivee(DossierPriseEnCharge.ModeArrivee.valueOf(modeArrivee));
        }
        dossier.setSymptomes(rs.getString("symptomes"));
        dossier.setConstantesVitales(rs.getString("constantes_vitales"));
        dossier.setAntecedents(rs.getString("antecedents"));
        dossier.setAllergies(rs.getString("allergies"));
        dossier.setTraitementEnCours(rs.getString("traitement_en_cours"));
        dossier.setStatut(DossierPriseEnCharge.Statut.valueOf(rs.getString("statut")));
        dossier.setPrioriteTriage(rs.getInt("priorite_triage"));
        int medecinId = rs.getInt("medecin_responsable_id");
        if (!rs.wasNull()) {
            dossier.setMedecinResponsableId(medecinId);
        }
        int creePar = rs.getInt("cree_par");
        if (!rs.wasNull()) {
            dossier.setCreePar(creePar);
        }
        Timestamp datePriseEnCharge = rs.getTimestamp("date_prise_en_charge");
        if (datePriseEnCharge != null) {
            dossier.setDatePriseEnCharge(datePriseEnCharge.toLocalDateTime());
        }
        Timestamp dateCloture = rs.getTimestamp("date_cloture");
        if (dateCloture != null) {
            dossier.setDateCloture(dateCloture.toLocalDateTime());
        }
        dossier.setNotesCloture(rs.getString("notes_cloture"));
        String destinationSortie = rs.getString("destination_sortie");
        if (destinationSortie != null) {
            dossier.setDestinationSortie(DossierPriseEnCharge.DestinationSortie.valueOf(destinationSortie));
        }
        return dossier;
    }
}
