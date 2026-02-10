package appli.service;

import appli.model.JournalAction;
import appli.model.Patient;
import appli.model.User;
import appli.repository.PatientRepository;
import appli.repository.jdbc.PatientRepositoryJdbc;
import appli.security.SessionManager;

import java.util.List;
import java.util.Optional;

/**
 * Service metier pour la gestion des patients.
 * Utilise par la secretaire pour creer et editer les fiches patients.
 */
public class PatientService {

    private final PatientRepository patientRepository = new PatientRepositoryJdbc();
    private final JournalService journalService = new JournalService();

    /**
     * Recupere un patient par son identifiant.
     */
    public Optional<Patient> getById(int id) {
        return patientRepository.findById(id);
    }

    /**
     * Recupere un patient par son numero de securite sociale.
     */
    public Optional<Patient> getByNumeroSecuriteSociale(String numeroSecu) {
        return patientRepository.findBySsn(numeroSecu);
    }

    /**
     * Recupere la liste de tous les patients.
     */
    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    /**
     * Recherche des patients par nom, prenom ou numero de securite sociale.
     */
    public List<Patient> search(String searchTerm) {
        return patientRepository.searchByName(searchTerm);
    }

    /**
     * Cree un nouveau patient.
     * Seule la secretaire peut creer un patient.
     */
    public Patient creerPatient(Patient patient) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isSecretaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seule la secretaire peut creer un patient");
        }

        // Verification que le numero de securite sociale n'existe pas deja
        if (patient.getNumeroSecuriteSociale() != null) {
            if (patientRepository.existsBySsn(patient.getNumeroSecuriteSociale())) {
                throw new IllegalArgumentException("Un patient avec ce numero de securite sociale existe deja");
            }
        }

        patient.setCreePar(currentUser.getId());
        Patient savedPatient = patientRepository.save(patient);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.CREATION,
            "Creation patient: " + savedPatient.getNomComplet(),
            "Patient",
            savedPatient.getId()
        );

        return savedPatient;
    }

    /**
     * Met a jour un patient existant.
     * Seule la secretaire peut modifier un patient.
     */
    public Patient modifierPatient(Patient patient) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isSecretaire() && !SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seule la secretaire peut modifier un patient");
        }

        // Verification que le patient existe
        Optional<Patient> existant = patientRepository.findById(patient.getId());
        if (existant.isEmpty()) {
            throw new IllegalArgumentException("Patient non trouve");
        }

        // Verification unicite numero secu si modifie
        if (patient.getNumeroSecuriteSociale() != null) {
            Optional<Patient> patientAvecMemeSecu = patientRepository.findBySsn(patient.getNumeroSecuriteSociale());
            if (patientAvecMemeSecu.isPresent() && patientAvecMemeSecu.get().getId() != patient.getId()) {
                throw new IllegalArgumentException("Un autre patient avec ce numero de securite sociale existe deja");
            }
        }

        patient.setModifiePar(currentUser.getId());
        Patient savedPatient = patientRepository.save(patient);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.MODIFICATION,
            "Modification patient: " + savedPatient.getNomComplet(),
            "Patient",
            savedPatient.getId()
        );

        return savedPatient;
    }

    /**
     * Supprime un patient.
     * Seul l'admin peut supprimer un patient (RGPD).
     */
    public void supprimerPatient(int patientId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecte");
        }

        if (!SessionManager.getInstance().isAdmin()) {
            throw new SecurityException("Seul l'administrateur peut supprimer un patient");
        }

        Optional<Patient> patient = patientRepository.findById(patientId);
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient non trouve");
        }

        String nomComplet = patient.get().getNomComplet();
        patientRepository.delete(patientId);

        journalService.logAction(
            currentUser,
            JournalAction.TypeAction.SUPPRESSION,
            "Suppression patient: " + nomComplet,
            "Patient",
            patientId
        );
    }

    /**
     * Consulte un patient (pour journalisation RGPD).
     */
    public Optional<Patient> consulterPatient(int patientId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        Optional<Patient> patient = patientRepository.findById(patientId);

        if (patient.isPresent() && currentUser != null) {
            journalService.logAction(
                currentUser,
                JournalAction.TypeAction.CONSULTATION,
                "Consultation patient: " + patient.get().getNomComplet(),
                "Patient",
                patientId
            );
        }

        return patient;
    }
}
