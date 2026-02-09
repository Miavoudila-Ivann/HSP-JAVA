package appli.repository;

import appli.dao.StockDAO;
import appli.model.Stock;

import java.util.List;
import java.util.Optional;

public class StockRepository {

    private final StockDAO stockDAO = new StockDAO();

    public Optional<Stock> getById(int id) {
        return Optional.ofNullable(stockDAO.findById(id));
    }

    public List<Stock> getByProduitId(int produitId) {
        return stockDAO.findByProduitId(produitId);
    }

    public List<Stock> getAll() {
        return stockDAO.findAll();
    }

    public List<Stock> getExpiringBefore(int days) {
        return stockDAO.findExpiringBefore(days);
    }

    public int getTotalQuantiteByProduit(int produitId) {
        return stockDAO.getTotalQuantiteByProduit(produitId);
    }

    public Stock save(Stock stock) {
        if (stock.getId() == 0) {
            int id = stockDAO.insert(stock);
            stock.setId(id);
        } else {
            stockDAO.update(stock);
        }
        return stock;
    }

    public void updateQuantite(int stockId, int quantite) {
        stockDAO.updateQuantite(stockId, quantite);
    }

    public void delete(int id) {
        stockDAO.delete(id);
    }
}
