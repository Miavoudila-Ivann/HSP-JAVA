package appli.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Produit {

    public enum NiveauDangerosite {
        FAIBLE("Faible", "#4CAF50"),
        MOYEN("Moyen", "#FFC107"),
        ELEVE("Eleve", "#FF9800"),
        TRES_ELEVE("Tres eleve", "#F44336");

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
    private String code;
    private String codeCip;
    private String nom;
    private String nomCommercial;
    private String description;
    private Integer categorieId;
    private CategorieProduit categorie;
    private Forme forme;
    private String dosage;
    private String uniteMesure;
    private BigDecimal prixUnitaire;
    private BigDecimal tva;
    private NiveauDangerosite niveauDangerosite;
    private String conditionsStockage;
    private BigDecimal temperatureMin;
    private BigDecimal temperatureMax;
    private boolean necessiteOrdonnance;
    private boolean stupefiant;
    private int datePeremptionAlerteJours;
    private int seuilAlerteStock;
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
