package model;

import java.time.LocalDateTime;

public class Produit {

    public enum NiveauDangerosite {
        FAIBLE("Faible", "#4CAF50"),
        MOYEN("Moyen", "#FFC107"),
        ELEVE("Élevé", "#FF9800"),
        TRES_ELEVE("Très élevé", "#F44336");

        private final String libelle;
        private final String couleur;

        NiveauDangerosite(String libelle, String couleur) {
            this.libelle = libelle;
            this.couleur = couleur;
        }

        public String getLibelle() {
            return libelle;
        }

        public String getCouleur() {
            return couleur;
        }
    }

    private int id;
    private String code;
    private String nom;
    private String description;
    private String categorie;
    private String uniteMesure;
    private NiveauDangerosite niveauDangerosite;
    private String conditionsStockage;
    private int datePeremptionAlerteJours;
    private int seuilAlerteStock;
    private boolean actif;
    private LocalDateTime dateCreation;

    public Produit() {
        this.niveauDangerosite = NiveauDangerosite.FAIBLE;
        this.datePeremptionAlerteJours = 30;
        this.seuilAlerteStock = 10;
        this.actif = true;
    }

    public Produit(String code, String nom, String uniteMesure) {
        this();
        this.code = code;
        this.nom = nom;
        this.uniteMesure = uniteMesure;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getUniteMesure() {
        return uniteMesure;
    }

    public void setUniteMesure(String uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    public NiveauDangerosite getNiveauDangerosite() {
        return niveauDangerosite;
    }

    public void setNiveauDangerosite(NiveauDangerosite niveauDangerosite) {
        this.niveauDangerosite = niveauDangerosite;
    }

    public String getConditionsStockage() {
        return conditionsStockage;
    }

    public void setConditionsStockage(String conditionsStockage) {
        this.conditionsStockage = conditionsStockage;
    }

    public int getDatePeremptionAlerteJours() {
        return datePeremptionAlerteJours;
    }

    public void setDatePeremptionAlerteJours(int datePeremptionAlerteJours) {
        this.datePeremptionAlerteJours = datePeremptionAlerteJours;
    }

    public int getSeuilAlerteStock() {
        return seuilAlerteStock;
    }

    public void setSeuilAlerteStock(int seuilAlerteStock) {
        this.seuilAlerteStock = seuilAlerteStock;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", niveauDangerosite=" + niveauDangerosite +
                '}';
    }
}
