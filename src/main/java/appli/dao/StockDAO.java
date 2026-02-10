package appli.dao;

import appli.model.Stock;
import appli.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    public Stock findById(int id) {
        String sql = "SELECT * FROM stocks WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStock(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du stock : " + e.getMessage());
        }
        return null;
    }

    public List<Stock> findByProduitId(int produitId) {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks WHERE produit_id = ? ORDER BY date_peremption";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stocks.add(mapResultSetToStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des stocks par produit : " + e.getMessage());
        }
        return stocks;
    }

    public List<Stock> findByEmplacementId(int emplacementId) {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks WHERE emplacement_id = ? ORDER BY date_peremption";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emplacementId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stocks.add(mapResultSetToStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des stocks par emplacement : " + e.getMessage());
        }
        return stocks;
    }

    public List<Stock> findAll() {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks ORDER BY date_peremption";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stocks.add(mapResultSetToStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des stocks : " + e.getMessage());
        }
        return stocks;
    }

    public List<Stock> findExpiringBefore(int days) {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks WHERE date_peremption <= DATE_ADD(CURDATE(), INTERVAL ? DAY) AND date_peremption IS NOT NULL ORDER BY date_peremption";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stocks.add(mapResultSetToStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des stocks proches de la peremption : " + e.getMessage());
        }
        return stocks;
    }

    public int insert(Stock stock) {
        String sql = "INSERT INTO stocks (produit_id, emplacement_id, lot, quantite, quantite_reservee, date_peremption, " +
                "date_reception, prix_unitaire_achat, fournisseur_id, numero_commande, date_derniere_maj) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, stock.getProduitId());
            stmt.setInt(2, stock.getEmplacementId());
            stmt.setString(3, stock.getLot());
            stmt.setInt(4, stock.getQuantite());
            stmt.setInt(5, stock.getQuantiteReservee());
            if (stock.getDatePeremption() != null) {
                stmt.setDate(6, Date.valueOf(stock.getDatePeremption()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            if (stock.getDateReception() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(stock.getDateReception()));
            } else {
                stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            }
            if (stock.getPrixUnitaireAchat() != null) {
                stmt.setBigDecimal(8, stock.getPrixUnitaireAchat());
            } else {
                stmt.setNull(8, Types.DECIMAL);
            }
            if (stock.getFournisseurId() != null) {
                stmt.setInt(9, stock.getFournisseurId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setString(10, stock.getNumeroCommande());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du stock : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Stock stock) {
        String sql = "UPDATE stocks SET produit_id = ?, emplacement_id = ?, lot = ?, quantite = ?, quantite_reservee = ?, " +
                "date_peremption = ?, date_reception = ?, prix_unitaire_achat = ?, fournisseur_id = ?, numero_commande = ?, " +
                "date_derniere_maj = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stock.getProduitId());
            stmt.setInt(2, stock.getEmplacementId());
            stmt.setString(3, stock.getLot());
            stmt.setInt(4, stock.getQuantite());
            stmt.setInt(5, stock.getQuantiteReservee());
            if (stock.getDatePeremption() != null) {
                stmt.setDate(6, Date.valueOf(stock.getDatePeremption()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            if (stock.getDateReception() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(stock.getDateReception()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }
            if (stock.getPrixUnitaireAchat() != null) {
                stmt.setBigDecimal(8, stock.getPrixUnitaireAchat());
            } else {
                stmt.setNull(8, Types.DECIMAL);
            }
            if (stock.getFournisseurId() != null) {
                stmt.setInt(9, stock.getFournisseurId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setString(10, stock.getNumeroCommande());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(12, stock.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du stock : " + e.getMessage());
        }
        return false;
    }

    public boolean updateQuantite(int stockId, int quantite) {
        String sql = "UPDATE stocks SET quantite = ?, date_derniere_maj = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, stockId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la quantite : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM stocks WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du stock : " + e.getMessage());
        }
        return false;
    }

    public int getTotalQuantiteByProduit(int produitId) {
        String sql = "SELECT COALESCE(SUM(quantite), 0) AS total FROM stocks WHERE produit_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul de la quantite totale : " + e.getMessage());
        }
        return 0;
    }

    private Stock mapResultSetToStock(ResultSet rs) throws SQLException {
        Stock stock = new Stock();
        stock.setId(rs.getInt("id"));
        stock.setProduitId(rs.getInt("produit_id"));
        stock.setEmplacementId(rs.getInt("emplacement_id"));
        stock.setLot(rs.getString("lot"));
        stock.setQuantite(rs.getInt("quantite"));
        stock.setQuantiteReservee(rs.getInt("quantite_reservee"));

        Date datePeremption = rs.getDate("date_peremption");
        if (datePeremption != null) {
            stock.setDatePeremption(datePeremption.toLocalDate());
        }

        Timestamp dateReception = rs.getTimestamp("date_reception");
        if (dateReception != null) {
            stock.setDateReception(dateReception.toLocalDateTime());
        }

        stock.setPrixUnitaireAchat(rs.getBigDecimal("prix_unitaire_achat"));

        int fournisseurId = rs.getInt("fournisseur_id");
        if (!rs.wasNull()) {
            stock.setFournisseurId(fournisseurId);
        }

        stock.setNumeroCommande(rs.getString("numero_commande"));

        Timestamp dateDerniereMaj = rs.getTimestamp("date_derniere_maj");
        if (dateDerniereMaj != null) {
            stock.setDateDerniereMaj(dateDerniereMaj.toLocalDateTime());
        }

        return stock;
    }
}
