package appli.model;

import java.time.LocalDateTime;

public class JournalAction {

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
    private String adresseIP;
    private String userAgent;
    private String entite;
    private Integer entiteId;
    private String donneesAvant;
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
