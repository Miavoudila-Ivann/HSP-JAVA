package appli.dao;

import appli.model.ProduitFournisseur;
import appli.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitFournisseurDAO {

    public ProduitFournisseur findById(int id) {
        String sql = "SELECT * FROM produits_fournisseurs WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduitFournisseur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du produit fournisseur : " + e.getMessage());
        }
        return null;
    }

    public List<ProduitFournisseur> findByProduitId(int produitId) {
        List<ProduitFournisseur> produitsFournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM produits_fournisseurs WHERE produit_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produitsFournisseurs.add(mapResultSetToProduitFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits fournisseurs par produit : " + e.getMessage());
        }
        return produitsFournisseurs;
    }

    public List<ProduitFournisseur> findByFournisseurId(int fournisseurId) {
        List<ProduitFournisseur> produitsFournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM produits_fournisseurs WHERE fournisseur_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fournisseurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produitsFournisseurs.add(mapResultSetToProduitFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits fournisseurs par fournisseur : " + e.getMessage());
        }
        return produitsFournisseurs;
    }

    public ProduitFournisseur findByProduitAndFournisseur(int produitId, int fournisseurId) {
        String sql = "SELECT * FROM produits_fournisseurs WHERE produit_id = ? AND fournisseur_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            stmt.setInt(2, fournisseurId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduitFournisseur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du produit fournisseur par produit et fournisseur : " + e.getMessage());
        }
        return null;
    }

    public List<ProduitFournisseur> findAll() {
        List<ProduitFournisseur> produitsFournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM produits_fournisseurs";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produitsFournisseurs.add(mapResultSetToProduitFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des produits fournisseurs : " + e.getMessage());
        }
        return produitsFournisseurs;
    }

    public int insert(ProduitFournisseur pf) {
        String sql = "INSERT INTO produits_fournisseurs (produit_id, fournisseur_id, reference_fournisseur, prix_achat, delai_livraison_jours, quantite_minimum_commande, est_principal, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pf.getProduitId());
            stmt.setInt(2, pf.getFournisseurId());
            stmt.setString(3, pf.getReferenceFournisseur());
            if (pf.getPrixAchat() != null) {
                stmt.setBigDecimal(4, pf.getPrixAchat());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }
            if (pf.getDelaiLivraisonJours() != null) {
                stmt.setInt(5, pf.getDelaiLivraisonJours());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            if (pf.getQuantiteMinimumCommande() != null) {
                stmt.setInt(6, pf.getQuantiteMinimumCommande());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setBoolean(7, pf.isEstPrincipal());
            stmt.setBoolean(8, pf.isActif());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du produit fournisseur : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(ProduitFournisseur pf) {
        String sql = "UPDATE produits_fournisseurs SET produit_id = ?, fournisseur_id = ?, reference_fournisseur = ?, prix_achat = ?, delai_livraison_jours = ?, quantite_minimum_commande = ?, est_principal = ?, actif = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pf.getProduitId());
            stmt.setInt(2, pf.getFournisseurId());
            stmt.setString(3, pf.getReferenceFournisseur());
            if (pf.getPrixAchat() != null) {
                stmt.setBigDecimal(4, pf.getPrixAchat());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }
            if (pf.getDelaiLivraisonJours() != null) {
                stmt.setInt(5, pf.getDelaiLivraisonJours());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            if (pf.getQuantiteMinimumCommande() != null) {
                stmt.setInt(6, pf.getQuantiteMinimumCommande());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setBoolean(7, pf.isEstPrincipal());
            stmt.setBoolean(8, pf.isActif());
            stmt.setInt(9, pf.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du produit fournisseur : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM produits_fournisseurs WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit fournisseur : " + e.getMessage());
        }
        return false;
    }

    private ProduitFournisseur mapResultSetToProduitFournisseur(ResultSet rs) throws SQLException {
        ProduitFournisseur pf = new ProduitFournisseur();
        pf.setId(rs.getInt("id"));
        pf.setProduitId(rs.getInt("produit_id"));
        pf.setFournisseurId(rs.getInt("fournisseur_id"));
        pf.setReferenceFournisseur(rs.getString("reference_fournisseur"));
        BigDecimal prixAchat = rs.getBigDecimal("prix_achat");
        if (prixAchat != null) {
            pf.setPrixAchat(prixAchat);
        }
        int delai = rs.getInt("delai_livraison_jours");
        if (!rs.wasNull()) {
            pf.setDelaiLivraisonJours(delai);
        }
        int qteMin = rs.getInt("quantite_minimum_commande");
        if (!rs.wasNull()) {
            pf.setQuantiteMinimumCommande(qteMin);
        }
        pf.setEstPrincipal(rs.getBoolean("est_principal"));
        pf.setActif(rs.getBoolean("actif"));
        return pf;
    }
}
