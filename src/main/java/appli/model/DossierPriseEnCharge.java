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

    public enum Statut {
        EN_ATTENTE("En attente"),
        EN_COURS("En cours"),
        TERMINE("Termine"),
        ANNULE("Annule");

        private final String libelle;

        Statut(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private int patientId;
    private Patient patient;
    private LocalDateTime dateCreation;
    private String motifAdmission;
    private NiveauGravite niveauGravite;
    private String symptomes;
    private String antecedents;
    private String allergies;
    private String traitementEnCours;
    private Statut statut;
    private Integer medecinResponsableId;
    private User medecinResponsable;
    private Integer creePar;
    private LocalDateTime dateCloture;
    private String notesCloture;

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

    public String getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
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

    @Override
    public String toString() {
        return "DossierPriseEnCharge{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", motifAdmission='" + motifAdmission + '\'' +
                ", niveauGravite=" + niveauGravite +
                ", statut=" + statut +
                '}';
    }
}
