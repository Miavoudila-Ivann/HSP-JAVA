package appli.repository;

import appli.model.DemandeProduit;
import appli.model.MouvementStock;
import appli.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository {

    DemandeProduit createRequest(DemandeProduit demande);

    List<DemandeProduit> listPendingRequests();

    void approveRequest(int demandeId, int gestionnaireId, String commentaire);

    void refuseRequest(int demandeId, int gestionnaireId, String commentaire);

    Stock replenishStock(Stock stock);

    MouvementStock insertMovement(MouvementStock mouvement);

    List<Stock> findAllStocks();

    Optional<Stock> findStockById(int id);

    List<Stock> findStocksByProduit(int produitId);

    int getTotalQuantiteByProduit(int produitId);
}
