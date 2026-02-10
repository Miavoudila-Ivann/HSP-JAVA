package appli.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Hospitalisation {

    public enum Statut {
        EN_COURS("En cours"),
        TERMINEE("Terminee"),
        TRANSFEREE("Transferee"),
        DECES("Deces");

        private final String libelle;

        Statut(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum TypeSortie {
        GUERISON("Guerison"),
        AMELIORATION("Amelioration"),
        TRANSFERT("Transfert"),
        CONTRE_AVIS("Contre avis medical"),
        DECES("Deces"),
        AUTRE("Autre");

        private final String libelle;

        TypeSortie(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String numeroSejour;
    private int dossierId;
    private DossierPriseEnCharge dossier;
    private int chambreId;
    private Chambre chambre;
    private Integer litNumero;
    private LocalDateTime dateEntree;
    private LocalDate dateSortiePrevue;
    private LocalDateTime dateSortieEffective;
    private String motifHospitalisation;
    private String diagnosticEntree;
    private String diagnosticSortie;
    private String traitement;
    private String observations;
    private String evolution;
    private Statut statut;
    private TypeSortie typeSortie;
    private int medecinId;
    private User medecin;
    private Integer medecinSortieId;
    private User medecinSortie;

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

    public String getNumeroSejour() {
        return numeroSejour;
    }

    public void setNumeroSejour(String numeroSejour) {
        this.numeroSejour = numeroSejour;
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

    public Integer getLitNumero() {
        return litNumero;
    }

    public void setLitNumero(Integer litNumero) {
        this.litNumero = litNumero;
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

    public String getDiagnosticEntree() {
        return diagnosticEntree;
    }

    public void setDiagnosticEntree(String diagnosticEntree) {
        this.diagnosticEntree = diagnosticEntree;
    }

    public String getDiagnosticSortie() {
        return diagnosticSortie;
    }

    public void setDiagnosticSortie(String diagnosticSortie) {
        this.diagnosticSortie = diagnosticSortie;
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

    public String getEvolution() {
        return evolution;
    }

    public void setEvolution(String evolution) {
        this.evolution = evolution;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public TypeSortie getTypeSortie() {
        return typeSortie;
    }

    public void setTypeSortie(TypeSortie typeSortie) {
        this.typeSortie = typeSortie;
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

    public Integer getMedecinSortieId() {
        return medecinSortieId;
    }

    public void setMedecinSortieId(Integer medecinSortieId) {
        this.medecinSortieId = medecinSortieId;
    }

    public User getMedecinSortie() {
        return medecinSortie;
    }

    public void setMedecinSortie(User medecinSortie) {
        this.medecinSortie = medecinSortie;
    }

    @Override
    public String toString() {
        return "Hospitalisation{" +
                "id=" + id +
                ", numeroSejour='" + numeroSejour + '\'' +
                ", dossierId=" + dossierId +
                ", chambreId=" + chambreId +
                ", dateEntree=" + dateEntree +
                ", statut=" + statut +
                '}';
    }
}
