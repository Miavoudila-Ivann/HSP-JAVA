package appli.service;

import appli.dao.DossierPriseEnChargeDAO;
import appli.model.DossierPriseEnCharge;
import appli.model.JournalAction;
import appli.model.Patient;
import appli.model.User;
import appli.repository.PatientRepository;
import appli.repository.TriageRepository;
import appli.repository.jdbc.PatientRepositoryJdbc;
import appli.repository.jdbc.TriageRepositoryJdbc;
import appli.security.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service metier pour la gestion du triage aux urgences.
 * Gere la creation des dossiers de prise en charge, la liste d'attente et les changements de statut.
 */
public class TriageService {

    private final TriageRepository triageRepository = new TriageRepositoryJdbc();
    private final DossierPriseEnChargeDAO dossierDAO = new DossierPriseEnChargeDAO();
    private final PatientRepository patientRepository = new PatientRepositoryJdbc();
    private final JournalService journalService = new JournalService();

    /**
     * Cree un nouveau dossier de prise en charge pour un patient arrivant aux urgences.
     * Genere automatiquement un numero de dossier unique.
     */
    public DossierPriseEnCharge creerDossier(int patientId, String motifAdmission,
                                              DossierPriseEnCharge.NiveauGravite niveauGravite,
                                              DossierPriseEnCharge.ModeArrivee modeArrivee,
                                              String symptomes) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        // Verifier que le patient existe
        Optional<Patient> patient = patientRepository.findById(patientId);
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient non trouve");
        }

        // Creer le dossier
        DossierPriseEnCharge dossier = new DossierPriseEnCharge();
        dossier.setNumeroDossier(genererNumeroDossier());
        dossier.setPatientId(patientId);
        dossier.setMotifAdmission(motifAdmission);
        dossier.setNiveauGravite(niveauGravite);
        dossier.setModeArrivee(modeArrivee);
        dossier.setSymptomes(symptomes);
        dossier.setStatut(DossierPriseEnCharge.Statut.EN_ATTENTE);
        dossier.setDateAdmission(LocalDateTime.now());
        dossier.setPrioriteTriage(calculerPriorite(niveauGravite));
        dossier.setCreePar(currentUser.getId());

        // Copier les informations medicales du patient
        dossier.setAntecedents(patient.get().getAntecedentsMedicaux());
        dossier.setAllergies(patient.get().getAllergiesConnues());

        DossierPriseEnCharge savedDossier = triageRepository.save(dossier);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.CREATION,
            "Creation dossier triage: " + savedDossier.getNumeroDossier() +
            " - Patient: " + patient.get().getNomComplet() +
            " - Gravite: " + niveauGravite.getLibelle(),
            "DossierPriseEnCharge",
            savedDossier.getId()
        );

        return savedDossier;
    }

    /**
     * Recupere la liste des dossiers en attente de prise en charge.
     * Tries par niveau de gravite (decroissant) puis par date d'arrivee.
     */
    public List<DossierPriseEnCharge> getListeAttente() {
        return dossierDAO.findEnAttenteTriage();
    }

    /**
     * Recupere tous les dossiers ouverts (EN_ATTENTE ou EN_COURS).
     */
    public List<DossierPriseEnCharge> getDossiersOuverts() {
        List<DossierPriseEnCharge> enAttente = dossierDAO.findByStatut(DossierPriseEnCharge.Statut.EN_ATTENTE);
        List<DossierPriseEnCharge> enCours = dossierDAO.findByStatut(DossierPriseEnCharge.Statut.EN_COURS);
        enAttente.addAll(enCours);
        return enAttente;
    }

    /**
     * Recupere les dossiers par statut.
     */
    public List<DossierPriseEnCharge> getByStatut(DossierPriseEnCharge.Statut statut) {
        return dossierDAO.findByStatut(statut);
    }

    /**
     * Recupere un dossier par son identifiant.
     */
    public Optional<DossierPriseEnCharge> getById(int id) {
        return triageRepository.findById(id);
    }

    /**
     * Recupere un dossier par son numero.
     */
    public Optional<DossierPriseEnCharge> getByNumeroDossier(String numeroDossier) {
        return Optional.ofNullable(dossierDAO.findByNumeroDossier(numeroDossier));
    }

    /**
     * Recupere les dossiers d'un patient.
     */
    public List<DossierPriseEnCharge> getByPatientId(int patientId) {
        return dossierDAO.findByPatientId(patientId);
    }

    /**
     * Recupere les dossiers assignes a un medecin.
     */
    public List<DossierPriseEnCharge> getByMedecinId(int medecinId) {
        return dossierDAO.findByMedecinId(medecinId);
    }

    /**
     * Change le statut d'un dossier vers EN_COURS (prise en charge par un medecin).
     */
    public DossierPriseEnCharge prendreEnCharge(int dossierId, int medecinId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isMedecin() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul un medecin peut prendre en charge un dossier");
        }

        Optional<DossierPriseEnCharge> dossierOpt = triageRepository.findById(dossierId);
        if (dossierOpt.isEmpty()) {
            throw new IllegalArgumentException("Dossier non trouve");
        }

        DossierPriseEnCharge dossier = dossierOpt.get();

        if (dossier.getStatut() != DossierPriseEnCharge.Statut.EN_ATTENTE) {
            throw new IllegalStateException("Le dossier n'est pas en attente de prise en charge");
        }

        dossier.setStatut(DossierPriseEnCharge.Statut.EN_COURS);
        dossier.setMedecinResponsableId(medecinId);
        dossier.setDatePriseEnCharge(LocalDateTime.now());

        DossierPriseEnCharge savedDossier = triageRepository.save(dossier);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Prise en charge dossier: " + savedDossier.getNumeroDossier(),
            "DossierPriseEnCharge",
            savedDossier.getId()
        );

        return savedDossier;
    }

    /**
     * Change le statut d'un dossier.
     */
    public DossierPriseEnCharge changerStatut(int dossierId, DossierPriseEnCharge.Statut nouveauStatut) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        Optional<DossierPriseEnCharge> dossierOpt = triageRepository.findById(dossierId);
        if (dossierOpt.isEmpty()) {
            throw new IllegalArgumentException("Dossier non trouve");
        }

        DossierPriseEnCharge dossier = dossierOpt.get();
        DossierPriseEnCharge.Statut ancienStatut = dossier.getStatut();

        dossier.setStatut(nouveauStatut);

        DossierPriseEnCharge savedDossier = triageRepository.save(dossier);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Changement statut dossier: " + savedDossier.getNumeroDossier() +
            " de " + ancienStatut.getLibelle() + " vers " + nouveauStatut.getLibelle(),
            "DossierPriseEnCharge",
            savedDossier.getId()
        );

        return savedDossier;
    }

    /**
     * Met a jour le niveau de gravite d'un dossier.
     */
    public DossierPriseEnCharge modifierNiveauGravite(int dossierId, DossierPriseEnCharge.NiveauGravite nouveauNiveau) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        Optional<DossierPriseEnCharge> dossierOpt = triageRepository.findById(dossierId);
        if (dossierOpt.isEmpty()) {
            throw new IllegalArgumentException("Dossier non trouve");
        }

        DossierPriseEnCharge dossier = dossierOpt.get();
        DossierPriseEnCharge.NiveauGravite ancienNiveau = dossier.getNiveauGravite();

        dossier.setNiveauGravite(nouveauNiveau);
        dossier.setPrioriteTriage(calculerPriorite(nouveauNiveau));

        DossierPriseEnCharge savedDossier = triageRepository.save(dossier);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Modification gravite dossier: " + savedDossier.getNumeroDossier() +
            " de " + ancienNiveau.getLibelle() + " vers " + nouveauNiveau.getLibelle(),
            "DossierPriseEnCharge",
            savedDossier.getId()
        );

        return savedDossier;
    }

    /**
     * Met a jour les constantes vitales d'un dossier.
     */
    public DossierPriseEnCharge mettreAJourConstantesVitales(int dossierId, String constantesVitales) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        Optional<DossierPriseEnCharge> dossierOpt = triageRepository.findById(dossierId);
        if (dossierOpt.isEmpty()) {
            throw new IllegalArgumentException("Dossier non trouve");
        }

        DossierPriseEnCharge dossier = dossierOpt.get();
        dossier.setConstantesVitales(constantesVitales);

        return triageRepository.save(dossier);
    }

    /**
     * Genere un numero de dossier unique.
     * Format: URG-YYYYMMDD-XXXX
     */
    private String genererNumeroDossier() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        return "URG-" + datePart + "-" + randomPart;
    }

    /**
     * Calcule la priorite de triage basee sur le niveau de gravite.
     * Plus le niveau est eleve, plus la priorite est haute (valeur basse).
     */
    private int calculerPriorite(DossierPriseEnCharge.NiveauGravite niveau) {
        return switch (niveau) {
            case NIVEAU_5 -> 1;  // Critique - priorite maximale
            case NIVEAU_4 -> 2;  // Grave
            case NIVEAU_3 -> 3;  // Serieux
            case NIVEAU_2 -> 4;  // Modere
            case NIVEAU_1 -> 5;  // Mineur
        };
    }
}
