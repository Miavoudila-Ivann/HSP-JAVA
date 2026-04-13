package appli.model;

import java.time.LocalDateTime;

/**
 * Modele representant un utilisateur du systeme hospitalier.
 * Un utilisateur dispose d'un role qui determine ses permissions via {@link appli.security.RoleGuard}.
 * La securite inclut le hachage du mot de passe, le verrouillage apres echecs et le 2FA (TOTP).
 */
public class User {

    /**
     * Les quatre roles possibles dans l'application.
     * Chaque role donne acces a un sous-ensemble de fonctionnalites.
     */
    public enum Role {
        ADMIN("Administrateur"),
        SECRETAIRE("Secretaire"),
        MEDECIN("Medecin"),
        GESTIONNAIRE("Gestionnaire de stock");

        private final String libelle;

        Role(String libelle) {
            this.libelle = libelle;
        }

        /** Retourne le libelle lisible du role (affiche dans l'interface). */
        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String email;
    /** Mot de passe stocke sous forme de hash bcrypt. */
    private String passwordHash;
    private String nom;
    private String prenom;
    private Role role;
    /** Specialite medicale (utilisee uniquement pour le role MEDECIN). */
    private String specialite;
    private String telephone;
    /** Indique si le compte est actif. Un compte inactif ne peut pas se connecter. */
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    /** Nombre de tentatives de connexion echouees consecutives. */
    private int tentativesConnexion;
    /** {@code true} si le compte a ete verrouille suite a trop d'echecs. */
    private boolean compteVerrouille;
    /** Date/heure du verrouillage, utilisee pour le deverrouillage automatique apres 30 min. */
    private LocalDateTime dateVerrouillage;
    /** Cle secrete TOTP (Base32) pour l'authentification a deux facteurs. */
    private String totpSecret;
    /** {@code true} si le 2FA TOTP est active pour ce compte. */
    private boolean totpEnabled;

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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public int getTentativesConnexion() {
        return tentativesConnexion;
    }

    public void setTentativesConnexion(int tentativesConnexion) {
        this.tentativesConnexion = tentativesConnexion;
    }

    public boolean isCompteVerrouille() {
        return compteVerrouille;
    }

    public void setCompteVerrouille(boolean compteVerrouille) {
        this.compteVerrouille = compteVerrouille;
    }

    public LocalDateTime getDateVerrouillage() {
        return dateVerrouillage;
    }

    public void setDateVerrouillage(LocalDateTime dateVerrouillage) {
        this.dateVerrouillage = dateVerrouillage;
    }

    public String getTotpSecret() {
        return totpSecret;
    }

    public void setTotpSecret(String totpSecret) {
        this.totpSecret = totpSecret;
    }

    public boolean isTotpEnabled() {
        return totpEnabled;
    }

    public void setTotpEnabled(boolean totpEnabled) {
        this.totpEnabled = totpEnabled;
    }

    /** Retourne le nom complet au format "Prenom Nom". */
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
