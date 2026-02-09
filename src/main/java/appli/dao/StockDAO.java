package appli.dao;

import appli.model.Stock;
import appli.util.DBConnection;

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

    public List<Stock> findAll() {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stocks ORDER BY date_peremption";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
        String sql = "SELECT * FROM stocks WHERE date_peremption <= DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY date_peremption";
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
        String sql = "INSERT INTO stocks (produit_id, quantite, emplacement, lot, date_peremption, date_derniere_maj) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, stock.getProduitId());
            stmt.setInt(2, stock.getQuantite());
            stmt.setString(3, stock.getEmplacement());
            stmt.setString(4, stock.getLot());
            stmt.setDate(5, stock.getDatePeremption() != null ? Date.valueOf(stock.getDatePeremption()) : null);
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
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
        String sql = "UPDATE stocks SET produit_id = ?, quantite = ?, emplacement = ?, lot = ?, date_peremption = ?, date_derniere_maj = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stock.getProduitId());
            stmt.setInt(2, stock.getQuantite());
            stmt.setString(3, stock.getEmplacement());
            stmt.setString(4, stock.getLot());
            stmt.setDate(5, stock.getDatePeremption() != null ? Date.valueOf(stock.getDatePeremption()) : null);
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(7, stock.getId());
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
        String sql = "SELECT SUM(quantite) as total FROM stocks WHERE produit_id = ?";
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
        stock.setQuantite(rs.getInt("quantite"));
        stock.setEmplacement(rs.getString("emplacement"));
        stock.setLot(rs.getString("lot"));
        Date datePeremption = rs.getDate("date_peremption");
        if (datePeremption != null) {
            stock.setDatePeremption(datePeremption.toLocalDate());
        }
        Timestamp dateDerniereMaj = rs.getTimestamp("date_derniere_maj");
        if (dateDerniereMaj != null) {
            stock.setDateDerniereMaj(dateDerniereMaj.toLocalDateTime());
        }
        return stock;
    }
}
