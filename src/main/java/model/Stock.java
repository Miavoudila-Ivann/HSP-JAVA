package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Stock {

    private int id;
    private int produitId;
    private Produit produit;
    private int quantite;
    private String emplacement;
    private String lot;
    private LocalDate datePeremption;
    private LocalDateTime dateDerniereMaj;

    public Stock() {}

    public Stock(int produitId, int quantite) {
        this.produitId = produitId;
        this.quantite = quantite;
    }

    // Getters and Setters
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

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    public LocalDateTime getDateDerniereMaj() {
        return dateDerniereMaj;
    }

    public void setDateDerniereMaj(LocalDateTime dateDerniereMaj) {
        this.dateDerniereMaj = dateDerniereMaj;
    }

    public boolean isPerime() {
        return datePeremption != null && datePeremption.isBefore(LocalDate.now());
    }

    public boolean isProcheDuPeremption(int joursAlerte) {
        if (datePeremption == null) {
            return false;
        }
        return datePeremption.isBefore(LocalDate.now().plusDays(joursAlerte));
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", quantite=" + quantite +
                ", emplacement='" + emplacement + '\'' +
                ", lot='" + lot + '\'' +
                ", datePeremption=" + datePeremption +
                '}';
    }
}
