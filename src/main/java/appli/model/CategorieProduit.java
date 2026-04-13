package appli.model;

/**
 * Modele representant une categorie de produits pharmaceutiques.
 * Les categories peuvent etre hierarchiques (parent/enfant).
 * Exemples : "Medicaments", "Dispositifs medicaux", "Consommables".
 */
public class CategorieProduit {

    private int id;
    private String code;
    private String nom;
    private String description;
    /** Identifiant de la categorie parente (null si categorie racine). */
    private Integer categorieParentId;
    private CategorieProduit categorieParent;
    private boolean actif;

    public CategorieProduit() {
        this.actif = true;
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

    public Integer getCategorieParentId() {
        return categorieParentId;
    }

    public void setCategorieParentId(Integer categorieParentId) {
        this.categorieParentId = categorieParentId;
    }

    public CategorieProduit getCategorieParent() {
        return categorieParent;
    }

    public void setCategorieParent(CategorieProduit categorieParent) {
        this.categorieParent = categorieParent;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "CategorieProduit{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }
}
