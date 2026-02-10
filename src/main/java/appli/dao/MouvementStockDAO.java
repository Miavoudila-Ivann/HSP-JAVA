package appli.dao;

import appli.model.MouvementStock;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MouvementStockDAO {

    public MouvementStock findById(int id) {
        String sql = "SELECT * FROM mouvements_stock WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToMouvementStock(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du mouvement de stock : " + e.getMessage());
        }
        return null;
    }

    public List<MouvementStock> findByStockId(int stockId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements_stock WHERE stock_id = ? ORDER BY date_mouvement DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stockId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des mouvements par stock : " + e.getMessage());
        }
        return mouvements;
    }

    public List<MouvementStock> findByProduitId(int produitId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements_stock WHERE produit_id = ? ORDER BY date_mouvement DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des mouvements par produit : " + e.getMessage());
        }
        return mouvements;
    }

    public List<MouvementStock> findByTypeMouvement(MouvementStock.TypeMouvement typeMouvement) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements_stock WHERE type_mouvement = ? ORDER BY date_mouvement DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, typeMouvement.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des mouvements par type : " + e.getMessage());
        }
        return mouvements;
    }

    public List<MouvementStock> findByUserId(int userId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements_stock WHERE user_id = ? ORDER BY date_mouvement DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des mouvements par utilisateur : " + e.getMessage());
        }
        return mouvements;
    }

    public List<MouvementStock> findByDateRange(LocalDateTime debut, LocalDateTime fin) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements_stock WHERE date_mouvement BETWEEN ? AND ? ORDER BY date_mouvement DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des mouvements par date : " + e.getMessage());
        }
        return mouvements;
    }

    public List<MouvementStock> findByDossierId(int dossierId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements_stock WHERE dossier_id = ? ORDER BY date_mouvement DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dossierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des mouvements par dossier : " + e.getMessage());
        }
        return mouvements;
    }

    public List<MouvementStock> findAll() {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements_stock ORDER BY date_mouvement DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des mouvements de stock : " + e.getMessage());
        }
        return mouvements;
    }

    public int insert(MouvementStock mouvement) {
        String sql = "INSERT INTO mouvements_stock (stock_id, produit_id, type_mouvement, quantite, quantite_avant, quantite_apres, motif, reference_document, emplacement_source_id, emplacement_destination_id, dossier_id, ordonnance_id, user_id, date_mouvement, valide, date_validation, validateur_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mouvement.getStockId());
            stmt.setInt(2, mouvement.getProduitId());
            stmt.setString(3, mouvement.getTypeMouvement().name());
            stmt.setInt(4, mouvement.getQuantite());
            stmt.setInt(5, mouvement.getQuantiteAvant());
            stmt.setInt(6, mouvement.getQuantiteApres());
            stmt.setString(7, mouvement.getMotif());
            stmt.setString(8, mouvement.getReferenceDocument());
            if (mouvement.getEmplacementSourceId() != null) {
                stmt.setInt(9, mouvement.getEmplacementSourceId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            if (mouvement.getEmplacementDestinationId() != null) {
                stmt.setInt(10, mouvement.getEmplacementDestinationId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }
            if (mouvement.getDossierId() != null) {
                stmt.setInt(11, mouvement.getDossierId());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }
            if (mouvement.getOrdonnanceId() != null) {
                stmt.setInt(12, mouvement.getOrdonnanceId());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }
            stmt.setInt(13, mouvement.getUserId());
            stmt.setTimestamp(14, Timestamp.valueOf(mouvement.getDateMouvement()));
            stmt.setBoolean(15, mouvement.isValide());
            if (mouvement.getDateValidation() != null) {
                stmt.setTimestamp(16, Timestamp.valueOf(mouvement.getDateValidation()));
            } else {
                stmt.setNull(16, Types.TIMESTAMP);
            }
            if (mouvement.getValidateurId() != null) {
                stmt.setInt(17, mouvement.getValidateurId());
            } else {
                stmt.setNull(17, Types.INTEGER);
            }
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du mouvement de stock : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(MouvementStock mouvement) {
        String sql = "UPDATE mouvements_stock SET stock_id = ?, produit_id = ?, type_mouvement = ?, quantite = ?, quantite_avant = ?, quantite_apres = ?, motif = ?, reference_document = ?, emplacement_source_id = ?, emplacement_destination_id = ?, dossier_id = ?, ordonnance_id = ?, user_id = ?, date_mouvement = ?, valide = ?, date_validation = ?, validateur_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mouvement.getStockId());
            stmt.setInt(2, mouvement.getProduitId());
            stmt.setString(3, mouvement.getTypeMouvement().name());
            stmt.setInt(4, mouvement.getQuantite());
            stmt.setInt(5, mouvement.getQuantiteAvant());
            stmt.setInt(6, mouvement.getQuantiteApres());
            stmt.setString(7, mouvement.getMotif());
            stmt.setString(8, mouvement.getReferenceDocument());
            if (mouvement.getEmplacementSourceId() != null) {
                stmt.setInt(9, mouvement.getEmplacementSourceId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            if (mouvement.getEmplacementDestinationId() != null) {
                stmt.setInt(10, mouvement.getEmplacementDestinationId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }
            if (mouvement.getDossierId() != null) {
                stmt.setInt(11, mouvement.getDossierId());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }
            if (mouvement.getOrdonnanceId() != null) {
                stmt.setInt(12, mouvement.getOrdonnanceId());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }
            stmt.setInt(13, mouvement.getUserId());
            stmt.setTimestamp(14, Timestamp.valueOf(mouvement.getDateMouvement()));
            stmt.setBoolean(15, mouvement.isValide());
            if (mouvement.getDateValidation() != null) {
                stmt.setTimestamp(16, Timestamp.valueOf(mouvement.getDateValidation()));
            } else {
                stmt.setNull(16, Types.TIMESTAMP);
            }
            if (mouvement.getValidateurId() != null) {
                stmt.setInt(17, mouvement.getValidateurId());
            } else {
                stmt.setNull(17, Types.INTEGER);
            }
            stmt.setInt(18, mouvement.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du mouvement de stock : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM mouvements_stock WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du mouvement de stock : " + e.getMessage());
        }
        return false;
    }

    private MouvementStock mapResultSetToMouvementStock(ResultSet rs) throws SQLException {
        MouvementStock mouvement = new MouvementStock();
        mouvement.setId(rs.getInt("id"));
        mouvement.setStockId(rs.getInt("stock_id"));
        mouvement.setProduitId(rs.getInt("produit_id"));
        String typeMouvement = rs.getString("type_mouvement");
        if (typeMouvement != null) {
            mouvement.setTypeMouvement(MouvementStock.TypeMouvement.valueOf(typeMouvement));
        }
        mouvement.setQuantite(rs.getInt("quantite"));
        mouvement.setQuantiteAvant(rs.getInt("quantite_avant"));
        mouvement.setQuantiteApres(rs.getInt("quantite_apres"));
        mouvement.setMotif(rs.getString("motif"));
        mouvement.setReferenceDocument(rs.getString("reference_document"));
        int emplacementSourceId = rs.getInt("emplacement_source_id");
        if (!rs.wasNull()) {
            mouvement.setEmplacementSourceId(emplacementSourceId);
        }
        int emplacementDestinationId = rs.getInt("emplacement_destination_id");
        if (!rs.wasNull()) {
            mouvement.setEmplacementDestinationId(emplacementDestinationId);
        }
        int dossierId = rs.getInt("dossier_id");
        if (!rs.wasNull()) {
            mouvement.setDossierId(dossierId);
        }
        int ordonnanceId = rs.getInt("ordonnance_id");
        if (!rs.wasNull()) {
            mouvement.setOrdonnanceId(ordonnanceId);
        }
        mouvement.setUserId(rs.getInt("user_id"));
        Timestamp dateMouvement = rs.getTimestamp("date_mouvement");
        if (dateMouvement != null) {
            mouvement.setDateMouvement(dateMouvement.toLocalDateTime());
        }
        mouvement.setValide(rs.getBoolean("valide"));
        Timestamp dateValidation = rs.getTimestamp("date_validation");
        if (dateValidation != null) {
            mouvement.setDateValidation(dateValidation.toLocalDateTime());
        }
        int validateurId = rs.getInt("validateur_id");
        if (!rs.wasNull()) {
            mouvement.setValidateurId(validateurId);
        }
        return mouvement;
    }
}
