package appli.model;

import java.time.LocalDateTime;

/**
 * Modele representant une entree dans le journal d'audit (traçabilite RGPD).
 * Enregistre chaque action significative : connexions, creation, modification,
 * suppression, consultation, export.
 * Stocke optionnellement les snapshots avant/apres pour les modifications.
 */
public class JournalAction {

    /** Types d'actions traquees dans le journal. */
    public enum TypeAction {
        CONNEXION("Connexion"),
        DECONNEXION("Deconnexion"),
        CREATION("Creation"),
        MODIFICATION("Modification"),
        SUPPRESSION("Suppression"),
        CONSULTATION("Consultation"),
        EXPORT("Export"),
        ECHEC_CONNEXION("Echec de connexion");

        private final String libelle;

        TypeAction(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private Integer userId;
    private User user;
    private TypeAction typeAction;
    private String description;
    private LocalDateTime dateAction;
    /** Adresse IP de la machine depuis laquelle l'action a ete effectuee. */
    private String adresseIP;
    /** Identifiant du client (ex: "HSP-JavaFX/1.0"). */
    private String userAgent;
    /** Nom de la table ou entite concernee (ex: "Patient", "Ordonnance"). */
    private String entite;
    /** Identifiant de l'entite concernee. */
    private Integer entiteId;
    /** Snapshot JSON de l'entite avant modification (RGPD). */
    private String donneesAvant;
    /** Snapshot JSON de l'entite apres modification (RGPD). */
    private String donneesApres;

    public JournalAction() {
        this.dateAction = LocalDateTime.now();
    }

    public JournalAction(Integer userId, TypeAction typeAction, String description) {
        this();
        this.userId = userId;
        this.typeAction = typeAction;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TypeAction getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(TypeAction typeAction) {
        this.typeAction = typeAction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }

    public String getAdresseIP() {
        return adresseIP;
    }

    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getEntite() {
        return entite;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }

    public Integer getEntiteId() {
        return entiteId;
    }

    public void setEntiteId(Integer entiteId) {
        this.entiteId = entiteId;
    }

    public String getDonneesAvant() {
        return donneesAvant;
    }

    public void setDonneesAvant(String donneesAvant) {
        this.donneesAvant = donneesAvant;
    }

    public String getDonneesApres() {
        return donneesApres;
    }

    public void setDonneesApres(String donneesApres) {
        this.donneesApres = donneesApres;
    }

    @Override
    public String toString() {
        return "JournalAction{" +
                "id=" + id +
                ", userId=" + userId +
                ", typeAction=" + typeAction +
                ", description='" + description + '\'' +
                ", dateAction=" + dateAction +
                '}';
    }
}
