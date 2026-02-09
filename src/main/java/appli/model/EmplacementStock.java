package appli.model;

import java.math.BigDecimal;

public class EmplacementStock {

    public enum TypeEmplacement {
        PHARMACIE("Pharmacie"),
        RESERVE("Reserve"),
        URGENCE("Urgences"),
        BLOC("Bloc operatoire"),
        SERVICE("Service"),
        FRIGO("Refrigerateur"),
        COFFRE("Coffre securise");

        private final String libelle;

        TypeEmplacement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String code;
    private String nom;
    private String description;
    private TypeEmplacement typeEmplacement;
    private boolean temperatureControlee;
    private BigDecimal temperatureCible;
    private Integer responsableId;
    private User responsable;
    private boolean actif;

    public EmplacementStock() {
        this.actif = true;
        this.temperatureControlee = false;
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

    public TypeEmplacement getTypeEmplacement() {
        return typeEmplacement;
    }

    public void setTypeEmplacement(TypeEmplacement typeEmplacement) {
        this.typeEmplacement = typeEmplacement;
    }

    public boolean isTemperatureControlee() {
        return temperatureControlee;
    }

    public void setTemperatureControlee(boolean temperatureControlee) {
        this.temperatureControlee = temperatureControlee;
    }

    public BigDecimal getTemperatureCible() {
        return temperatureCible;
    }

    public void setTemperatureCible(BigDecimal temperatureCible) {
        this.temperatureCible = temperatureCible;
    }

    public Integer getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Integer responsableId) {
        this.responsableId = responsableId;
    }

    public User getResponsable() {
        return responsable;
    }

    public void setResponsable(User responsable) {
        this.responsable = responsable;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "EmplacementStock{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", typeEmplacement=" + typeEmplacement +
                '}';
    }
}
