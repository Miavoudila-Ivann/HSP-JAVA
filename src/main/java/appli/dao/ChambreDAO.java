package appli.dao;

import appli.model.Chambre;
import appli.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChambreDAO {

    public Chambre findById(int id) {
        String sql = "SELECT * FROM chambres WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToChambre(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la chambre : " + e.getMessage());
        }
        return null;
    }

    public Chambre findByNumero(String numero) {
        String sql = "SELECT * FROM chambres WHERE numero = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToChambre(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la chambre par numero : " + e.getMessage());
        }
        return null;
    }

    public List<Chambre> findAll() {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambres ORDER BY etage, numero";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                chambres.add(mapResultSetToChambre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des chambres : " + e.getMessage());
        }
        return chambres;
    }

    public List<Chambre> findDisponibles() {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambres WHERE actif = true AND nb_lits_occupes < capacite AND en_maintenance = false ORDER BY etage, numero";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                chambres.add(mapResultSetToChambre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des chambres disponibles : " + e.getMessage());
        }
        return chambres;
    }

    public List<Chambre> findByType(Chambre.TypeChambre type) {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambres WHERE type_chambre = ? ORDER BY etage, numero";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                chambres.add(mapResultSetToChambre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des chambres par type : " + e.getMessage());
        }
        return chambres;
    }

    public int insert(Chambre chambre) {
        String sql = "INSERT INTO chambres (numero, etage, batiment, type_chambre, capacite, nb_lits_occupes, equipements, tarif_journalier, actif, en_maintenance, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, chambre.getNumero());
            stmt.setInt(2, chambre.getEtage());
            stmt.setString(3, chambre.getBatiment());
            stmt.setString(4, chambre.getTypeChambre().name());
            stmt.setInt(5, chambre.getCapacite());
            stmt.setInt(6, chambre.getNbLitsOccupes());
            stmt.setString(7, chambre.getEquipements());
            stmt.setBigDecimal(8, chambre.getTarifJournalier());
            stmt.setBoolean(9, chambre.isActif());
            stmt.setBoolean(10, chambre.isEnMaintenance());
            stmt.setString(11, chambre.getNotes());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la chambre : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Chambre chambre) {
        String sql = "UPDATE chambres SET numero = ?, etage = ?, batiment = ?, type_chambre = ?, capacite = ?, nb_lits_occupes = ?, equipements = ?, tarif_journalier = ?, actif = ?, en_maintenance = ?, notes = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chambre.getNumero());
            stmt.setInt(2, chambre.getEtage());
            stmt.setString(3, chambre.getBatiment());
            stmt.setString(4, chambre.getTypeChambre().name());
            stmt.setInt(5, chambre.getCapacite());
            stmt.setInt(6, chambre.getNbLitsOccupes());
            stmt.setString(7, chambre.getEquipements());
            stmt.setBigDecimal(8, chambre.getTarifJournalier());
            stmt.setBoolean(9, chambre.isActif());
            stmt.setBoolean(10, chambre.isEnMaintenance());
            stmt.setString(11, chambre.getNotes());
            stmt.setInt(12, chambre.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la chambre : " + e.getMessage());
        }
        return false;
    }

    public boolean updateNbLitsOccupes(int chambreId, int nbLitsOccupes) {
        String sql = "UPDATE chambres SET nb_lits_occupes = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nbLitsOccupes);
            stmt.setInt(2, chambreId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du nombre de lits occupes : " + e.getMessage());
        }
        return false;
    }

    public Chambre findFirstAvailable() {
        String sql = "SELECT * FROM chambres WHERE actif = true AND nb_lits_occupes < capacite AND en_maintenance = false ORDER BY etage, numero LIMIT 1";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return mapResultSetToChambre(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la premiere chambre disponible : " + e.getMessage());
        }
        return null;
    }

    public boolean setAvailable(int chambreId, boolean available) {
        String sql = "UPDATE chambres SET en_maintenance = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, !available);
            stmt.setInt(2, chambreId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la disponibilite de la chambre : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM chambres WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la chambre : " + e.getMessage());
        }
        return false;
    }

    private Chambre mapResultSetToChambre(ResultSet rs) throws SQLException {
        Chambre chambre = new Chambre();
        chambre.setId(rs.getInt("id"));
        chambre.setNumero(rs.getString("numero"));
        chambre.setEtage(rs.getInt("etage"));
        chambre.setBatiment(rs.getString("batiment"));
        chambre.setTypeChambre(Chambre.TypeChambre.valueOf(rs.getString("type_chambre")));
        chambre.setCapacite(rs.getInt("capacite"));
        chambre.setNbLitsOccupes(rs.getInt("nb_lits_occupes"));
        chambre.setEquipements(rs.getString("equipements"));
        BigDecimal tarif = rs.getBigDecimal("tarif_journalier");
        if (tarif != null) {
            chambre.setTarifJournalier(tarif);
        }
        chambre.setActif(rs.getBoolean("actif"));
        chambre.setEnMaintenance(rs.getBoolean("en_maintenance"));
        chambre.setNotes(rs.getString("notes"));
        return chambre;
    }
}
