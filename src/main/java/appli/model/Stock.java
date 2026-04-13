package appli.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modele representant une entree de stock : un lot d'un produit dans un emplacement donne.
 * La quantite disponible est calculee comme {@code quantite - quantiteReservee}.
 * La date de peremption permet de declencher des alertes anticipees.
 */
public class Stock {

    private int id;
    private int produitId;
    private Produit produit;
    private int emplacementId;
    private EmplacementStock emplacement;
    private String lot;
    /** Quantite totale en stock (avant reservation). */
    private int quantite;
    /** Quantite reservee pour des demandes en cours de livraison. */
    private int quantiteReservee;
    private LocalDate datePeremption;
    private LocalDateTime dateReception;
    private BigDecimal prixUnitaireAchat;
    private Integer fournisseurId;
    private Fournisseur fournisseur;
    private String numeroCommande;
    private LocalDateTime dateDerniereMaj;

    public Stock() {
        this.quantite = 0;
        this.quantiteReservee = 0;
        this.dateReception = LocalDateTime.now();
    }

    public Stock(int produitId, int emplacementId, String lot, int quantite) {
        this();
        this.produitId = produitId;
        this.emplacementId = emplacementId;
        this.lot = lot;
        this.quantite = quantite;
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

    public int getEmplacementId() {
        return emplacementId;
    }

    public void setEmplacementId(int emplacementId) {
        this.emplacementId = emplacementId;
    }

    public EmplacementStock getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(EmplacementStock emplacement) {
        this.emplacement = emplacement;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getQuantiteReservee() {
        return quantiteReservee;
    }

    public void setQuantiteReservee(int quantiteReservee) {
        this.quantiteReservee = quantiteReservee;
    }

    /** Retourne la quantite reellement disponible (total - reserves). */
    public int getQuantiteDisponible() {
        return quantite - quantiteReservee;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    public LocalDateTime getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDateTime dateReception) {
        this.dateReception = dateReception;
    }

    public BigDecimal getPrixUnitaireAchat() {
        return prixUnitaireAchat;
    }

    public void setPrixUnitaireAchat(BigDecimal prixUnitaireAchat) {
        this.prixUnitaireAchat = prixUnitaireAchat;
    }

    public Integer getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Integer fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getNumeroCommande() {
        return numeroCommande;
    }

    public void setNumeroCommande(String numeroCommande) {
        this.numeroCommande = numeroCommande;
    }

    public LocalDateTime getDateDerniereMaj() {
        return dateDerniereMaj;
    }

    public void setDateDerniereMaj(LocalDateTime dateDerniereMaj) {
        this.dateDerniereMaj = dateDerniereMaj;
    }

    /** Retourne {@code true} si la date de peremption est passee. */
    public boolean isPerime() {
        return datePeremption != null && datePeremption.isBefore(LocalDate.now());
    }

    /**
     * Retourne {@code true} si la date de peremption est dans moins de {@code joursAlerte} jours.
     */
    public boolean isProcheDuPeremption(int joursAlerte) {
        if (datePeremption == null) {
            return false;
        }
        return datePeremption.isBefore(LocalDate.now().plusDays(joursAlerte));
    }

    /**
     * Retourne le nombre de jours avant la date de peremption.
     * Retourne {@link Long#MAX_VALUE} si aucune date n'est definie.
     */
    public long getJoursAvantPeremption() {
        if (datePeremption == null) {
            return Long.MAX_VALUE;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), datePeremption);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", emplacementId=" + emplacementId +
                ", lot='" + lot + '\'' +
                ", quantite=" + quantite +
                ", datePeremption=" + datePeremption +
                '}';
    }
}
