package appli.dao;

import appli.model.Chambre;
import appli.util.DBConnection;

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
        String sql = "SELECT * FROM chambres WHERE occupee = false ORDER BY etage, numero";
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
        String sql = "INSERT INTO chambres (numero, etage, type_chambre, capacite, occupee) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, chambre.getNumero());
            stmt.setInt(2, chambre.getEtage());
            stmt.setString(3, chambre.getTypeChambre().name());
            stmt.setInt(4, chambre.getCapacite());
            stmt.setBoolean(5, chambre.isOccupee());
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
        String sql = "UPDATE chambres SET numero = ?, etage = ?, type_chambre = ?, capacite = ?, occupee = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chambre.getNumero());
            stmt.setInt(2, chambre.getEtage());
            stmt.setString(3, chambre.getTypeChambre().name());
            stmt.setInt(4, chambre.getCapacite());
            stmt.setBoolean(5, chambre.isOccupee());
            stmt.setInt(6, chambre.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la chambre : " + e.getMessage());
        }
        return false;
    }

    public boolean updateOccupation(int chambreId, boolean occupee) {
        String sql = "UPDATE chambres SET occupee = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, occupee);
            stmt.setInt(2, chambreId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de l'occupation : " + e.getMessage());
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
        chambre.setTypeChambre(Chambre.TypeChambre.valueOf(rs.getString("type_chambre")));
        chambre.setCapacite(rs.getInt("capacite"));
        chambre.setOccupee(rs.getBoolean("occupee"));
        return chambre;
    }
}
