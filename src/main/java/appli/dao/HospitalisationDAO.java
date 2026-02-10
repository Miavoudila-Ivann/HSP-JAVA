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

    public Hospitalisation findByNumeroSejour(String numeroSejour) {
        String sql = "SELECT * FROM hospitalisations WHERE numero_sejour = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroSejour);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToHospitalisation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'hospitalisation par numero sejour : " + e.getMessage());
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

    public List<Hospitalisation> findByChambreId(int chambreId) {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisations WHERE chambre_id = ? ORDER BY date_entree DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chambreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hospitalisations.add(mapResultSetToHospitalisation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des hospitalisations par chambre : " + e.getMessage());
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
        String sql = "INSERT INTO hospitalisations (numero_sejour, dossier_id, chambre_id, lit_numero, date_entree, date_sortie_prevue, date_sortie_effective, motif_hospitalisation, diagnostic_entree, diagnostic_sortie, traitement, observations, evolution, statut, type_sortie, medecin_id, medecin_sortie_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, hospitalisation.getNumeroSejour());
            stmt.setInt(2, hospitalisation.getDossierId());
            stmt.setInt(3, hospitalisation.getChambreId());
            if (hospitalisation.getLitNumero() != null) {
                stmt.setInt(4, hospitalisation.getLitNumero());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setTimestamp(5, Timestamp.valueOf(hospitalisation.getDateEntree()));
            stmt.setDate(6, hospitalisation.getDateSortiePrevue() != null ? Date.valueOf(hospitalisation.getDateSortiePrevue()) : null);
            stmt.setTimestamp(7, hospitalisation.getDateSortieEffective() != null ? Timestamp.valueOf(hospitalisation.getDateSortieEffective()) : null);
            stmt.setString(8, hospitalisation.getMotifHospitalisation());
            stmt.setString(9, hospitalisation.getDiagnosticEntree());
            stmt.setString(10, hospitalisation.getDiagnosticSortie());
            stmt.setString(11, hospitalisation.getTraitement());
            stmt.setString(12, hospitalisation.getObservations());
            stmt.setString(13, hospitalisation.getEvolution());
            stmt.setString(14, hospitalisation.getStatut().name());
            stmt.setString(15, hospitalisation.getTypeSortie() != null ? hospitalisation.getTypeSortie().name() : null);
            stmt.setInt(16, hospitalisation.getMedecinId());
            if (hospitalisation.getMedecinSortieId() != null) {
                stmt.setInt(17, hospitalisation.getMedecinSortieId());
            } else {
                stmt.setNull(17, Types.INTEGER);
            }
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
        String sql = "UPDATE hospitalisations SET numero_sejour = ?, dossier_id = ?, chambre_id = ?, lit_numero = ?, date_entree = ?, date_sortie_prevue = ?, date_sortie_effective = ?, motif_hospitalisation = ?, diagnostic_entree = ?, diagnostic_sortie = ?, traitement = ?, observations = ?, evolution = ?, statut = ?, type_sortie = ?, medecin_id = ?, medecin_sortie_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hospitalisation.getNumeroSejour());
            stmt.setInt(2, hospitalisation.getDossierId());
            stmt.setInt(3, hospitalisation.getChambreId());
            if (hospitalisation.getLitNumero() != null) {
                stmt.setInt(4, hospitalisation.getLitNumero());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setTimestamp(5, Timestamp.valueOf(hospitalisation.getDateEntree()));
            stmt.setDate(6, hospitalisation.getDateSortiePrevue() != null ? Date.valueOf(hospitalisation.getDateSortiePrevue()) : null);
            stmt.setTimestamp(7, hospitalisation.getDateSortieEffective() != null ? Timestamp.valueOf(hospitalisation.getDateSortieEffective()) : null);
            stmt.setString(8, hospitalisation.getMotifHospitalisation());
            stmt.setString(9, hospitalisation.getDiagnosticEntree());
            stmt.setString(10, hospitalisation.getDiagnosticSortie());
            stmt.setString(11, hospitalisation.getTraitement());
            stmt.setString(12, hospitalisation.getObservations());
            stmt.setString(13, hospitalisation.getEvolution());
            stmt.setString(14, hospitalisation.getStatut().name());
            stmt.setString(15, hospitalisation.getTypeSortie() != null ? hospitalisation.getTypeSortie().name() : null);
            stmt.setInt(16, hospitalisation.getMedecinId());
            if (hospitalisation.getMedecinSortieId() != null) {
                stmt.setInt(17, hospitalisation.getMedecinSortieId());
            } else {
                stmt.setNull(17, Types.INTEGER);
            }
            stmt.setInt(18, hospitalisation.getId());
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
        hospitalisation.setNumeroSejour(rs.getString("numero_sejour"));
        hospitalisation.setDossierId(rs.getInt("dossier_id"));
        hospitalisation.setChambreId(rs.getInt("chambre_id"));
        int litNumero = rs.getInt("lit_numero");
        if (!rs.wasNull()) {
            hospitalisation.setLitNumero(litNumero);
        }
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
        hospitalisation.setDiagnosticEntree(rs.getString("diagnostic_entree"));
        hospitalisation.setDiagnosticSortie(rs.getString("diagnostic_sortie"));
        hospitalisation.setTraitement(rs.getString("traitement"));
        hospitalisation.setObservations(rs.getString("observations"));
        hospitalisation.setEvolution(rs.getString("evolution"));
        hospitalisation.setStatut(Hospitalisation.Statut.valueOf(rs.getString("statut")));
        String typeSortie = rs.getString("type_sortie");
        if (typeSortie != null) {
            hospitalisation.setTypeSortie(Hospitalisation.TypeSortie.valueOf(typeSortie));
        }
        hospitalisation.setMedecinId(rs.getInt("medecin_id"));
        int medecinSortieId = rs.getInt("medecin_sortie_id");
        if (!rs.wasNull()) {
            hospitalisation.setMedecinSortieId(medecinSortieId);
        }
        return hospitalisation;
    }
}
