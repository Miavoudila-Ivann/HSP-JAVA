package appli.service;

import appli.dao.ChambreDAO;
import appli.dao.DossierPriseEnChargeDAO;
import appli.dao.HospitalisationDAO;
import appli.dao.OrdonnanceDAO;
import appli.dao.LigneOrdonnanceDAO;
import appli.model.*;
import appli.security.SessionManager;
import appli.util.DBConnection;

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
 * Service metier pour les traitements medicaux.
 * Gere les ordonnances, les hospitalisations et les sorties.
 * Utilise des transactions pour les operations critiques.
 */
public class MedicalService {

    private final DossierPriseEnChargeDAO dossierDAO = new DossierPriseEnChargeDAO();
    private final OrdonnanceDAO ordonnanceDAO = new OrdonnanceDAO();
    private final LigneOrdonnanceDAO ligneOrdonnanceDAO = new LigneOrdonnanceDAO();
    private final HospitalisationDAO hospitalisationDAO = new HospitalisationDAO();
    private final ChambreDAO chambreDAO = new ChambreDAO();
    private final JournalService journalService = new JournalService();

    // ==================== ORDONNANCES ====================

    /**
     * Cree une ordonnance pour un dossier de prise en charge.
     * Le medecin prescrit des medicaments au patient.
     */
    public Ordonnance creerOrdonnance(int dossierId, int medecinId, String notes, LocalDate dateFin) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isMedecin() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul un medecin peut creer une ordonnance");
        }

        DossierPriseEnCharge dossier = dossierDAO.findById(dossierId);
        if (dossier == null) {
            throw new IllegalArgumentException("Dossier non trouve");
        }

        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setNumeroOrdonnance(genererNumeroOrdonnance());
        ordonnance.setDossierId(dossierId);
        ordonnance.setMedecinId(medecinId);
        ordonnance.setDatePrescription(LocalDateTime.now());
        ordonnance.setDateDebut(LocalDate.now());
        ordonnance.setDateFin(dateFin);
        ordonnance.setNotes(notes);
        ordonnance.setStatut(Ordonnance.Statut.ACTIVE);

        int id = ordonnanceDAO.insert(ordonnance);
        ordonnance.setId(id);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.CREATION,
            "Creation ordonnance: " + ordonnance.getNumeroOrdonnance() + " pour dossier " + dossier.getNumeroDossier(),
            "Ordonnance",
            id
        );

        return ordonnance;
    }

    /**
     * Ajoute une ligne a une ordonnance.
     */
    public LigneOrdonnance ajouterLigneOrdonnance(int ordonnanceId, int produitId, String posologie,
                                                   int quantite, int dureeJours,
                                                   LigneOrdonnance.VoieAdministration voieAdministration,
                                                   String instructions) {
        Ordonnance ordonnance = ordonnanceDAO.findById(ordonnanceId);
        if (ordonnance == null) {
            throw new IllegalArgumentException("Ordonnance non trouvee");
        }

        if (ordonnance.getStatut() != Ordonnance.Statut.ACTIVE) {
            throw new IllegalStateException("L'ordonnance n'est pas active");
        }

        LigneOrdonnance ligne = new LigneOrdonnance();
        ligne.setOrdonnanceId(ordonnanceId);
        ligne.setProduitId(produitId);
        ligne.setPosologie(posologie);
        ligne.setQuantite(quantite);
        ligne.setDureeJours(dureeJours);
        ligne.setVoieAdministration(voieAdministration);
        ligne.setInstructions(instructions);
        ligne.setDateDebut(LocalDate.now());
        if (dureeJours > 0) {
            ligne.setDateFin(LocalDate.now().plusDays(dureeJours));
        }

        int id = ligneOrdonnanceDAO.insert(ligne);
        ligne.setId(id);

        return ligne;
    }

    /**
     * Recupere une ordonnance par son identifiant.
     */
    public Optional<Ordonnance> getOrdonnanceById(int id) {
        return Optional.ofNullable(ordonnanceDAO.findById(id));
    }

    /**
     * Recupere les ordonnances d'un dossier.
     */
    public List<Ordonnance> getOrdonnancesByDossierId(int dossierId) {
        return ordonnanceDAO.findByDossierId(dossierId);
    }

    /**
     * Termine une ordonnance.
     */
    public Ordonnance terminerOrdonnance(int ordonnanceId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        Ordonnance ordonnance = ordonnanceDAO.findById(ordonnanceId);
        if (ordonnance == null) {
            throw new IllegalArgumentException("Ordonnance non trouvee");
        }

        ordonnance.setStatut(Ordonnance.Statut.TERMINEE);
        ordonnance.setDateFin(LocalDate.now());
        ordonnanceDAO.update(ordonnance);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Cloture ordonnance: " + ordonnance.getNumeroOrdonnance(),
            "Ordonnance",
            ordonnanceId
        );

        return ordonnance;
    }

    // ==================== CLOTURE DOSSIER ====================

    /**
     * Cloture un dossier de prise en charge avec sortie vers domicile.
     * Le patient repart avec son ordonnance.
     */
    public DossierPriseEnCharge cloturerDossier(int dossierId, String notesCloture,
                                                 DossierPriseEnCharge.DestinationSortie destination) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isMedecin() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul un medecin peut cloturer un dossier");
        }

        DossierPriseEnCharge dossier = dossierDAO.findById(dossierId);
        if (dossier == null) {
            throw new IllegalArgumentException("Dossier non trouve");
        }

        if (dossier.getStatut() == DossierPriseEnCharge.Statut.TERMINE) {
            throw new IllegalStateException("Le dossier est deja cloture");
        }

        dossier.setStatut(DossierPriseEnCharge.Statut.TERMINE);
        dossier.setDateCloture(LocalDateTime.now());
        dossier.setNotesCloture(notesCloture);
        dossier.setDestinationSortie(destination);

        dossierDAO.update(dossier);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Cloture dossier: " + dossier.getNumeroDossier() + " - Destination: " + destination.getLibelle(),
            "DossierPriseEnCharge",
            dossierId
        );

        return dossier;
    }

    // ==================== HOSPITALISATION (TRANSACTIONNEL) ====================

    /**
     * Hospitalise un patient de maniere transactionnelle.
     * 1. Verrouille la chambre avec SELECT FOR UPDATE
     * 2. Verifie la disponibilite
     * 3. Cree l'hospitalisation
     * 4. Incremente le nombre de lits occupes
     * 5. Met a jour le statut du dossier
     */
    public Hospitalisation hospitaliser(int dossierId, int chambreId, String motif,
                                         String diagnosticEntree, LocalDate dateSortiePrevue) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isMedecin() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul un medecin peut hospitaliser un patient");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Verifier le dossier
            DossierPriseEnCharge dossier = dossierDAO.findById(dossierId);
            if (dossier == null) {
                throw new IllegalArgumentException("Dossier non trouve");
            }

            // 2. Verrouiller et verifier la chambre avec SELECT FOR UPDATE
            String lockSql = "SELECT * FROM chambres WHERE id = ? FOR UPDATE";
            Chambre chambre = null;
            try (PreparedStatement stmt = conn.prepareStatement(lockSql)) {
                stmt.setInt(1, chambreId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    chambre = new Chambre();
                    chambre.setId(rs.getInt("id"));
                    chambre.setNumero(rs.getString("numero"));
                    chambre.setCapacite(rs.getInt("capacite"));
                    chambre.setNbLitsOccupes(rs.getInt("nb_lits_occupes"));
                    chambre.setActif(rs.getBoolean("actif"));
                    chambre.setEnMaintenance(rs.getBoolean("en_maintenance"));
                }
            }

            if (chambre == null) {
                throw new IllegalArgumentException("Chambre non trouvee");
            }

            if (!chambre.isActif() || chambre.isEnMaintenance()) {
                throw new IllegalStateException("La chambre n'est pas disponible");
            }

            if (chambre.getNbLitsOccupes() >= chambre.getCapacite()) {
                throw new IllegalStateException("Aucun lit disponible dans cette chambre");
            }

            // 3. Creer l'hospitalisation
            Hospitalisation hospitalisation = new Hospitalisation();
            hospitalisation.setNumeroSejour(genererNumeroSejour());
            hospitalisation.setDossierId(dossierId);
            hospitalisation.setChambreId(chambreId);
            hospitalisation.setLitNumero(chambre.getNbLitsOccupes() + 1);
            hospitalisation.setDateEntree(LocalDateTime.now());
            hospitalisation.setDateSortiePrevue(dateSortiePrevue);
            hospitalisation.setMotifHospitalisation(motif);
            hospitalisation.setDiagnosticEntree(diagnosticEntree);
            hospitalisation.setStatut(Hospitalisation.Statut.EN_COURS);
            hospitalisation.setMedecinId(currentUser.getId());

            int hospId = hospitalisationDAO.insert(hospitalisation);
            hospitalisation.setId(hospId);

            // 4. Incrementer le nombre de lits occupes
            String updateChambreSql = "UPDATE chambres SET nb_lits_occupes = nb_lits_occupes + 1 WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateChambreSql)) {
                stmt.setInt(1, chambreId);
                stmt.executeUpdate();
            }

            // 5. Mettre a jour le statut du dossier
            dossier.setStatut(DossierPriseEnCharge.Statut.HOSPITALISE);
            dossierDAO.update(dossier);

            conn.commit();

            journalService.logAction(
                currentUser,
                JournalAction.TypeAction.CREATION,
                "Hospitalisation patient - Sejour: " + hospitalisation.getNumeroSejour() +
                " - Chambre: " + chambre.getNumero(),
                "Hospitalisation",
                hospId
            );

            return hospitalisation;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Erreur lors de l'hospitalisation: " + e.getMessage(), e);
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
     * Libere un patient hospitalise de maniere transactionnelle.
     * 1. Termine l'hospitalisation
     * 2. Libere le lit dans la chambre
     * 3. Met a jour le statut du dossier
     */
    public Hospitalisation sortiePatient(int hospitalisationId, String diagnosticSortie,
                                          Hospitalisation.TypeSortie typeSortie, String observations) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isMedecin() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul un medecin peut autoriser la sortie d'un patient");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Recuperer l'hospitalisation
            Hospitalisation hospitalisation = hospitalisationDAO.findById(hospitalisationId);
            if (hospitalisation == null) {
                throw new IllegalArgumentException("Hospitalisation non trouvee");
            }

            if (hospitalisation.getStatut() != Hospitalisation.Statut.EN_COURS) {
                throw new IllegalStateException("L'hospitalisation n'est pas en cours");
            }

            // 2. Verrouiller la chambre avec SELECT FOR UPDATE
            String lockSql = "SELECT * FROM chambres WHERE id = ? FOR UPDATE";
            try (PreparedStatement stmt = conn.prepareStatement(lockSql)) {
                stmt.setInt(1, hospitalisation.getChambreId());
                stmt.executeQuery();
            }

            // 3. Terminer l'hospitalisation
            hospitalisation.setStatut(Hospitalisation.Statut.TERMINEE);
            hospitalisation.setDateSortieEffective(LocalDateTime.now());
            hospitalisation.setDiagnosticSortie(diagnosticSortie);
            hospitalisation.setTypeSortie(typeSortie);
            hospitalisation.setObservations(observations);
            hospitalisation.setMedecinSortieId(currentUser.getId());

            hospitalisationDAO.update(hospitalisation);

            // 4. Liberer le lit dans la chambre
            String updateChambreSql = "UPDATE chambres SET nb_lits_occupes = nb_lits_occupes - 1 WHERE id = ? AND nb_lits_occupes > 0";
            try (PreparedStatement stmt = conn.prepareStatement(updateChambreSql)) {
                stmt.setInt(1, hospitalisation.getChambreId());
                stmt.executeUpdate();
            }

            // 5. Mettre a jour le dossier
            DossierPriseEnCharge dossier = dossierDAO.findById(hospitalisation.getDossierId());
            if (dossier != null) {
                dossier.setStatut(DossierPriseEnCharge.Statut.TERMINE);
                dossier.setDateCloture(LocalDateTime.now());
                dossier.setDestinationSortie(DossierPriseEnCharge.DestinationSortie.DOMICILE);
                dossierDAO.update(dossier);
            }

            conn.commit();

            journalService.logAction(
                currentUser,
                JournalAction.TypeAction.MODIFICATION,
                "Sortie patient - Sejour: " + hospitalisation.getNumeroSejour() +
                " - Type: " + typeSortie.getLibelle(),
                "Hospitalisation",
                hospitalisationId
            );

            return hospitalisation;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            throw new RuntimeException("Erreur lors de la sortie: " + e.getMessage(), e);
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

    // ==================== CONSULTATION ====================

    /**
     * Recupere une hospitalisation par son identifiant.
     */
    public Optional<Hospitalisation> getHospitalisationById(int id) {
        return Optional.ofNullable(hospitalisationDAO.findById(id));
    }

    /**
     * Recupere les hospitalisations en cours.
     */
    public List<Hospitalisation> getHospitalisationsEnCours() {
        return hospitalisationDAO.findEnCours();
    }

    /**
     * Recupere les hospitalisations d'un dossier.
     */
    public List<Hospitalisation> getHospitalisationsByDossierId(int dossierId) {
        return hospitalisationDAO.findByDossierId(dossierId);
    }

    /**
     * Recupere les chambres disponibles.
     */
    public List<Chambre> getChambresDisponibles() {
        return chambreDAO.findDisponibles();
    }

    /**
     * Met a jour les observations d'une hospitalisation.
     */
    public Hospitalisation mettreAJourObservations(int hospitalisationId, String observations, String evolution) {
        Hospitalisation hospitalisation = hospitalisationDAO.findById(hospitalisationId);
        if (hospitalisation == null) {
            throw new IllegalArgumentException("Hospitalisation non trouvee");
        }

        hospitalisation.setObservations(observations);
        hospitalisation.setEvolution(evolution);
        hospitalisationDAO.update(hospitalisation);

        return hospitalisation;
    }

    // ==================== UTILITAIRES ====================

    private String genererNumeroOrdonnance() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        return "ORD-" + datePart + "-" + randomPart;
    }

    private String genererNumeroSejour() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        return "SEJ-" + datePart + "-" + randomPart;
    }
}
