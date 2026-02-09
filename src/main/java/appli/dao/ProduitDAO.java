package appli.dao;

import appli.model.Produit;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public Produit findById(int id) {
        String sql = "SELECT * FROM produits WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du produit : " + e.getMessage());
        }
        return null;
    }

    public Produit findByCode(String code) {
        String sql = "SELECT * FROM produits WHERE code = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du produit par code : " + e.getMessage());
        }
        return null;
    }

    public List<Produit> findAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des produits : " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> findByCategorie(String categorie) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE categorie = ? AND actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categorie);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits par categorie : " + e.getMessage());
        }
        return produits;
    }

    public int insert(Produit produit) {
        String sql = "INSERT INTO produits (code, nom, description, categorie, unite_mesure, niveau_dangerosite, conditions_stockage, date_peremption_alerte_jours, seuil_alerte_stock, actif, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getCode());
            stmt.setString(2, produit.getNom());
            stmt.setString(3, produit.getDescription());
            stmt.setString(4, produit.getCategorie());
            stmt.setString(5, produit.getUniteMesure());
            stmt.setString(6, produit.getNiveauDangerosite().name());
            stmt.setString(7, produit.getConditionsStockage());
            stmt.setInt(8, produit.getDatePeremptionAlerteJours());
            stmt.setInt(9, produit.getSeuilAlerteStock());
            stmt.setBoolean(10, produit.isActif());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du produit : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Produit produit) {
        String sql = "UPDATE produits SET code = ?, nom = ?, description = ?, categorie = ?, unite_mesure = ?, niveau_dangerosite = ?, conditions_stockage = ?, date_peremption_alerte_jours = ?, seuil_alerte_stock = ?, actif = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produit.getCode());
            stmt.setString(2, produit.getNom());
            stmt.setString(3, produit.getDescription());
            stmt.setString(4, produit.getCategorie());
            stmt.setString(5, produit.getUniteMesure());
            stmt.setString(6, produit.getNiveauDangerosite().name());
            stmt.setString(7, produit.getConditionsStockage());
            stmt.setInt(8, produit.getDatePeremptionAlerteJours());
            stmt.setInt(9, produit.getSeuilAlerteStock());
            stmt.setBoolean(10, produit.isActif());
            stmt.setInt(11, produit.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du produit : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "UPDATE produits SET actif = false WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit : " + e.getMessage());
        }
        return false;
    }

    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setId(rs.getInt("id"));
        produit.setCode(rs.getString("code"));
        produit.setNom(rs.getString("nom"));
        produit.setDescription(rs.getString("description"));
        produit.setCategorie(rs.getString("categorie"));
        produit.setUniteMesure(rs.getString("unite_mesure"));
        String niveau = rs.getString("niveau_dangerosite");
        if (niveau != null) {
            produit.setNiveauDangerosite(Produit.NiveauDangerosite.valueOf(niveau));
        }
        produit.setConditionsStockage(rs.getString("conditions_stockage"));
        produit.setDatePeremptionAlerteJours(rs.getInt("date_peremption_alerte_jours"));
        produit.setSeuilAlerteStock(rs.getInt("seuil_alerte_stock"));
        produit.setActif(rs.getBoolean("actif"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            produit.setDateCreation(dateCreation.toLocalDateTime());
        }
        return produit;
    }
}
