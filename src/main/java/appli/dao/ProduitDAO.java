package appli.dao;

import appli.model.Produit;
import appli.util.DBConnection;

import java.math.BigDecimal;
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
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des produits : " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> findByCategorieId(int categorieId) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE categorie_id = ? AND actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categorieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits par categorie : " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> findByFournisseurId(int fournisseurId) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE fournisseur_principal_id = ? AND actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fournisseurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits par fournisseur : " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> search(String searchTerm) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE (nom LIKE ? OR code LIKE ? OR code_cip LIKE ? OR nom_commercial LIKE ?) AND actif = true ORDER BY nom";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des produits : " + e.getMessage());
        }
        return produits;
    }

    public int insert(Produit produit) {
        String sql = "INSERT INTO produits (code, code_cip, nom, nom_commercial, description, categorie_id, forme, dosage, " +
                "unite_mesure, prix_unitaire, tva, niveau_dangerosite, conditions_stockage, temperature_min, temperature_max, " +
                "necessite_ordonnance, stupefiant, date_peremption_alerte_jours, seuil_alerte_stock, seuil_commande_auto, " +
                "fournisseur_principal_id, actif, date_creation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getCode());
            stmt.setString(2, produit.getCodeCip());
            stmt.setString(3, produit.getNom());
            stmt.setString(4, produit.getNomCommercial());
            stmt.setString(5, produit.getDescription());
            if (produit.getCategorieId() != null) {
                stmt.setInt(6, produit.getCategorieId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (produit.getForme() != null) {
                stmt.setString(7, produit.getForme().name());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            stmt.setString(8, produit.getDosage());
            stmt.setString(9, produit.getUniteMesure());
            if (produit.getPrixUnitaire() != null) {
                stmt.setBigDecimal(10, produit.getPrixUnitaire());
            } else {
                stmt.setNull(10, Types.DECIMAL);
            }
            stmt.setBigDecimal(11, produit.getTva());
            stmt.setString(12, produit.getNiveauDangerosite().name());
            stmt.setString(13, produit.getConditionsStockage());
            if (produit.getTemperatureMin() != null) {
                stmt.setBigDecimal(14, produit.getTemperatureMin());
            } else {
                stmt.setNull(14, Types.DECIMAL);
            }
            if (produit.getTemperatureMax() != null) {
                stmt.setBigDecimal(15, produit.getTemperatureMax());
            } else {
                stmt.setNull(15, Types.DECIMAL);
            }
            stmt.setBoolean(16, produit.isNecessiteOrdonnance());
            stmt.setBoolean(17, produit.isStupefiant());
            stmt.setInt(18, produit.getDatePeremptionAlerteJours());
            stmt.setInt(19, produit.getSeuilAlerteStock());
            if (produit.getSeuilCommandeAuto() != null) {
                stmt.setInt(20, produit.getSeuilCommandeAuto());
            } else {
                stmt.setNull(20, Types.INTEGER);
            }
            if (produit.getFournisseurPrincipalId() != null) {
                stmt.setInt(21, produit.getFournisseurPrincipalId());
            } else {
                stmt.setNull(21, Types.INTEGER);
            }
            stmt.setBoolean(22, produit.isActif());
            stmt.setTimestamp(23, Timestamp.valueOf(LocalDateTime.now()));
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
        String sql = "UPDATE produits SET code = ?, code_cip = ?, nom = ?, nom_commercial = ?, description = ?, categorie_id = ?, " +
                "forme = ?, dosage = ?, unite_mesure = ?, prix_unitaire = ?, tva = ?, niveau_dangerosite = ?, conditions_stockage = ?, " +
                "temperature_min = ?, temperature_max = ?, necessite_ordonnance = ?, stupefiant = ?, date_peremption_alerte_jours = ?, " +
                "seuil_alerte_stock = ?, seuil_commande_auto = ?, fournisseur_principal_id = ?, actif = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produit.getCode());
            stmt.setString(2, produit.getCodeCip());
            stmt.setString(3, produit.getNom());
            stmt.setString(4, produit.getNomCommercial());
            stmt.setString(5, produit.getDescription());
            if (produit.getCategorieId() != null) {
                stmt.setInt(6, produit.getCategorieId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (produit.getForme() != null) {
                stmt.setString(7, produit.getForme().name());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            stmt.setString(8, produit.getDosage());
            stmt.setString(9, produit.getUniteMesure());
            if (produit.getPrixUnitaire() != null) {
                stmt.setBigDecimal(10, produit.getPrixUnitaire());
            } else {
                stmt.setNull(10, Types.DECIMAL);
            }
            stmt.setBigDecimal(11, produit.getTva());
            stmt.setString(12, produit.getNiveauDangerosite().name());
            stmt.setString(13, produit.getConditionsStockage());
            if (produit.getTemperatureMin() != null) {
                stmt.setBigDecimal(14, produit.getTemperatureMin());
            } else {
                stmt.setNull(14, Types.DECIMAL);
            }
            if (produit.getTemperatureMax() != null) {
                stmt.setBigDecimal(15, produit.getTemperatureMax());
            } else {
                stmt.setNull(15, Types.DECIMAL);
            }
            stmt.setBoolean(16, produit.isNecessiteOrdonnance());
            stmt.setBoolean(17, produit.isStupefiant());
            stmt.setInt(18, produit.getDatePeremptionAlerteJours());
            stmt.setInt(19, produit.getSeuilAlerteStock());
            if (produit.getSeuilCommandeAuto() != null) {
                stmt.setInt(20, produit.getSeuilCommandeAuto());
            } else {
                stmt.setNull(20, Types.INTEGER);
            }
            if (produit.getFournisseurPrincipalId() != null) {
                stmt.setInt(21, produit.getFournisseurPrincipalId());
            } else {
                stmt.setNull(21, Types.INTEGER);
            }
            stmt.setBoolean(22, produit.isActif());
            stmt.setInt(23, produit.getId());
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
        produit.setCodeCip(rs.getString("code_cip"));
        produit.setNom(rs.getString("nom"));
        produit.setNomCommercial(rs.getString("nom_commercial"));
        produit.setDescription(rs.getString("description"));

        int categorieId = rs.getInt("categorie_id");
        if (!rs.wasNull()) {
            produit.setCategorieId(categorieId);
        }

        String forme = rs.getString("forme");
        if (forme != null) {
            produit.setForme(Produit.Forme.valueOf(forme));
        }

        produit.setDosage(rs.getString("dosage"));
        produit.setUniteMesure(rs.getString("unite_mesure"));
        produit.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
        produit.setTva(rs.getBigDecimal("tva"));

        String niveauDangerosite = rs.getString("niveau_dangerosite");
        if (niveauDangerosite != null) {
            produit.setNiveauDangerosite(Produit.NiveauDangerosite.valueOf(niveauDangerosite));
        }

        produit.setConditionsStockage(rs.getString("conditions_stockage"));
        produit.setTemperatureMin(rs.getBigDecimal("temperature_min"));
        produit.setTemperatureMax(rs.getBigDecimal("temperature_max"));
        produit.setNecessiteOrdonnance(rs.getBoolean("necessite_ordonnance"));
        produit.setStupefiant(rs.getBoolean("stupefiant"));
        produit.setDatePeremptionAlerteJours(rs.getInt("date_peremption_alerte_jours"));
        produit.setSeuilAlerteStock(rs.getInt("seuil_alerte_stock"));

        int seuilCommandeAuto = rs.getInt("seuil_commande_auto");
        if (!rs.wasNull()) {
            produit.setSeuilCommandeAuto(seuilCommandeAuto);
        }

        int fournisseurPrincipalId = rs.getInt("fournisseur_principal_id");
        if (!rs.wasNull()) {
            produit.setFournisseurPrincipalId(fournisseurPrincipalId);
        }

        produit.setActif(rs.getBoolean("actif"));

        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            produit.setDateCreation(dateCreation.toLocalDateTime());
        }

        return produit;
    }
}
