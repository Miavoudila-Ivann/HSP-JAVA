package appli.dao;

import appli.model.LigneCommande;
import appli.model.Produit;
import appli.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeDAO {

    public int insert(LigneCommande ligne) {
        String sql = "INSERT INTO lignes_commande " +
                "(commande_id, produit_id, quantite_commandee, quantite_recue, " +
                "prix_unitaire, tva, remise_pourcent, montant_ht) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ligne.getCommandeId());
            stmt.setInt(2, ligne.getProduitId());
            stmt.setInt(3, ligne.getQuantiteCommandee());
            stmt.setInt(4, ligne.getQuantiteRecue());
            stmt.setBigDecimal(5, ligne.getPrixUnitaire());
            stmt.setBigDecimal(6, ligne.getTva());
            stmt.setBigDecimal(7, ligne.getRemisePourcent());
            stmt.setBigDecimal(8, ligne.getMontantHt());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur insert LigneCommande: " + e.getMessage(), e);
        }
        return -1;
    }

    public List<LigneCommande> findByCommandeId(int commandeId) {
        String sql = "SELECT lc.*, p.nom AS produit_nom, p.code AS produit_code " +
                "FROM lignes_commande lc " +
                "LEFT JOIN produits p ON lc.produit_id = p.id " +
                "WHERE lc.commande_id = ?";
        List<LigneCommande> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCommandeId LigneCommande: " + e.getMessage(), e);
        }
        return list;
    }

    public void updateQuantiteRecue(int ligneId, int quantiteRecue) {
        String sql = "UPDATE lignes_commande SET quantite_recue = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantiteRecue);
            stmt.setInt(2, ligneId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateQuantiteRecue LigneCommande: " + e.getMessage(), e);
        }
    }

    private LigneCommande mapResultSet(ResultSet rs) throws SQLException {
        LigneCommande l = new LigneCommande();
        l.setId(rs.getInt("id"));
        l.setCommandeId(rs.getInt("commande_id"));
        l.setProduitId(rs.getInt("produit_id"));
        l.setQuantiteCommandee(rs.getInt("quantite_commandee"));
        l.setQuantiteRecue(rs.getInt("quantite_recue"));
        l.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
        l.setTva(rs.getBigDecimal("tva"));
        l.setRemisePourcent(rs.getBigDecimal("remise_pourcent"));
        l.setMontantHt(rs.getBigDecimal("montant_ht"));

        // Produit (join)
        try {
            String pNom = rs.getString("produit_nom");
            String pCode = rs.getString("produit_code");
            if (pNom != null) {
                Produit p = new Produit();
                p.setId(l.getProduitId());
                p.setNom(pNom);
                p.setCode(pCode);
                l.setProduit(p);
            }
        } catch (SQLException ignored) {}

        return l;
    }
}
