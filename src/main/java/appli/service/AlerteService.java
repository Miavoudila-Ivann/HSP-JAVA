package appli.service;

import appli.dao.AlerteDAO;
import appli.dao.StockDAO;
import appli.model.Alerte;
import appli.model.Alerte.Niveau;
import appli.model.Alerte.TypeAlerte;
import appli.model.Produit;
import appli.model.Stock;
import appli.model.JournalAction;
import appli.model.User;
import appli.security.SessionManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AlerteService {

    private final AlerteDAO alerteDAO = new AlerteDAO();
    private final StockService stockService = new StockService();
    private final JournalService journalService = new JournalService();

    public record CompteursAlertes(int critiques, int warnings, int infos, int total) {}

    public void verifierEtGenererAlertes() {
        // 1. Verifier les produits en stock bas / rupture
        List<Produit> produitsStockBas = stockService.getProduitsStockBas();
        for (Produit produit : produitsStockBas) {
            int quantite = stockService.getQuantiteTotale(produit.getId());

            if (quantite == 0) {
                if (!alerteDAO.existsNonResolue(TypeAlerte.RUPTURE, "produits", produit.getId())) {
                    Alerte alerte = new Alerte();
                    alerte.setTypeAlerte(TypeAlerte.RUPTURE);
                    alerte.setNiveau(Niveau.CRITICAL);
                    alerte.setTitre("Rupture de stock : " + produit.getNom());
                    alerte.setMessage("Le produit " + produit.getNom() + " (" + produit.getCode() +
                            ") est en rupture de stock totale.");
                    alerte.setEntite("produits");
                    alerte.setEntiteId(produit.getId());
                    alerteDAO.insert(alerte);
                }
            } else if (quantite <= produit.getSeuilAlerteStock()) {
                if (!alerteDAO.existsNonResolue(TypeAlerte.STOCK_BAS, "produits", produit.getId())) {
                    Alerte alerte = new Alerte();
                    alerte.setTypeAlerte(TypeAlerte.STOCK_BAS);
                    alerte.setNiveau(Niveau.WARNING);
                    alerte.setTitre("Stock bas : " + produit.getNom());
                    alerte.setMessage("Le produit " + produit.getNom() + " (" + produit.getCode() +
                            ") n'a plus que " + quantite + " unite(s) en stock (seuil : " +
                            produit.getSeuilAlerteStock() + ").");
                    alerte.setEntite("produits");
                    alerte.setEntiteId(produit.getId());
                    alerteDAO.insert(alerte);
                }
            }
        }

        // 2. Verifier les stocks proches de la peremption
        List<Stock> stocksPeremption = stockService.getStocksProchesPeremption(30);
        for (Stock stock : stocksPeremption) {
            if (!alerteDAO.existsNonResolue(TypeAlerte.PEREMPTION, "produits", stock.getProduitId())) {
                long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), stock.getDatePeremption());

                Alerte alerte = new Alerte();
                alerte.setTypeAlerte(TypeAlerte.PEREMPTION);
                alerte.setEntite("produits");
                alerte.setEntiteId(stock.getProduitId());

                if (joursRestants <= 0) {
                    alerte.setNiveau(Niveau.CRITICAL);
                    alerte.setTitre("Produit perime - Lot " + stock.getLot());
                    alerte.setMessage("Le lot " + stock.getLot() + " (produit ID " + stock.getProduitId() +
                            ") est perime depuis " + Math.abs(joursRestants) + " jour(s).");
                } else {
                    alerte.setNiveau(Niveau.WARNING);
                    alerte.setTitre("Peremption proche - Lot " + stock.getLot());
                    alerte.setMessage("Le lot " + stock.getLot() + " (produit ID " + stock.getProduitId() +
                            ") expire dans " + joursRestants + " jour(s) (le " + stock.getDatePeremption() + ").");
                }

                alerteDAO.insert(alerte);
            }
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            journalService.logAction(
                    currentUser,
                    JournalAction.TypeAction.CONSULTATION,
                    "Verification et generation des alertes de stock",
                    "Alerte",
                    null
            );
        }
    }

    public List<Alerte> getAlertesActives() {
        return alerteDAO.findNonResolues();
    }

    public CompteursAlertes getCompteursAlertes() {
        int critiques = alerteDAO.countByNiveau(Niveau.CRITICAL);
        int warnings = alerteDAO.countByNiveau(Niveau.WARNING);
        int infos = alerteDAO.countByNiveau(Niveau.INFO);
        int total = critiques + warnings + infos;
        return new CompteursAlertes(critiques, warnings, infos, total);
    }

    public void marquerCommeLue(int alerteId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            alerteDAO.marquerCommeLue(alerteId, currentUser.getId());
        }
    }

    public void resoudreAlerte(int alerteId, String notes) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            alerteDAO.resoudre(alerteId, currentUser.getId(), notes);
            journalService.logAction(
                    currentUser,
                    JournalAction.TypeAction.MODIFICATION,
                    "Resolution alerte #" + alerteId + (notes != null ? " - " + notes : ""),
                    "Alerte",
                    alerteId
            );
        }
    }

    public void resoudreToutesParType(TypeAlerte type) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        List<Alerte> alertes = alerteDAO.findByType(type);
        for (Alerte alerte : alertes) {
            if (!alerte.isResolue()) {
                alerteDAO.resoudre(alerte.getId(), currentUser.getId(), "Resolution groupee");
            }
        }

        journalService.logAction(
                currentUser,
                JournalAction.TypeAction.MODIFICATION,
                "Resolution groupee des alertes de type " + type.getLibelle(),
                "Alerte",
                null
        );
    }

    public int countNonResolues() {
        return alerteDAO.countNonResolues();
    }
}
