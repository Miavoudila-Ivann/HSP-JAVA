package appli.model;

import java.math.BigDecimal;

public class LigneCommande {

    private int id;
    private int commandeId;
    private int produitId;
    private Produit produit;
    private int quantiteCommandee;
    private int quantiteRecue;
    private BigDecimal prixUnitaire;
    private BigDecimal tva;
    private BigDecimal remisePourcent;
    private BigDecimal montantHt;

    public LigneCommande() {
        this.quantiteRecue = 0;
        this.tva = BigDecimal.valueOf(20);
        this.remisePourcent = BigDecimal.ZERO;
        this.montantHt = BigDecimal.ZERO;
    }

    // ==================== Getters / Setters ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCommandeId() { return commandeId; }
    public void setCommandeId(int commandeId) { this.commandeId = commandeId; }

    public int getProduitId() { return produitId; }
    public void setProduitId(int produitId) { this.produitId = produitId; }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public int getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(int quantiteCommandee) { this.quantiteCommandee = quantiteCommandee; }

    public int getQuantiteRecue() { return quantiteRecue; }
    public void setQuantiteRecue(int quantiteRecue) { this.quantiteRecue = quantiteRecue; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public BigDecimal getTva() { return tva; }
    public void setTva(BigDecimal tva) { this.tva = tva; }

    public BigDecimal getRemisePourcent() { return remisePourcent; }
    public void setRemisePourcent(BigDecimal remisePourcent) { this.remisePourcent = remisePourcent; }

    public BigDecimal getMontantHt() { return montantHt; }
    public void setMontantHt(BigDecimal montantHt) { this.montantHt = montantHt; }

    /**
     * Recalcule le montant HT : quantite * prix * (1 - remise/100)
     */
    public void recalculerMontantHt() {
        if (prixUnitaire != null) {
            BigDecimal base = prixUnitaire.multiply(BigDecimal.valueOf(quantiteCommandee));
            if (remisePourcent != null && remisePourcent.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal remise = base.multiply(remisePourcent).divide(BigDecimal.valueOf(100));
                base = base.subtract(remise);
            }
            this.montantHt = base;
        }
    }
}
