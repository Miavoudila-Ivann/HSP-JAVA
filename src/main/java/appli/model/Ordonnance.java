package appli.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ordonnance {

    public enum Statut {
        ACTIVE("Active"),
        TERMINEE("Terminee"),
        ANNULEE("Annulee"),
        SUSPENDUE("Suspendue");

        private final String libelle;

        Statut(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String numeroOrdonnance;
    private int dossierId;
    private DossierPriseEnCharge dossier;
    private Integer hospitalisationId;
    private Hospitalisation hospitalisation;
    private int medecinId;
    private User medecin;
    private LocalDateTime datePrescription;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Statut statut;
    private String notes;
    private List<LigneOrdonnance> lignes;

    public Ordonnance() {
        this.statut = Statut.ACTIVE;
        this.datePrescription = LocalDateTime.now();
        this.dateDebut = LocalDate.now();
        this.lignes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroOrdonnance() {
        return numeroOrdonnance;
    }

    public void setNumeroOrdonnance(String numeroOrdonnance) {
        this.numeroOrdonnance = numeroOrdonnance;
    }

    public int getDossierId() {
        return dossierId;
    }

    public void setDossierId(int dossierId) {
        this.dossierId = dossierId;
    }

    public DossierPriseEnCharge getDossier() {
        return dossier;
    }

    public void setDossier(DossierPriseEnCharge dossier) {
        this.dossier = dossier;
    }

    public Integer getHospitalisationId() {
        return hospitalisationId;
    }

    public void setHospitalisationId(Integer hospitalisationId) {
        this.hospitalisationId = hospitalisationId;
    }

    public Hospitalisation getHospitalisation() {
        return hospitalisation;
    }

    public void setHospitalisation(Hospitalisation hospitalisation) {
        this.hospitalisation = hospitalisation;
    }

    public int getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(int medecinId) {
        this.medecinId = medecinId;
    }

    public User getMedecin() {
        return medecin;
    }

    public void setMedecin(User medecin) {
        this.medecin = medecin;
    }

    public LocalDateTime getDatePrescription() {
        return datePrescription;
    }

    public void setDatePrescription(LocalDateTime datePrescription) {
        this.datePrescription = datePrescription;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<LigneOrdonnance> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneOrdonnance> lignes) {
        this.lignes = lignes;
    }

    public void addLigne(LigneOrdonnance ligne) {
        this.lignes.add(ligne);
        ligne.setOrdonnanceId(this.id);
    }

    @Override
    public String toString() {
        return "Ordonnance{" +
                "id=" + id +
                ", numeroOrdonnance='" + numeroOrdonnance + '\'' +
                ", dossierId=" + dossierId +
                ", medecinId=" + medecinId +
                ", statut=" + statut +
                '}';
    }
}
