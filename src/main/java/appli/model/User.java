package appli.model;

import java.time.LocalDateTime;

public class User {

    public enum Role {
        ADMIN("Administrateur"),
        SECRETAIRE("Secretaire"),
        MEDECIN("Medecin"),
        GESTIONNAIRE("Gestionnaire de stock");

        private final String libelle;

        Role(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String email;
    private String passwordHash;
    private String nom;
    private String prenom;
    private Role role;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;

    public User() {}

    public User(String email, String passwordHash, String nom, String prenom, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.actif = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", role=" + role +
                ", actif=" + actif +
                '}';
    }
}
