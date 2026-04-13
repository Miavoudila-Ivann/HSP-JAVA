package appli.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modele representant un produit pharmaceutique ou consommable medical en stock.
 * Contient les informations de nomenclature, de stockage, de danger et de seuils d'alerte.
 */
public class Produit {

    /**
     * Niveau de dangerosite du produit (couleur utilisee pour l'affichage).
     */
    public enum NiveauDangerosite {
        FAIBLE("Faible", "#4CAF50"),
        MOYEN("Moyen", "#FFC107"),
        ELEVE("Eleve", "#FF9800"),
        TRES_ELEVE("Tres eleve", "#F44336"),
        CRITIQUE("Critique", "#9C27B0");

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

    /** Forme galenique du produit. */
    public enum Forme {
        COMPRIME("Comprime"),
        GELULE("Gelule"),
        SIROP("Sirop"),
        INJECTABLE("Injectable"),
        POMMADE("Pommade"),
        SPRAY("Spray"),
        PATCH("Patch"),
        DISPOSITIF("Dispositif"),
        CONSOMMABLE("Consommable"),
        AUTRE("Autre");

        private final String libelle;

        Forme(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    /** Code interne unique du produit (ex: "PARA500"). */
    private String code;
    /** Code CIP (Codes Identifiants de Presentation) pour les medicaments. */
    private String codeCip;
    private String nom;
    /** Nom de marque commercial, affiche en complement du nom generique. */
    private String nomCommercial;
    private String description;
    private Integer categorieId;
    private CategorieProduit categorie;
    private Forme forme;
    private String dosage;
    private String uniteMesure;
    private BigDecimal prixUnitaire;
    /** Taux de TVA en pourcentage (ex: 20.00 pour 20%). */
    private BigDecimal tva;
    private NiveauDangerosite niveauDangerosite;
    private String conditionsStockage;
    /** Temperature minimale de stockage en degres Celsius. */
    private BigDecimal temperatureMin;
    /** Temperature maximale de stockage en degres Celsius. */
    private BigDecimal temperatureMax;
    /** Indique si une ordonnance est requise pour dispenser ce produit. */
    private boolean necessiteOrdonnance;
    /** Indique si le produit est un stupefiant (sujet a une tracabilite renforcee). */
    private boolean stupefiant;
    /** Nombre de jours avant la date de peremption a partir duquel une alerte est generee. */
    private int datePeremptionAlerteJours;
    /** Quantite minimale en dessous de laquelle une alerte de stock bas est declenchee. */
    private int seuilAlerteStock;
    /** Quantite declenchant une commande automatique (null si inactif). */
    private Integer seuilCommandeAuto;
    private Integer fournisseurPrincipalId;
    private Fournisseur fournisseurPrincipal;
    private boolean actif;
    private LocalDateTime dateCreation;

    public Produit() {
        this.niveauDangerosite = NiveauDangerosite.FAIBLE;
        this.datePeremptionAlerteJours = 30;
        this.seuilAlerteStock = 10;
        this.actif = true;
        this.necessiteOrdonnance = false;
        this.stupefiant = false;
        this.tva = new BigDecimal("20.00");
    }

    public Produit(String code, String nom, String uniteMesure) {
        this();
        this.code = code;
        this.nom = nom;
        this.uniteMesure = uniteMesure;
    }

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

    public String getCodeCip() {
        return codeCip;
    }

    public void setCodeCip(String codeCip) {
        this.codeCip = codeCip;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNomCommercial() {
        return nomCommercial;
    }

    public void setNomCommercial(String nomCommercial) {
        this.nomCommercial = nomCommercial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Integer categorieId) {
        this.categorieId = categorieId;
    }

    public CategorieProduit getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
    }

    public Forme getForme() {
        return forme;
    }

    public void setForme(Forme forme) {
        this.forme = forme;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getUniteMesure() {
        return uniteMesure;
    }

    public void setUniteMesure(String uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getTva() {
        return tva;
    }

    public void setTva(BigDecimal tva) {
        this.tva = tva;
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

    public BigDecimal getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(BigDecimal temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public BigDecimal getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(BigDecimal temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public boolean isNecessiteOrdonnance() {
        return necessiteOrdonnance;
    }

    public void setNecessiteOrdonnance(boolean necessiteOrdonnance) {
        this.necessiteOrdonnance = necessiteOrdonnance;
    }

    public boolean isStupefiant() {
        return stupefiant;
    }

    public void setStupefiant(boolean stupefiant) {
        this.stupefiant = stupefiant;
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

    public Integer getSeuilCommandeAuto() {
        return seuilCommandeAuto;
    }

    public void setSeuilCommandeAuto(Integer seuilCommandeAuto) {
        this.seuilCommandeAuto = seuilCommandeAuto;
    }

    public Integer getFournisseurPrincipalId() {
        return fournisseurPrincipalId;
    }

    public void setFournisseurPrincipalId(Integer fournisseurPrincipalId) {
        this.fournisseurPrincipalId = fournisseurPrincipalId;
    }

    public Fournisseur getFournisseurPrincipal() {
        return fournisseurPrincipal;
    }

    public void setFournisseurPrincipal(Fournisseur fournisseurPrincipal) {
        this.fournisseurPrincipal = fournisseurPrincipal;
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

    /** Retourne le nom complet "Nom generique (Nom commercial)" si disponible, sinon juste le nom. */
    public String getNomComplet() {
        if (nomCommercial != null && !nomCommercial.isEmpty()) {
            return nom + " (" + nomCommercial + ")";
        }
        return nom;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", niveauDangerosite=" + niveauDangerosite +
                ", actif=" + actif +
                '}';
    }
}
