package appli.dao;

import appli.model.Patient;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du patient : " + e.getMessage());
        }
        return null;
    }

    public Patient findByNumeroSecuriteSociale(String numeroSecu) {
        String sql = "SELECT * FROM patients WHERE numero_securite_sociale = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroSecu);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du patient par numero secu : " + e.getMessage());
        }
        return null;
    }

    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY nom, prenom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des patients : " + e.getMessage());
        }
        return patients;
    }

    public List<Patient> search(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE nom LIKE ? OR prenom LIKE ? OR numero_securite_sociale LIKE ? ORDER BY nom, prenom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de patients : " + e.getMessage());
        }
        return patients;
    }

    public int insert(Patient patient) {
        String sql = "INSERT INTO patients (numero_securite_sociale, nom, prenom, date_naissance, sexe, adresse, telephone, email, personne_contact_nom, personne_contact_telephone, date_creation, cree_par) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, patient.getNumeroSecuriteSociale());
            stmt.setString(2, patient.getNom());
            stmt.setString(3, patient.getPrenom());
            stmt.setDate(4, patient.getDateNaissance() != null ? Date.valueOf(patient.getDateNaissance()) : null);
            stmt.setString(5, patient.getSexe() != null ? patient.getSexe().name() : null);
            stmt.setString(6, patient.getAdresse());
            stmt.setString(7, patient.getTelephone());
            stmt.setString(8, patient.getEmail());
            stmt.setString(9, patient.getPersonneContactNom());
            stmt.setString(10, patient.getPersonneContactTelephone());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            if (patient.getCreePar() != null) {
                stmt.setInt(12, patient.getCreePar());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du patient : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Patient patient) {
        String sql = "UPDATE patients SET numero_securite_sociale = ?, nom = ?, prenom = ?, date_naissance = ?, sexe = ?, adresse = ?, telephone = ?, email = ?, personne_contact_nom = ?, personne_contact_telephone = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getNumeroSecuriteSociale());
            stmt.setString(2, patient.getNom());
            stmt.setString(3, patient.getPrenom());
            stmt.setDate(4, patient.getDateNaissance() != null ? Date.valueOf(patient.getDateNaissance()) : null);
            stmt.setString(5, patient.getSexe() != null ? patient.getSexe().name() : null);
            stmt.setString(6, patient.getAdresse());
            stmt.setString(7, patient.getTelephone());
            stmt.setString(8, patient.getEmail());
            stmt.setString(9, patient.getPersonneContactNom());
            stmt.setString(10, patient.getPersonneContactTelephone());
            stmt.setInt(11, patient.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du patient : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du patient : " + e.getMessage());
        }
        return false;
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setNumeroSecuriteSociale(rs.getString("numero_securite_sociale"));
        patient.setNom(rs.getString("nom"));
        patient.setPrenom(rs.getString("prenom"));
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            patient.setDateNaissance(dateNaissance.toLocalDate());
        }
        String sexe = rs.getString("sexe");
        if (sexe != null) {
            patient.setSexe(Patient.Sexe.valueOf(sexe));
        }
        patient.setAdresse(rs.getString("adresse"));
        patient.setTelephone(rs.getString("telephone"));
        patient.setEmail(rs.getString("email"));
        patient.setPersonneContactNom(rs.getString("personne_contact_nom"));
        patient.setPersonneContactTelephone(rs.getString("personne_contact_telephone"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            patient.setDateCreation(dateCreation.toLocalDateTime());
        }
        int creePar = rs.getInt("cree_par");
        if (!rs.wasNull()) {
            patient.setCreePar(creePar);
        }
        return patient;
    }
}
