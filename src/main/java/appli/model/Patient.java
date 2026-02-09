package appli.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Patient {

    public enum Sexe {
        M("Masculin"),
        F("Feminin");

        private final String libelle;

        Sexe(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String numeroSecuriteSociale;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String adresse;
    private String telephone;
    private String email;
    private String personneContactNom;
    private String personneContactTelephone;
    private LocalDateTime dateCreation;
    private Integer creePar;

    public Patient() {}

    public Patient(String numeroSecuriteSociale, String nom, String prenom,
                   LocalDate dateNaissance, Sexe sexe) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroSecuriteSociale() {
        return numeroSecuriteSociale;
    }

    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) {
        this.numeroSecuriteSociale = numeroSecuriteSociale;
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

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonneContactNom() {
        return personneContactNom;
    }

    public void setPersonneContactNom(String personneContactNom) {
        this.personneContactNom = personneContactNom;
    }

    public String getPersonneContactTelephone() {
        return personneContactTelephone;
    }

    public void setPersonneContactTelephone(String personneContactTelephone) {
        this.personneContactTelephone = personneContactTelephone;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Integer getCreePar() {
        return creePar;
    }

    public void setCreePar(Integer creePar) {
        this.creePar = creePar;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public int getAge() {
        if (dateNaissance == null) {
            return 0;
        }
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", numeroSecuriteSociale='" + numeroSecuriteSociale + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", sexe=" + sexe +
                '}';
    }
}
