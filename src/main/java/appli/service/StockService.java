package appli.service;

import appli.dao.*;
import appli.model.*;
import appli.repository.FournisseurRepository;
import appli.repository.ProduitRepository;
import appli.repository.StockRepository;
import appli.security.SessionManager;
import appli.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service metier pour la gestion des stocks de produits medicaux.
 * Gere les produits, fournisseurs, demandes de produits et mouvements de stock.
 * Utilise des transactions pour les operations critiques.
 */
public class StockService {

    private final ProduitRepository produitRepository = new ProduitRepository();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final FournisseurRepository fournisseurRepository = new FournisseurRepository();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final ProduitFournisseurDAO produitFournisseurDAO = new ProduitFournisseurDAO();
    private final StockRepository stockRepository = new StockRepository();
    private final StockDAO stockDAO = new StockDAO();
    private final DemandeProduitDAO demandeProduitDAO = new DemandeProduitDAO();
    private final MouvementStockDAO mouvementStockDAO = new MouvementStockDAO();
    private final JournalService journalService = new JournalService();

    // ==================== CRUD PRODUITS ====================

    /**
     * Recupere un produit par son identifiant.
     */
    public Optional<Produit> getProduitById(int id) {
        return produitRepository.getById(id);
    }

    /**
     * Recupere un produit par son code.
     */
    public Optional<Produit> getProduitByCode(String code) {
        return produitRepository.getByCode(code);
    }

    /**
     * Recupere tous les produits.
     */
    public List<Produit> getAllProduits() {
        return produitRepository.getAll();
    }

    /**
     * Recupere les produits d'une categorie.
     */
    public List<Produit> getProduitsByCategorie(int categorieId) {
        return produitRepository.getByCategorie(categorieId);
    }

