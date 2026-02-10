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

    public enum GroupeSanguin {
        A_POSITIF("A+"),
        A_NEGATIF("A-"),
        B_POSITIF("B+"),
        B_NEGATIF("B-"),
        AB_POSITIF("AB+"),
        AB_NEGATIF("AB-"),
        O_POSITIF("O+"),
        O_NEGATIF("O-");

        private final String libelle;

        GroupeSanguin(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }

        public String getDbValue() {
            return libelle;
        }

        public static GroupeSanguin fromDbValue(String value) {
            for (GroupeSanguin gs : values()) {
                if (gs.libelle.equals(value)) {
                    return gs;
                }
            }
            return null;
        }
    }

    private int id;
    private String numeroSecuriteSociale;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private GroupeSanguin groupeSanguin;
    private String adresse;
    private String codePostal;
    private String ville;
    private String telephone;
    private String telephoneMobile;
    private String email;
    private String personneContactNom;
    private String personneContactTelephone;
    private String personneContactLien;
    private String medecinTraitant;
    private String notesMedicales;
    private String allergiesConnues;
    private String antecedentsMedicaux;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private Integer creePar;
    private Integer modifiePar;

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

    public GroupeSanguin getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(GroupeSanguin groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephoneMobile() {
        return telephoneMobile;
    }

    public void setTelephoneMobile(String telephoneMobile) {
        this.telephoneMobile = telephoneMobile;
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

    public String getPersonneContactLien() {
        return personneContactLien;
    }

    public void setPersonneContactLien(String personneContactLien) {
        this.personneContactLien = personneContactLien;
    }

    public String getMedecinTraitant() {
        return medecinTraitant;
    }

    public void setMedecinTraitant(String medecinTraitant) {
        this.medecinTraitant = medecinTraitant;
    }

    public String getNotesMedicales() {
        return notesMedicales;
    }

    public void setNotesMedicales(String notesMedicales) {
        this.notesMedicales = notesMedicales;
    }

    public String getAllergiesConnues() {
        return allergiesConnues;
    }

    public void setAllergiesConnues(String allergiesConnues) {
        this.allergiesConnues = allergiesConnues;
    }

    public String getAntecedentsMedicaux() {
        return antecedentsMedicaux;
    }

    public void setAntecedentsMedicaux(String antecedentsMedicaux) {
        this.antecedentsMedicaux = antecedentsMedicaux;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Integer getCreePar() {
        return creePar;
    }

    public void setCreePar(Integer creePar) {
        this.creePar = creePar;
    }

    public Integer getModifiePar() {
        return modifiePar;
    }

    public void setModifiePar(Integer modifiePar) {
        this.modifiePar = modifiePar;
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
