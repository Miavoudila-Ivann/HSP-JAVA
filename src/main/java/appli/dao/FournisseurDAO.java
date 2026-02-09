package appli.dao;

import appli.model.Fournisseur;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {

    public Fournisseur findById(int id) {
        String sql = "SELECT * FROM fournisseurs WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFournisseur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du fournisseur : " + e.getMessage());
        }
        return null;
    }

    public List<Fournisseur> findAll() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseurs WHERE actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des fournisseurs : " + e.getMessage());
        }
        return fournisseurs;
    }

    public List<Fournisseur> search(String searchTerm) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseurs WHERE (nom LIKE ? OR email LIKE ?) AND actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fournisseurs.add(mapResultSetToFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de fournisseurs : " + e.getMessage());
        }
        return fournisseurs;
    }

    public int insert(Fournisseur fournisseur) {
        String sql = "INSERT INTO fournisseurs (nom, adresse, telephone, email, contact_nom, actif, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getAdresse());
            stmt.setString(3, fournisseur.getTelephone());
            stmt.setString(4, fournisseur.getEmail());
            stmt.setString(5, fournisseur.getContactNom());
            stmt.setBoolean(6, fournisseur.isActif());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du fournisseur : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Fournisseur fournisseur) {
        String sql = "UPDATE fournisseurs SET nom = ?, adresse = ?, telephone = ?, email = ?, contact_nom = ?, actif = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getAdresse());
            stmt.setString(3, fournisseur.getTelephone());
            stmt.setString(4, fournisseur.getEmail());
            stmt.setString(5, fournisseur.getContactNom());
            stmt.setBoolean(6, fournisseur.isActif());
            stmt.setInt(7, fournisseur.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du fournisseur : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "UPDATE fournisseurs SET actif = false WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur : " + e.getMessage());
        }
        return false;
    }

    private Fournisseur mapResultSetToFournisseur(ResultSet rs) throws SQLException {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(rs.getInt("id"));
        fournisseur.setNom(rs.getString("nom"));
        fournisseur.setAdresse(rs.getString("adresse"));
        fournisseur.setTelephone(rs.getString("telephone"));
        fournisseur.setEmail(rs.getString("email"));
        fournisseur.setContactNom(rs.getString("contact_nom"));
        fournisseur.setActif(rs.getBoolean("actif"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            fournisseur.setDateCreation(dateCreation.toLocalDateTime());
        }
        return fournisseur;
    }
}
