package appli.service;

import appli.dao.ChambreDAO;
import appli.dao.DossierPriseEnChargeDAO;
import appli.dao.HospitalisationDAO;
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
 * Tests unitaires pour MedicalService.
 * Verifie les regles metier : securite, etats, capacite chambre, sortie patient.
 */
@ExtendWith(MockitoExtension.class)
class MedicalServiceTest {

    @Mock
    private DossierPriseEnChargeDAO dossierDAO;

    @Mock
    private HospitalisationDAO hospitalisationDAO;

    @Mock
    private ChambreDAO chambreDAO;

    @Mock
    private JournalService journalService;

    private MedicalService medicalService;

    private User medecin;
    private User secretaire;
    private User gestionnaire;

    @BeforeEach
    void setUp() throws Exception {
        medecin = new User("medecin@hsp.fr", "hash", "Leroy", "Pierre", User.Role.MEDECIN);
        medecin.setId(4);
        medecin.setSpecialite("Medecine generale");

        secretaire = new User("secretaire@hsp.fr", "hash", "Martin", "Marie", User.Role.SECRETAIRE);
        secretaire.setId(2);

        gestionnaire = new User("gestionnaire@hsp.fr", "hash", "Fournier", "Michel", User.Role.GESTIONNAIRE);
        gestionnaire.setId(8);

        // Creation manuelle + injection par reflexion (necessaire sur JDK 25 avec modules)
        medicalService = new MedicalService();
        setField(medicalService, "dossierDAO", dossierDAO);
        setField(medicalService, "hospitalisationDAO", hospitalisationDAO);
        setField(medicalService, "chambreDAO", chambreDAO);
        setField(medicalService, "journalService", journalService);
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

    // ==================== HOSPITALISATION ====================

    @Test
    @DisplayName("Hospitaliser - echec sans utilisateur connecte")
    void hospitaliser_echecSansConnexion() {
        SessionManager.getInstance().logout();

        assertThrows(IllegalStateException.class, () ->
                medicalService.hospitaliser(1, 15, "Motif", "Diag", LocalDate.now().plusDays(5))
        );
    }

    @Test
    @DisplayName("Hospitaliser - echec avec role secretaire (non autorise)")
    void hospitaliser_echecRoleSecretaire() {
        SessionManager.getInstance().login(secretaire);

        assertThrows(SecurityException.class, () ->
                medicalService.hospitaliser(1, 15, "Motif", "Diag", LocalDate.now().plusDays(5))
        );
    }

    @Test
    @DisplayName("Hospitaliser - echec avec role gestionnaire (non autorise)")
    void hospitaliser_echecRoleGestionnaire() {
        SessionManager.getInstance().login(gestionnaire);

        assertThrows(SecurityException.class, () ->
                medicalService.hospitaliser(1, 15, "Motif", "Diag", LocalDate.now().plusDays(5))
        );
    }

    // ==================== CLOTURE DOSSIER ====================

    @Test
    @DisplayName("Cloturer dossier - echec sans connexion")
    void cloturerDossier_echecSansConnexion() {
        SessionManager.getInstance().logout();

        assertThrows(IllegalStateException.class, () ->
                medicalService.cloturerDossier(1, "Notes", DossierPriseEnCharge.DestinationSortie.DOMICILE)
        );
    }

    @Test
    @DisplayName("Cloturer dossier - echec role non autorise")
    void cloturerDossier_echecRoleNonAutorise() {
        SessionManager.getInstance().login(gestionnaire);

        assertThrows(SecurityException.class, () ->
                medicalService.cloturerDossier(1, "Notes", DossierPriseEnCharge.DestinationSortie.DOMICILE)
        );
    }

    @Test
    @DisplayName("Cloturer dossier - echec dossier inexistant")
    void cloturerDossier_echecDossierInexistant() {
        SessionManager.getInstance().login(medecin);

        when(dossierDAO.findById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                medicalService.cloturerDossier(999, "Notes", DossierPriseEnCharge.DestinationSortie.DOMICILE)
        );
    }

    @Test
    @DisplayName("Cloturer dossier - echec dossier deja cloture")
    void cloturerDossier_echecDejaCloture() {
        SessionManager.getInstance().login(medecin);

        DossierPriseEnCharge dossier = new DossierPriseEnCharge();
        dossier.setId(1);
        dossier.setStatut(DossierPriseEnCharge.Statut.TERMINE);
        when(dossierDAO.findById(1)).thenReturn(dossier);

        assertThrows(IllegalStateException.class, () ->
                medicalService.cloturerDossier(1, "Notes", DossierPriseEnCharge.DestinationSortie.DOMICILE)
        );
    }

    @Test
    @DisplayName("Cloturer dossier - succes avec medecin")
    void cloturerDossier_succes() {
        SessionManager.getInstance().login(medecin);

        DossierPriseEnCharge dossier = new DossierPriseEnCharge();
        dossier.setId(1);
        dossier.setNumeroDossier("DPC-20240115-0001");
        dossier.setStatut(DossierPriseEnCharge.Statut.EN_COURS);
        when(dossierDAO.findById(1)).thenReturn(dossier);

        DossierPriseEnCharge result = medicalService.cloturerDossier(
                1, "Amelioration clinique", DossierPriseEnCharge.DestinationSortie.DOMICILE
        );

        assertEquals(DossierPriseEnCharge.Statut.TERMINE, result.getStatut());
        assertEquals(DossierPriseEnCharge.DestinationSortie.DOMICILE, result.getDestinationSortie());
        assertEquals("Amelioration clinique", result.getNotesCloture());
        assertNotNull(result.getDateCloture());
        verify(dossierDAO).update(dossier);
    }

    // ==================== SORTIE PATIENT ====================

    @Test
    @DisplayName("Sortie patient - echec sans utilisateur connecte")
    void sortiePatient_echecSansConnexion() {
        SessionManager.getInstance().logout();

        assertThrows(IllegalStateException.class, () ->
                medicalService.sortiePatient(1, "Guerison", Hospitalisation.TypeSortie.GUERISON, "RAS")
        );
    }

    @Test
    @DisplayName("Sortie patient - echec role non autorise")
    void sortiePatient_echecRoleNonAutorise() {
        SessionManager.getInstance().login(secretaire);

        assertThrows(SecurityException.class, () ->
                medicalService.sortiePatient(1, "Guerison", Hospitalisation.TypeSortie.GUERISON, "RAS")
        );
    }

    // ==================== MODELES ====================

    @Test
    @DisplayName("Modele Chambre - verification lits disponibles")
    void chambre_litsDisponibles() {
        Chambre chambre = new Chambre("301", 3, Chambre.TypeChambre.SOINS_INTENSIFS, 2);
        chambre.setNbLitsOccupes(0);

        assertTrue(chambre.hasLitDisponible());
        assertEquals(2, chambre.getLitsDisponibles());

        chambre.setNbLitsOccupes(1);
        assertTrue(chambre.hasLitDisponible());
        assertEquals(1, chambre.getLitsDisponibles());

        chambre.setNbLitsOccupes(2);
        assertFalse(chambre.hasLitDisponible());
        assertEquals(0, chambre.getLitsDisponibles());
    }

    @Test
    @DisplayName("Modele Chambre - indisponible si en maintenance")
    void chambre_enMaintenance() {
        Chambre chambre = new Chambre("101", 1, Chambre.TypeChambre.SIMPLE, 1);
        chambre.setEnMaintenance(true);

        assertFalse(chambre.hasLitDisponible());
    }

    @Test
    @DisplayName("Modele Chambre - indisponible si inactive")
    void chambre_inactive() {
        Chambre chambre = new Chambre("101", 1, Chambre.TypeChambre.SIMPLE, 1);
        chambre.setActif(false);

        assertFalse(chambre.hasLitDisponible());
    }

    @Test
    @DisplayName("Modele Hospitalisation - statut initial EN_COURS")
    void hospitalisation_statutInitial() {
        Hospitalisation hospitalisation = new Hospitalisation();
        assertEquals(Hospitalisation.Statut.EN_COURS, hospitalisation.getStatut());
        assertNotNull(hospitalisation.getDateEntree());
    }

    @Test
    @DisplayName("Modele DossierPriseEnCharge - statut initial EN_ATTENTE")
    void dossier_statutInitial() {
        DossierPriseEnCharge dossier = new DossierPriseEnCharge();
        assertEquals(DossierPriseEnCharge.Statut.EN_ATTENTE, dossier.getStatut());
    }

    // ==================== SESSION MANAGER ====================

    @Test
    @DisplayName("SessionManager - verification des roles")
    void sessionManager_roles() {
        SessionManager sm = SessionManager.getInstance();

        sm.login(medecin);
        assertTrue(sm.isMedecin());
        assertFalse(sm.isAdmin());
        assertFalse(sm.isSecretaire());
        assertFalse(sm.isGestionnaire());

        sm.login(secretaire);
        assertTrue(sm.isSecretaire());
        assertFalse(sm.isMedecin());

        sm.login(gestionnaire);
        assertTrue(sm.isGestionnaire());
        assertFalse(sm.isMedecin());

        sm.logout();
        assertFalse(sm.isLoggedIn());
        assertNull(sm.getCurrentUser());
    }
}
