package appli.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Hospitalisation {

    public enum Statut {
        EN_COURS("En cours"),
        TERMINEE("Terminee"),
        TRANSFEREE("Transferee");

        private final String libelle;

        Statut(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private int dossierId;
    private DossierPriseEnCharge dossier;
    private int chambreId;
    private Chambre chambre;
    private LocalDateTime dateEntree;
    private LocalDate dateSortiePrevue;
    private LocalDateTime dateSortieEffective;
    private String motifHospitalisation;
    private String diagnostic;
    private String traitement;
    private String observations;
    private Statut statut;
    private int medecinId;
    private User medecin;

    public Hospitalisation() {
        this.statut = Statut.EN_COURS;
        this.dateEntree = LocalDateTime.now();
    }

    public Hospitalisation(int dossierId, int chambreId, String motifHospitalisation, int medecinId) {
        this();
        this.dossierId = dossierId;
        this.chambreId = chambreId;
        this.motifHospitalisation = motifHospitalisation;
        this.medecinId = medecinId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getChambreId() {
        return chambreId;
    }

    public void setChambreId(int chambreId) {
        this.chambreId = chambreId;
    }

    public Chambre getChambre() {
        return chambre;
    }

    public void setChambre(Chambre chambre) {
        this.chambre = chambre;
    }

    public LocalDateTime getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDateTime dateEntree) {
        this.dateEntree = dateEntree;
    }

    public LocalDate getDateSortiePrevue() {
        return dateSortiePrevue;
    }

    public void setDateSortiePrevue(LocalDate dateSortiePrevue) {
        this.dateSortiePrevue = dateSortiePrevue;
    }

    public LocalDateTime getDateSortieEffective() {
        return dateSortieEffective;
    }

    public void setDateSortieEffective(LocalDateTime dateSortieEffective) {
        this.dateSortieEffective = dateSortieEffective;
    }

    public String getMotifHospitalisation() {
        return motifHospitalisation;
    }

    public void setMotifHospitalisation(String motifHospitalisation) {
        this.motifHospitalisation = motifHospitalisation;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
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

    @Override
    public String toString() {
        return "Hospitalisation{" +
                "id=" + id +
                ", dossierId=" + dossierId +
                ", chambreId=" + chambreId +
                ", dateEntree=" + dateEntree +
                ", statut=" + statut +
                '}';
    }
}
