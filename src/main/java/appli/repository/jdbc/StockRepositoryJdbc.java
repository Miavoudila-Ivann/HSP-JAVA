package appli.repository.jdbc;

import appli.dao.DemandeProduitDAO;
import appli.dao.MouvementStockDAO;
import appli.dao.StockDAO;
import appli.model.DemandeProduit;
import appli.model.MouvementStock;
import appli.model.Stock;
import appli.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class StockRepositoryJdbc implements StockRepository {

    private final StockDAO stockDAO = new StockDAO();
    private final DemandeProduitDAO demandeProduitDAO = new DemandeProduitDAO();
    private final MouvementStockDAO mouvementStockDAO = new MouvementStockDAO();

    @Override
    public DemandeProduit createRequest(DemandeProduit demande) {
        int id = demandeProduitDAO.insert(demande);
        demande.setId(id);
        return demande;
    }

    @Override
    public List<DemandeProduit> listPendingRequests() {
        return demandeProduitDAO.findEnAttente();
    }

    @Override
    public void approveRequest(int demandeId, int gestionnaireId, String commentaire) {
        demandeProduitDAO.updateStatut(demandeId, DemandeProduit.Statut.VALIDEE,
                gestionnaireId, commentaire, LocalDateTime.now());
    }

    @Override
    public void refuseRequest(int demandeId, int gestionnaireId, String commentaire) {
        demandeProduitDAO.updateStatut(demandeId, DemandeProduit.Statut.REFUSEE,
                gestionnaireId, commentaire, LocalDateTime.now());
    }

    @Override
    public Stock replenishStock(Stock stock) {
        if (stock.getId() == 0) {
            int id = stockDAO.insert(stock);
            stock.setId(id);
        } else {
            stockDAO.update(stock);
        }
        return stock;
    }

    @Override
    public MouvementStock insertMovement(MouvementStock mouvement) {
        int id = mouvementStockDAO.insert(mouvement);
        mouvement.setId(id);
        return mouvement;
    }

    @Override
    public List<Stock> findAllStocks() {
        return stockDAO.findAll();
    }

    @Override
    public Optional<Stock> findStockById(int id) {
        return Optional.ofNullable(stockDAO.findById(id));
    }

    @Override
    public List<Stock> findStocksByProduit(int produitId) {
        return stockDAO.findByProduitId(produitId);
    }

    @Override
    public int getTotalQuantiteByProduit(int produitId) {
        return stockDAO.getTotalQuantiteByProduit(produitId);
    }
}
