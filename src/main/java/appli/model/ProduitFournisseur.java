package appli.model;

import java.math.BigDecimal;

public class ProduitFournisseur {

    private int id;
    private int produitId;
    private Produit produit;
    private int fournisseurId;
    private Fournisseur fournisseur;
    private String referenceFournisseur;
    private BigDecimal prixAchat;
    private Integer delaiLivraisonJours;
    private Integer quantiteMinimumCommande;
    private boolean estPrincipal;
    private boolean actif;

    public ProduitFournisseur() {
        this.estPrincipal = false;
        this.actif = true;
    }

    public ProduitFournisseur(int produitId, int fournisseurId) {
        this();
        this.produitId = produitId;
        this.fournisseurId = fournisseurId;
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

    public int getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getReferenceFournisseur() {
        return referenceFournisseur;
    }

    public void setReferenceFournisseur(String referenceFournisseur) {
        this.referenceFournisseur = referenceFournisseur;
    }

    public BigDecimal getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(BigDecimal prixAchat) {
        this.prixAchat = prixAchat;
    }

    public Integer getDelaiLivraisonJours() {
        return delaiLivraisonJours;
    }

    public void setDelaiLivraisonJours(Integer delaiLivraisonJours) {
        this.delaiLivraisonJours = delaiLivraisonJours;
    }

    public Integer getQuantiteMinimumCommande() {
        return quantiteMinimumCommande;
    }

    public void setQuantiteMinimumCommande(Integer quantiteMinimumCommande) {
        this.quantiteMinimumCommande = quantiteMinimumCommande;
    }

    public boolean isEstPrincipal() {
        return estPrincipal;
    }

    public void setEstPrincipal(boolean estPrincipal) {
        this.estPrincipal = estPrincipal;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "ProduitFournisseur{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", fournisseurId=" + fournisseurId +
                ", prixAchat=" + prixAchat +
                ", estPrincipal=" + estPrincipal +
                '}';
    }
}
