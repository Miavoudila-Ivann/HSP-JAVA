package model;

public class Auteur {
    private int idAuteur;
    private String nom;
    private String prenom;
    public String dateNaissance;
    private String refPays;

    public Auteur(int idAuteur, String nom, String prenom, String dateNaissance, int refPays) {
        this.idAuteur = idAuteur;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;

    }

    public int getIdAuteur() {
        return idAuteur;
    }

    public void setIdAuteur(int idAuteur) {
        this.idAuteur = idAuteur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getRefPays() {
        return refPays;
    }

    public void setRefPays(String refPays) {
        this.refPays = refPays;
    }


}
