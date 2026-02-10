package appli.service;

import appli.dao.DemandeProduitDAO;
import appli.dao.MouvementStockDAO;
import appli.dao.ProduitDAO;
import appli.model.*;
import appli.security.SessionManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour StockService.
 * Verifie les regles metier : securite, validation/refus demandes, etats.
 */
@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private ProduitDAO produitDAO;

    @Mock
    private DemandeProduitDAO demandeProduitDAO;

    @Mock
    private MouvementStockDAO mouvementStockDAO;

    @Mock
    private JournalService journalService;

    private StockService stockService;

    private User medecin;
    private User gestionnaire;
    private User secretaire;

    @BeforeEach
    void setUp() throws Exception {
        medecin = new User("medecin@hsp.fr", "hash", "Leroy", "Pierre", User.Role.MEDECIN);
        medecin.setId(4);

        gestionnaire = new User("gestionnaire@hsp.fr", "hash", "Fournier", "Michel", User.Role.GESTIONNAIRE);
        gestionnaire.setId(8);

        secretaire = new User("secretaire@hsp.fr", "hash", "Martin", "Marie", User.Role.SECRETAIRE);
        secretaire.setId(2);

        // Creation manuelle + injection par reflexion (necessaire sur JDK 25 avec modules)
        stockService = new StockService();
        setField(stockService, "produitDAO", produitDAO);
        setField(stockService, "demandeProduitDAO", demandeProduitDAO);
        setField(stockService, "mouvementStockDAO", mouvementStockDAO);
        setField(stockService, "journalService", journalService);
    }

    @AfterEach
    void tearDown() {
        SessionManager.getInstance().logout();
    }

    /** Injecte un mock dans un champ prive (meme final) via reflexion. */
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ==================== CREATION DEMANDE ====================

    @Test
    @DisplayName("Creer demande - succes avec medecin et produit existant")
    void creerDemande_succes() {
        SessionManager.getInstance().login(medecin);

        Produit produit = new Produit("MED-001", "Paracetamol 500mg", "boite 16");
        produit.setId(1);
        when(produitDAO.findById(1)).thenReturn(produit);
        when(demandeProduitDAO.insert(any(DemandeProduit.class))).thenReturn(1);

        DemandeProduit result = stockService.creerDemandeProduit(
                1, 50, 1, 1, LocalDate.now(), false, "Reapprovisionnement"
        );

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getProduitId());
        assertEquals(50, result.getQuantiteDemandee());
        assertEquals(DemandeProduit.Statut.EN_ATTENTE, result.getStatut());
        assertEquals(medecin.getId(), result.getMedecinId());
        assertFalse(result.isUrgence());
        assertEquals(5, result.getPriorite());
        assertNotNull(result.getNumeroDemande());
        assertTrue(result.getNumeroDemande().startsWith("DEM-"));
    }

    @Test
    @DisplayName("Creer demande urgente - priorite 1")
    void creerDemande_urgente() {
        SessionManager.getInstance().login(medecin);

        Produit produit = new Produit("MED-005", "Morphine 10mg", "ampoule");
        produit.setId(5);
        when(produitDAO.findById(5)).thenReturn(produit);
        when(demandeProduitDAO.insert(any(DemandeProduit.class))).thenReturn(2);

        DemandeProduit result = stockService.creerDemandeProduit(
                5, 10, 1, 1, LocalDate.now(), true, "IDM - analgesie urgente"
        );

        assertTrue(result.isUrgence());
        assertEquals(1, result.getPriorite());
    }

    @Test
    @DisplayName("Creer demande - echec sans connexion")
    void creerDemande_echecSansConnexion() {
        SessionManager.getInstance().logout();

        assertThrows(IllegalStateException.class, () ->
                stockService.creerDemandeProduit(1, 50, null, null, LocalDate.now(), false, "Motif")
        );
    }

    @Test
    @DisplayName("Creer demande - echec role gestionnaire (non autorise)")
    void creerDemande_echecRoleGestionnaire() {
        SessionManager.getInstance().login(gestionnaire);

        assertThrows(SecurityException.class, () ->
                stockService.creerDemandeProduit(1, 50, null, null, LocalDate.now(), false, "Motif")
        );
    }

    @Test
    @DisplayName("Creer demande - echec role secretaire (non autorise)")
    void creerDemande_echecRoleSecretaire() {
        SessionManager.getInstance().login(secretaire);

        assertThrows(SecurityException.class, () ->
                stockService.creerDemandeProduit(1, 50, null, null, LocalDate.now(), false, "Motif")
        );
    }

    @Test
    @DisplayName("Creer demande - echec produit inexistant")
    void creerDemande_echecProduitInexistant() {
        SessionManager.getInstance().login(medecin);

        when(produitDAO.findById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                stockService.creerDemandeProduit(999, 50, null, null, LocalDate.now(), false, "Motif")
        );
    }

    // ==================== VALIDATION DEMANDE ====================

    @Test
    @DisplayName("Valider demande - echec sans connexion")
    void validerDemande_echecSansConnexion() {
        SessionManager.getInstance().logout();

        assertThrows(IllegalStateException.class, () ->
                stockService.validerDemande(1, "OK")
        );
    }

    @Test
    @DisplayName("Valider demande - echec role medecin (non autorise)")
    void validerDemande_echecRoleMedecin() {
        SessionManager.getInstance().login(medecin);

        assertThrows(SecurityException.class, () ->
                stockService.validerDemande(1, "OK")
        );
    }

    @Test
    @DisplayName("Valider demande - echec role secretaire (non autorise)")
    void validerDemande_echecRoleSecretaire() {
        SessionManager.getInstance().login(secretaire);

        assertThrows(SecurityException.class, () ->
                stockService.validerDemande(1, "OK")
        );
    }

    // ==================== REFUS DEMANDE ====================

    @Test
    @DisplayName("Refuser demande - succes avec gestionnaire")
    void refuserDemande_succes() {
        SessionManager.getInstance().login(gestionnaire);

        DemandeProduit demande = new DemandeProduit();
        demande.setId(3);
        demande.setNumeroDemande("DEM-20240115-0003");
        demande.setStatut(DemandeProduit.Statut.EN_ATTENTE);
        when(demandeProduitDAO.findById(3)).thenReturn(demande);

        DemandeProduit result = stockService.refuserDemande(3, "Rupture de stock");

        assertEquals(DemandeProduit.Statut.REFUSEE, result.getStatut());
        assertEquals(gestionnaire.getId(), result.getGestionnaireId());
        assertEquals("Rupture de stock", result.getCommentaireTraitement());
        assertNotNull(result.getDateTraitement());
        verify(demandeProduitDAO).update(demande);
    }

    @Test
    @DisplayName("Refuser demande - echec sans connexion")
    void refuserDemande_echecSansConnexion() {
        SessionManager.getInstance().logout();

        assertThrows(IllegalStateException.class, () ->
                stockService.refuserDemande(1, "Motif")
        );
    }

    @Test
    @DisplayName("Refuser demande - echec role medecin (non autorise)")
    void refuserDemande_echecRoleMedecin() {
        SessionManager.getInstance().login(medecin);

        assertThrows(SecurityException.class, () ->
                stockService.refuserDemande(1, "Motif")
        );
    }

    @Test
    @DisplayName("Refuser demande - echec demande inexistante")
    void refuserDemande_echecDemandeInexistante() {
        SessionManager.getInstance().login(gestionnaire);

        when(demandeProduitDAO.findById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                stockService.refuserDemande(999, "Motif")
        );
    }

    @Test
    @DisplayName("Refuser demande - echec demande deja validee")
    void refuserDemande_echecDejaValidee() {
        SessionManager.getInstance().login(gestionnaire);

        DemandeProduit demande = new DemandeProduit();
        demande.setId(1);
        demande.setStatut(DemandeProduit.Statut.VALIDEE);
        when(demandeProduitDAO.findById(1)).thenReturn(demande);

        assertThrows(IllegalStateException.class, () ->
                stockService.refuserDemande(1, "Motif")
        );
    }

    // ==================== MODELES ====================

    @Test
    @DisplayName("Modele DemandeProduit - statut initial EN_ATTENTE")
    void demandeProduit_statutInitial() {
        DemandeProduit demande = new DemandeProduit();
        assertEquals(DemandeProduit.Statut.EN_ATTENTE, demande.getStatut());
        assertFalse(demande.isUrgence());
        assertEquals(0, demande.getQuantiteLivree());
    }

    @Test
    @DisplayName("Modele DemandeProduit - livraison complete")
    void demandeProduit_livraisonComplete() {
        DemandeProduit demande = new DemandeProduit(1, 50, 4);
        demande.setQuantiteLivree(50);
        assertTrue(demande.isLivraisonComplete());

        demande.setQuantiteLivree(30);
        assertFalse(demande.isLivraisonComplete());
    }

    @Test
    @DisplayName("Modele Stock - verification peremption")
    void stock_peremption() {
        Stock stock = new Stock(1, 1, "LOT001", 100);

        stock.setDatePeremption(LocalDate.now().minusDays(1));
        assertTrue(stock.isPerime());

        stock.setDatePeremption(LocalDate.now().plusDays(10));
        assertFalse(stock.isPerime());
        assertTrue(stock.isProcheDuPeremption(30));
        assertFalse(stock.isProcheDuPeremption(5));
    }

    @Test
    @DisplayName("Modele Stock - quantite disponible")
    void stock_quantiteDisponible() {
        Stock stock = new Stock(1, 1, "LOT001", 100);
        stock.setQuantiteReservee(20);

        assertEquals(80, stock.getQuantiteDisponible());
    }

    @Test
    @DisplayName("Modele Stock - sans date peremption")
    void stock_sansDatePeremption() {
        Stock stock = new Stock(1, 1, "LOT001", 100);

        assertFalse(stock.isPerime());
        assertFalse(stock.isProcheDuPeremption(30));
        assertEquals(Long.MAX_VALUE, stock.getJoursAvantPeremption());
    }

    @Test
    @DisplayName("Modele Produit - nom complet avec nom commercial")
    void produit_nomComplet() {
        Produit produit = new Produit("MED-001", "Paracetamol 500mg", "boite 16");
        produit.setNomCommercial("Doliprane");

        assertEquals("Paracetamol 500mg (Doliprane)", produit.getNomComplet());
    }

    @Test
    @DisplayName("Modele Produit - nom complet sans nom commercial")
    void produit_nomCompletSansCommercial() {
        Produit produit = new Produit("CONS-001", "Compresses steriles", "sachet 10");

        assertEquals("Compresses steriles", produit.getNomComplet());
    }

    @Test
    @DisplayName("Modele Produit - valeurs par defaut")
    void produit_valeursParDefaut() {
        Produit produit = new Produit();

        assertEquals(Produit.NiveauDangerosite.FAIBLE, produit.getNiveauDangerosite());
        assertTrue(produit.isActif());
        assertFalse(produit.isNecessiteOrdonnance());
        assertFalse(produit.isStupefiant());
        assertEquals(10, produit.getSeuilAlerteStock());
        assertEquals(30, produit.getDatePeremptionAlerteJours());
    }

    // ==================== REAPPROVISIONNEMENT ====================

    @Test
    @DisplayName("Reapprovisionner - echec sans connexion")
    void reapprovisionner_echecSansConnexion() {
        SessionManager.getInstance().logout();

        assertThrows(IllegalStateException.class, () ->
                stockService.reapprovisionner(1, 1, "LOT001", 100,
                        LocalDate.now().plusMonths(12), new java.math.BigDecimal("1.80"), 1, "CMD-001")
        );
    }

    @Test
    @DisplayName("Reapprovisionner - echec role medecin (non autorise)")
    void reapprovisionner_echecRoleMedecin() {
        SessionManager.getInstance().login(medecin);

        assertThrows(SecurityException.class, () ->
                stockService.reapprovisionner(1, 1, "LOT001", 100,
                        LocalDate.now().plusMonths(12), new java.math.BigDecimal("1.80"), 1, "CMD-001")
        );
    }
}