    /**
     * Cree un nouveau produit.
     */
    public Produit creerProduit(Produit produit) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut creer un produit");
        }

        Produit savedProduit = produitRepository.save(produit);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.CREATION,
            "Creation produit: " + savedProduit.getNom(),
            "Produit",
            savedProduit.getId()
        );

        return savedProduit;
    }

    /**
     * Met a jour un produit.
     */
    public Produit modifierProduit(Produit produit) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut modifier un produit");
        }

        Produit savedProduit = produitRepository.save(produit);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Modification produit: " + savedProduit.getNom(),
            "Produit",
            savedProduit.getId()
        );

        return savedProduit;
    }

    // ==================== CRUD FOURNISSEURS ====================

    /**
     * Recupere un fournisseur par son identifiant.
     */
    public Optional<Fournisseur> getFournisseurById(int id) {
        return fournisseurRepository.getById(id);
    }

    /**
     * Recupere tous les fournisseurs.
     */
    public List<Fournisseur> getAllFournisseurs() {
        return fournisseurRepository.getAll();
    }

    /**
     * Cree un nouveau fournisseur.
     */
    public Fournisseur creerFournisseur(Fournisseur fournisseur) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut creer un fournisseur");
        }

        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.CREATION,
            "Creation fournisseur: " + savedFournisseur.getNom(),
            "Fournisseur",
            savedFournisseur.getId()
        );

        return savedFournisseur;
    }

    /**
     * Met a jour un fournisseur.
     */
    public Fournisseur modifierFournisseur(Fournisseur fournisseur) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut modifier un fournisseur");
        }

        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Modification fournisseur: " + savedFournisseur.getNom(),
            "Fournisseur",
            savedFournisseur.getId()
        );

        return savedFournisseur;
    }

    // ==================== ASSOCIATION PRODUIT-FOURNISSEUR (PRIX) ====================

    /**
     * Associe un produit a un fournisseur avec un prix d'achat.
     */
    public ProduitFournisseur associerProduitFournisseur(int produitId, int fournisseurId,
                                                          String referenceFournisseur,
                                                          BigDecimal prixAchat,
                                                          int delaiLivraison,
                                                          int quantiteMinimum,
                                                          boolean estPrincipal) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut associer produit et fournisseur");
        }

        ProduitFournisseur pf = new ProduitFournisseur();
        pf.setProduitId(produitId);
        pf.setFournisseurId(fournisseurId);
        pf.setReferenceFournisseur(referenceFournisseur);
        pf.setPrixAchat(prixAchat);
        pf.setDelaiLivraisonJours(delaiLivraison);
        pf.setQuantiteMinimumCommande(quantiteMinimum);
        pf.setEstPrincipal(estPrincipal);
        pf.setActif(true);

        int id = produitFournisseurDAO.insert(pf);
        pf.setId(id);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.CREATION,
            "Association produit " + produitId + " - fournisseur " + fournisseurId + " - prix: " + prixAchat,
            "ProduitFournisseur",
            id
        );

        return pf;
    }

    /**
     * Recupere les fournisseurs d'un produit.
     */
    public List<ProduitFournisseur> getFournisseursByProduit(int produitId) {
        return produitFournisseurDAO.findByProduitId(produitId);
    }

    // ==================== DEMANDE DE PRODUIT (MEDECIN) ====================

    /**
     * Cree une demande de produit par un medecin.
     */
    public DemandeProduit creerDemandeProduit(int produitId, int quantiteDemandee,
                                               Integer dossierId, Integer hospitalisationId,
                                               LocalDate dateBesoin, boolean urgence,
                                               String motif) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isMedecin() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul un medecin peut creer une demande de produit");
        }

        // Verifier que le produit existe
        Produit produit = produitDAO.findById(produitId);
        if (produit == null) {
            throw new IllegalArgumentException("Produit non trouve");
        }

        DemandeProduit demande = new DemandeProduit();
        demande.setNumeroDemande(genererNumeroDemande());
        demande.setProduitId(produitId);
        demande.setQuantiteDemandee(quantiteDemandee);
        demande.setMedecinId(currentUser.getId());
        demande.setDossierId(dossierId);
        demande.setHospitalisationId(hospitalisationId);
        demande.setDateDemande(LocalDateTime.now());
        demande.setDateBesoin(dateBesoin);
        demande.setUrgence(urgence);
        demande.setPriorite(urgence ? 1 : 5);
        demande.setMotif(motif);
        demande.setStatut(DemandeProduit.Statut.EN_ATTENTE);

        int id = demandeProduitDAO.insert(demande);
        demande.setId(id);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.CREATION,
            "Demande produit: " + demande.getNumeroDemande() +
            " - " + produit.getNom() + " x" + quantiteDemandee +
            (urgence ? " (URGENT)" : ""),
            "DemandeProduit",
            id
        );

        return demande;
    }

    /**
     * Recupere les demandes en attente.
     */
    public List<DemandeProduit> getDemandesEnAttente() {
        return demandeProduitDAO.findEnAttente();
    }

    /**
     * Recupere les demandes d'un medecin.
     */
    public List<DemandeProduit> getDemandesByMedecin(int medecinId) {
        return demandeProduitDAO.findByMedecinId(medecinId);
    }

    /**
     * Recupere une demande par son identifiant.
     */
    public Optional<DemandeProduit> getDemandeById(int id) {
        return Optional.ofNullable(demandeProduitDAO.findById(id));
    }

    // ==================== VALIDATION/REFUS DEMANDE (GESTIONNAIRE - TRANSACTIONNEL) ====================

    /**
     * Valide une demande de produit de maniere transactionnelle.
     * 1. Verrouille le stock avec SELECT FOR UPDATE
     * 2. Verifie la disponibilite
     * 3. Decremente le stock
     * 4. Cree le mouvement de stock
     * 5. Met a jour la demande
     */
    public DemandeProduit validerDemande(int demandeId, String commentaire) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut valider une demande");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Recuperer la demande
            DemandeProduit demande = demandeProduitDAO.findById(demandeId);
            if (demande == null) {
                throw new IllegalArgumentException("Demande non trouvee");
            }

            if (demande.getStatut() != DemandeProduit.Statut.EN_ATTENTE) {
                throw new IllegalStateException("La demande n'est pas en attente");
            }

            // 2. Verrouiller les stocks du produit avec SELECT FOR UPDATE
            String lockSql = "SELECT * FROM stocks WHERE produit_id = ? AND quantite > 0 ORDER BY date_peremption FOR UPDATE";
            int quantiteRestante = demande.getQuantiteDemandee();
            int quantiteLivree = 0;

            try (PreparedStatement stmt = conn.prepareStatement(lockSql)) {
                stmt.setInt(1, demande.getProduitId());
                ResultSet rs = stmt.executeQuery();

                while (rs.next() && quantiteRestante > 0) {
                    int stockId = rs.getInt("id");
                    int quantiteStock = rs.getInt("quantite");
                    int emplacementId = rs.getInt("emplacement_id");

                    int quantiteARetirer = Math.min(quantiteStock, quantiteRestante);

                    // 3. Decrementer le stock
                    String updateStockSql = "UPDATE stocks SET quantite = quantite - ?, date_derniere_maj = NOW() WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
                        updateStmt.setInt(1, quantiteARetirer);
                        updateStmt.setInt(2, stockId);
                        updateStmt.executeUpdate();
                    }

                    // 4. Creer le mouvement de stock (SORTIE)
                    MouvementStock mouvement = new MouvementStock();
                    mouvement.setStockId(stockId);
                    mouvement.setProduitId(demande.getProduitId());
                    mouvement.setTypeMouvement(MouvementStock.TypeMouvement.SORTIE);
                    mouvement.setQuantite(quantiteARetirer);
                    mouvement.setQuantiteAvant(quantiteStock);
                    mouvement.setQuantiteApres(quantiteStock - quantiteARetirer);
                    mouvement.setMotif("Demande " + demande.getNumeroDemande());
                    mouvement.setReferenceDocument(demande.getNumeroDemande());
                    mouvement.setEmplacementSourceId(emplacementId);
                    mouvement.setDossierId(demande.getDossierId());
                    mouvement.setUserId(currentUser.getId());
                    mouvement.setDateMouvement(LocalDateTime.now());
                    mouvement.setValide(true);
                    mouvement.setDateValidation(LocalDateTime.now());
                    mouvement.setValidateurId(currentUser.getId());

                    mouvementStockDAO.insert(mouvement);

                    quantiteRestante -= quantiteARetirer;
                    quantiteLivree += quantiteARetirer;
                }
            }

            // 5. Mettre a jour la demande
            demande.setStatut(DemandeProduit.Statut.VALIDEE);
            demande.setGestionnaireId(currentUser.getId());
            demande.setDateTraitement(LocalDateTime.now());
            demande.setCommentaireTraitement(commentaire);
            demande.setQuantiteLivree(quantiteLivree);

            // Si toute la quantite n'a pas pu etre livree
            if (quantiteLivree < demande.getQuantiteDemandee()) {
                demande.setCommentaireTraitement(
                    (commentaire != null ? commentaire + " - " : "") +
                    "Quantite partielle livree: " + quantiteLivree + "/" + demande.getQuantiteDemandee()
                );
            }

            demandeProduitDAO.update(demande);

            conn.commit();

            journalService.logAction(
                currentUser,
                JournalAction.TypeAction.MODIFICATION,
                "Validation demande: " + demande.getNumeroDemande() +
                " - Livre: " + quantiteLivree + "/" + demande.getQuantiteDemandee(),
                "DemandeProduit",
                demandeId
            );

            return demande;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Erreur lors de la validation: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Erreur lors du reset autocommit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Refuse une demande de produit.
     */
    public DemandeProduit refuserDemande(int demandeId, String motifRefus) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut refuser une demande");
        }

        DemandeProduit demande = demandeProduitDAO.findById(demandeId);
        if (demande == null) {
            throw new IllegalArgumentException("Demande non trouvee");
        }

        if (demande.getStatut() != DemandeProduit.Statut.EN_ATTENTE) {
            throw new IllegalStateException("La demande n'est pas en attente");
        }

        demande.setStatut(DemandeProduit.Statut.REFUSEE);
        demande.setGestionnaireId(currentUser.getId());
        demande.setDateTraitement(LocalDateTime.now());
        demande.setCommentaireTraitement(motifRefus);

        demandeProduitDAO.update(demande);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Refus demande: " + demande.getNumeroDemande() + " - Motif: " + motifRefus,
            "DemandeProduit",
            demandeId
        );

        return demande;
    }

    // ==================== REAPPROVISIONNEMENT (TRANSACTIONNEL) ====================

    /**
     * Reapprovisionne un stock de maniere transactionnelle.
     * Cree une entree de stock et enregistre le mouvement.
     */
    public Stock reapprovisionner(int produitId, int emplacementId, String lot,
                                   int quantite, LocalDate datePeremption,
                                   BigDecimal prixUnitaire, Integer fournisseurId,
                                   String numeroCommande) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isGestionnaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul le gestionnaire de stock peut reapprovisionner");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Verifier si un stock existe deja pour ce produit/emplacement/lot
            String checkSql = "SELECT * FROM stocks WHERE produit_id = ? AND emplacement_id = ? AND lot = ? FOR UPDATE";
            Stock stock = null;
            int quantiteAvant = 0;

            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setInt(1, produitId);
                stmt.setInt(2, emplacementId);
                stmt.setString(3, lot);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    stock = new Stock();
                    stock.setId(rs.getInt("id"));
                    stock.setProduitId(rs.getInt("produit_id"));
                    stock.setEmplacementId(rs.getInt("emplacement_id"));
                    stock.setLot(rs.getString("lot"));
                    stock.setQuantite(rs.getInt("quantite"));
                    quantiteAvant = stock.getQuantite();
                }
            }

            int stockId;
            if (stock != null) {
                // Mettre a jour le stock existant
                String updateSql = "UPDATE stocks SET quantite = quantite + ?, date_peremption = ?, " +
                        "prix_unitaire_achat = ?, fournisseur_id = ?, numero_commande = ?, date_derniere_maj = NOW() WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, quantite);
                    stmt.setObject(2, datePeremption);
                    stmt.setBigDecimal(3, prixUnitaire);
                    stmt.setObject(4, fournisseurId);
                    stmt.setString(5, numeroCommande);
                    stmt.setInt(6, stock.getId());
                    stmt.executeUpdate();
                }
                stockId = stock.getId();
                stock.setQuantite(quantiteAvant + quantite);
            } else {
                // Creer un nouveau stock
                stock = new Stock(produitId, emplacementId, lot, quantite);
                stock.setDatePeremption(datePeremption);
                stock.setPrixUnitaireAchat(prixUnitaire);
                stock.setFournisseurId(fournisseurId);
                stock.setNumeroCommande(numeroCommande);
                stock.setDateReception(LocalDateTime.now());

                stockId = stockDAO.insert(stock);
                stock.setId(stockId);
            }

            // Creer le mouvement de stock (ENTREE)
            MouvementStock mouvement = new MouvementStock();
            mouvement.setStockId(stockId);
            mouvement.setProduitId(produitId);
            mouvement.setTypeMouvement(MouvementStock.TypeMouvement.ENTREE);
            mouvement.setQuantite(quantite);
            mouvement.setQuantiteAvant(quantiteAvant);
            mouvement.setQuantiteApres(quantiteAvant + quantite);
            mouvement.setMotif("Reapprovisionnement");
            mouvement.setReferenceDocument(numeroCommande);
            mouvement.setEmplacementDestinationId(emplacementId);
            mouvement.setUserId(currentUser.getId());
            mouvement.setDateMouvement(LocalDateTime.now());
            mouvement.setValide(true);
            mouvement.setDateValidation(LocalDateTime.now());
            mouvement.setValidateurId(currentUser.getId());

            mouvementStockDAO.insert(mouvement);

            conn.commit();

            journalService.logAction(
                currentUser,
                JournalAction.TypeAction.CREATION,
                "Reapprovisionnement produit " + produitId + " - Lot: " + lot + " - Quantite: " + quantite,
                "Stock",
                stockId
            );

            return stock;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Erreur lors du reapprovisionnement: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Erreur lors du reset autocommit: " + e.getMessage());
                }
            }
        }
    }

    // ==================== CONSULTATION STOCKS ====================

    /**
     * Recupere tous les stocks.
     */
    public List<Stock> getAllStocks() {
        return stockRepository.getAll();
    }

    /**
     * Recupere les stocks d'un produit.
     */
    public List<Stock> getStocksByProduit(int produitId) {
        return stockDAO.findByProduitId(produitId);
    }

    /**
     * Recupere la quantite totale en stock d'un produit.
     */
    public int getQuantiteTotale(int produitId) {
        return stockDAO.getTotalQuantiteByProduit(produitId);
    }

    /**
     * Recupere les stocks proches de la peremption.
     */
    public List<Stock> getStocksProchesPeremption(int joursAlerte) {
        return stockDAO.findExpiringBefore(joursAlerte);
    }

    /**
     * Recupere les produits en rupture ou stock bas.
     */
    public List<Produit> getProduitsStockBas() {
        List<Produit> produits = produitRepository.getAll();
        return produits.stream()
            .filter(p -> {
                int quantite = stockDAO.getTotalQuantiteByProduit(p.getId());
                return quantite <= p.getSeuilAlerteStock();
            })
            .toList();
    }

    // ==================== MOUVEMENTS DE STOCK ====================

    /**
     * Recupere les mouvements d'un produit.
     */
    public List<MouvementStock> getMouvementsByProduit(int produitId) {
        return mouvementStockDAO.findByProduitId(produitId);
    }

    /**
     * Recupere les mouvements sur une periode.
     */
    public List<MouvementStock> getMouvementsByPeriode(LocalDateTime debut, LocalDateTime fin) {
        return mouvementStockDAO.findByDateRange(debut, fin);
    }

    // ==================== UTILITAIRES ====================

    private String genererNumeroDemande() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        return "DEM-" + datePart + "-" + randomPart;
    }
}
