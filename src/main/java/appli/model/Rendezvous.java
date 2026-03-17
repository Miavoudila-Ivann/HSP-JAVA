package appli.model;

import java.time.LocalDateTime;

public class Rendezvous {

    public enum TypeRdv {
        CONSULTATION("Consultation"),
        SUIVI("Suivi"),
        EXAMEN("Examen"),
        CHIRURGIE("Chirurgie"),
        AUTRE("Autre");

        private final String libelle;
        TypeRdv(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
        @Override public String toString() { return libelle; }
    }

    public enum Statut {
        PLANIFIE("Planifie"),
        CONFIRME("Confirme"),
        REALISE("Realise"),
        ANNULE("Annule"),
        REPORTE("Reporte");

        private final String libelle;
        Statut(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
        @Override public String toString() { return libelle; }
    }

    private int id;
    private String numeroRdv;
    private int patientId;
    private int medecinId;
    private LocalDateTime dateHeure;
    private int dureeMinutes;
    private TypeRdv typeRdv;
    private Statut statut;
    private String motif;
    private String notes;
    private String lieu;
    private LocalDateTime dateCreation;
    private Integer creePar;

    // Champs joins pour affichage
    private String patientNom;
    private String patientPrenom;
    private String medecinNom;
    private String medecinPrenom;

    public Rendezvous() {
        this.dureeMinutes = 30;
        this.typeRdv = TypeRdv.CONSULTATION;
        this.statut = Statut.PLANIFIE;
    }

    public String getPatientNomComplet() {
        if (patientPrenom != null && patientNom != null) return patientPrenom + " " + patientNom;
        return "Patient #" + patientId;
    }

    public String getMedecinNomComplet() {
        if (medecinPrenom != null && medecinNom != null) return "Dr. " + medecinPrenom + " " + medecinNom;
        return "Medecin #" + medecinId;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumeroRdv() { return numeroRdv; }
    public void setNumeroRdv(String numeroRdv) { this.numeroRdv = numeroRdv; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getMedecinId() { return medecinId; }
    public void setMedecinId(int medecinId) { this.medecinId = medecinId; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public TypeRdv getTypeRdv() { return typeRdv; }
    public void setTypeRdv(TypeRdv typeRdv) { this.typeRdv = typeRdv; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public Integer getCreePar() { return creePar; }
    public void setCreePar(Integer creePar) { this.creePar = creePar; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String patientNom) { this.patientNom = patientNom; }
    public String getPatientPrenom() { return patientPrenom; }
    public void setPatientPrenom(String patientPrenom) { this.patientPrenom = patientPrenom; }
    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String medecinNom) { this.medecinNom = medecinNom; }
    public String getMedecinPrenom() { return medecinPrenom; }
    public void setMedecinPrenom(String medecinPrenom) { this.medecinPrenom = medecinPrenom; }
}
