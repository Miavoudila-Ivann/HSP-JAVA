package appli.model;

import java.time.LocalDateTime;

public class DemandeProduit {

    public enum Statut {
        EN_ATTENTE("En attente"),
        VALIDEE("Validee"),
        REFUSEE("Refusee"),
        LIVREE("Livree");

        private final String libelle;

        Statut(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private int produitId;
    private Produit produit;
    private int quantiteDemandee;
    private int medecinId;
    private User medecin;
    private Integer dossierId;
    private DossierPriseEnCharge dossier;
    private LocalDateTime dateDemande;
    private boolean urgence;
    private String motif;
    private Statut statut;
    private Integer gestionnaireId;
    private User gestionnaire;
    private LocalDateTime dateTraitement;
    private String commentaireTraitement;

    public DemandeProduit() {
        this.statut = Statut.EN_ATTENTE;
        this.dateDemande = LocalDateTime.now();
        this.urgence = false;
    }

    public DemandeProduit(int produitId, int quantiteDemandee, int medecinId) {
        this();
        this.produitId = produitId;
        this.quantiteDemandee = quantiteDemandee;
        this.medecinId = medecinId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantiteDemandee() {
        return quantiteDemandee;
    }

    public void setQuantiteDemandee(int quantiteDemandee) {
        this.quantiteDemandee = quantiteDemandee;
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

    public Integer getDossierId() {
        return dossierId;
    }

    public void setDossierId(Integer dossierId) {
        this.dossierId = dossierId;
    }

    public DossierPriseEnCharge getDossier() {
        return dossier;
    }

    public void setDossier(DossierPriseEnCharge dossier) {
        this.dossier = dossier;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public boolean isUrgence() {
        return urgence;
    }

    public void setUrgence(boolean urgence) {
        this.urgence = urgence;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Integer getGestionnaireId() {
        return gestionnaireId;
    }

    public void setGestionnaireId(Integer gestionnaireId) {
        this.gestionnaireId = gestionnaireId;
    }

    public User getGestionnaire() {
        return gestionnaire;
    }

    public void setGestionnaire(User gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public String getCommentaireTraitement() {
        return commentaireTraitement;
    }

    public void setCommentaireTraitement(String commentaireTraitement) {
        this.commentaireTraitement = commentaireTraitement;
    }

    @Override
    public String toString() {
        return "DemandeProduit{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", quantiteDemandee=" + quantiteDemandee +
                ", medecinId=" + medecinId +
                ", urgence=" + urgence +
                ", statut=" + statut +
                '}';
    }
}
