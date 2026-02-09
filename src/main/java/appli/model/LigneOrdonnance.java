package appli.model;

import java.time.LocalDate;

public class LigneOrdonnance {

    public enum VoieAdministration {
        ORALE("Orale"),
        IV("Intraveineuse"),
        IM("Intramusculaire"),
        SC("Sous-cutanee"),
        CUTANEE("Cutanee"),
        RECTALE("Rectale"),
        INHALATION("Inhalation"),
        AUTRE("Autre");

        private final String libelle;

        VoieAdministration(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private int ordonnanceId;
    private Ordonnance ordonnance;
    private int produitId;
    private Produit produit;
    private String posologie;
    private int quantite;
    private Integer dureeJours;
    private String frequence;
    private VoieAdministration voieAdministration;
    private String instructions;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public LigneOrdonnance() {
        this.voieAdministration = VoieAdministration.ORALE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrdonnanceId() {
        return ordonnanceId;
    }

    public void setOrdonnanceId(int ordonnanceId) {
        this.ordonnanceId = ordonnanceId;
    }

    public Ordonnance getOrdonnance() {
        return ordonnance;
    }

    public void setOrdonnance(Ordonnance ordonnance) {
        this.ordonnance = ordonnance;
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

    public String getPosologie() {
        return posologie;
    }

    public void setPosologie(String posologie) {
        this.posologie = posologie;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Integer getDureeJours() {
        return dureeJours;
    }

    public void setDureeJours(Integer dureeJours) {
        this.dureeJours = dureeJours;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public VoieAdministration getVoieAdministration() {
        return voieAdministration;
    }

    public void setVoieAdministration(VoieAdministration voieAdministration) {
        this.voieAdministration = voieAdministration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

    @Override
    public String toString() {
        return "LigneOrdonnance{" +
                "id=" + id +
                ", ordonnanceId=" + ordonnanceId +
                ", produitId=" + produitId +
                ", posologie='" + posologie + '\'' +
                ", quantite=" + quantite +
                '}';
    }
}
