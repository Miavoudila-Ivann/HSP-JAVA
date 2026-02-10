package appli.model;

import java.time.LocalDateTime;

public class DossierPriseEnCharge {

    public enum NiveauGravite {
        NIVEAU_1("1", "Mineur", "#4CAF50"),
        NIVEAU_2("2", "Modere", "#8BC34A"),
        NIVEAU_3("3", "Serieux", "#FFC107"),
        NIVEAU_4("4", "Grave", "#FF9800"),
        NIVEAU_5("5", "Critique", "#F44336");

        private final String code;
        private final String libelle;
        private final String couleur;

        NiveauGravite(String code, String libelle, String couleur) {
            this.code = code;
            this.libelle = libelle;
            this.couleur = couleur;
        }

        public String getCode() {
            return code;
        }

        public String getLibelle() {
            return libelle;
        }

        public String getCouleur() {
            return couleur;
        }

        public static NiveauGravite fromCode(String code) {
            for (NiveauGravite niveau : values()) {
                if (niveau.code.equals(code)) {
                    return niveau;
                }
            }
            return NIVEAU_1;
        }
    }

    public enum ModeArrivee {
        AMBULANCE("Ambulance"),
        POMPIERS("Pompiers"),
        PERSONNEL("Personnel"),
        TRANSFERT("Transfert"),
        AUTRE("Autre");

        private final String libelle;

        ModeArrivee(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum Statut {
        EN_ATTENTE("En attente"),
        EN_COURS("En cours"),
        HOSPITALISE("Hospitalise"),
        TERMINE("Termine"),
        TRANSFERE("Transfere"),
        ANNULE("Annule"),
        DECEDE("Decede");

        private final String libelle;

        Statut(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum DestinationSortie {
        DOMICILE("Domicile"),
        HOSPITALISATION("Hospitalisation"),
        TRANSFERT("Transfert"),
        DECES("Deces"),
        AUTRE("Autre");

        private final String libelle;

        DestinationSortie(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String numeroDossier;
    private int patientId;
    private Patient patient;
    private LocalDateTime dateCreation;
    private LocalDateTime dateAdmission;
    private String motifAdmission;
    private NiveauGravite niveauGravite;
    private ModeArrivee modeArrivee;
    private String symptomes;
    private String constantesVitales;
    private String antecedents;
    private String allergies;
    private String traitementEnCours;
    private Statut statut;
    private int prioriteTriage;
    private Integer medecinResponsableId;
    private User medecinResponsable;
    private Integer creePar;
    private LocalDateTime datePriseEnCharge;
    private LocalDateTime dateCloture;
    private String notesCloture;
    private DestinationSortie destinationSortie;

    public DossierPriseEnCharge() {
        this.statut = Statut.EN_ATTENTE;
    }

    public DossierPriseEnCharge(int patientId, String motifAdmission, NiveauGravite niveauGravite) {
        this();
        this.patientId = patientId;
        this.motifAdmission = motifAdmission;
        this.niveauGravite = niveauGravite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroDossier() {
        return numeroDossier;
    }

    public void setNumeroDossier(String numeroDossier) {
        this.numeroDossier = numeroDossier;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateAdmission() {
        return dateAdmission;
    }

    public void setDateAdmission(LocalDateTime dateAdmission) {
        this.dateAdmission = dateAdmission;
    }

    public String getMotifAdmission() {
        return motifAdmission;
    }

    public void setMotifAdmission(String motifAdmission) {
        this.motifAdmission = motifAdmission;
    }

    public NiveauGravite getNiveauGravite() {
        return niveauGravite;
    }

    public void setNiveauGravite(NiveauGravite niveauGravite) {
        this.niveauGravite = niveauGravite;
    }

    public ModeArrivee getModeArrivee() {
        return modeArrivee;
    }

    public void setModeArrivee(ModeArrivee modeArrivee) {
        this.modeArrivee = modeArrivee;
    }

    public String getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
    }

    public String getConstantesVitales() {
        return constantesVitales;
    }

    public void setConstantesVitales(String constantesVitales) {
        this.constantesVitales = constantesVitales;
    }

    public String getAntecedents() {
        return antecedents;
    }

    public void setAntecedents(String antecedents) {
        this.antecedents = antecedents;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getTraitementEnCours() {
        return traitementEnCours;
    }

    public void setTraitementEnCours(String traitementEnCours) {
        this.traitementEnCours = traitementEnCours;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public int getPrioriteTriage() {
        return prioriteTriage;
    }

    public void setPrioriteTriage(int prioriteTriage) {
        this.prioriteTriage = prioriteTriage;
    }

    public Integer getMedecinResponsableId() {
        return medecinResponsableId;
    }

    public void setMedecinResponsableId(Integer medecinResponsableId) {
        this.medecinResponsableId = medecinResponsableId;
    }

    public User getMedecinResponsable() {
        return medecinResponsable;
    }

    public void setMedecinResponsable(User medecinResponsable) {
        this.medecinResponsable = medecinResponsable;
    }

    public Integer getCreePar() {
        return creePar;
    }

    public void setCreePar(Integer creePar) {
        this.creePar = creePar;
    }

    public LocalDateTime getDatePriseEnCharge() {
        return datePriseEnCharge;
    }

    public void setDatePriseEnCharge(LocalDateTime datePriseEnCharge) {
        this.datePriseEnCharge = datePriseEnCharge;
    }

    public LocalDateTime getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDateTime dateCloture) {
        this.dateCloture = dateCloture;
    }

    public String getNotesCloture() {
        return notesCloture;
    }

    public void setNotesCloture(String notesCloture) {
        this.notesCloture = notesCloture;
    }

    public DestinationSortie getDestinationSortie() {
        return destinationSortie;
    }

    public void setDestinationSortie(DestinationSortie destinationSortie) {
        this.destinationSortie = destinationSortie;
    }

    @Override
    public String toString() {
        return "DossierPriseEnCharge{" +
                "id=" + id +
                ", numeroDossier='" + numeroDossier + '\'' +
                ", patientId=" + patientId +
                ", motifAdmission='" + motifAdmission + '\'' +
                ", niveauGravite=" + niveauGravite +
                ", statut=" + statut +
                '}';
    }
}
