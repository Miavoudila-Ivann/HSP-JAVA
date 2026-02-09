package appli.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DemandeProduit {

    public enum Statut {
        EN_ATTENTE("En attente"),
        VALIDEE("Validee"),
        EN_PREPARATION("En preparation"),
        PRETE("Prete"),
        LIVREE("Livree"),
        REFUSEE("Refusee"),
        ANNULEE("Annulee");

        private final String libelle;

        Statut(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String numeroDemande;
    private int produitId;
    private Produit produit;
    private int quantiteDemandee;
    private Integer quantiteLivree;
    private int medecinId;
    private User medecin;
    private Integer dossierId;
    private DossierPriseEnCharge dossier;
    private Integer hospitalisationId;
    private Hospitalisation hospitalisation;
    private Integer ordonnanceId;
    private Ordonnance ordonnance;
    private Integer emplacementDestinationId;
    private EmplacementStock emplacementDestination;
    private LocalDateTime dateDemande;
    private LocalDate dateBesoin;
    private boolean urgence;
    private int priorite;
    private String motif;
    private Statut statut;
    private Integer gestionnaireId;
    private User gestionnaire;
    private LocalDateTime dateTraitement;
    private String commentaireTraitement;
    private LocalDateTime dateLivraison;
    private Integer livreurId;
    private User livreur;

    public DemandeProduit() {
        this.statut = Statut.EN_ATTENTE;
        this.dateDemande = LocalDateTime.now();
        this.urgence = false;
        this.priorite = 0;
        this.quantiteLivree = 0;
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

    public String getNumeroDemande() {
        return numeroDemande;
    }

    public void setNumeroDemande(String numeroDemande) {
        this.numeroDemande = numeroDemande;
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

    public Integer getQuantiteLivree() {
        return quantiteLivree;
    }

    public void setQuantiteLivree(Integer quantiteLivree) {
        this.quantiteLivree = quantiteLivree;
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

    public Integer getOrdonnanceId() {
        return ordonnanceId;
    }

    public void setOrdonnanceId(Integer ordonnanceId) {
        this.ordonnanceId = ordonnanceId;
    }

    public Ordonnance getOrdonnance() {
        return ordonnance;
    }

    public void setOrdonnance(Ordonnance ordonnance) {
        this.ordonnance = ordonnance;
    }

    public Integer getEmplacementDestinationId() {
        return emplacementDestinationId;
    }

    public void setEmplacementDestinationId(Integer emplacementDestinationId) {
        this.emplacementDestinationId = emplacementDestinationId;
    }

    public EmplacementStock getEmplacementDestination() {
        return emplacementDestination;
    }

    public void setEmplacementDestination(EmplacementStock emplacementDestination) {
        this.emplacementDestination = emplacementDestination;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDate getDateBesoin() {
        return dateBesoin;
    }

    public void setDateBesoin(LocalDate dateBesoin) {
        this.dateBesoin = dateBesoin;
    }

    public boolean isUrgence() {
        return urgence;
    }

    public void setUrgence(boolean urgence) {
        this.urgence = urgence;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
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

    public LocalDateTime getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDateTime dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public Integer getLivreurId() {
        return livreurId;
    }

    public void setLivreurId(Integer livreurId) {
        this.livreurId = livreurId;
    }

    public User getLivreur() {
        return livreur;
    }

    public void setLivreur(User livreur) {
        this.livreur = livreur;
    }

    public boolean isLivraisonComplete() {
        return quantiteLivree != null && quantiteLivree >= quantiteDemandee;
    }

    @Override
    public String toString() {
        return "DemandeProduit{" +
                "id=" + id +
                ", numeroDemande='" + numeroDemande + '\'' +
                ", produitId=" + produitId +
                ", quantiteDemandee=" + quantiteDemandee +
                ", urgence=" + urgence +
                ", statut=" + statut +
                '}';
    }
}
