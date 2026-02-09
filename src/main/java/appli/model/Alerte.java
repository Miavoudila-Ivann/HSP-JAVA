package appli.model;

import java.time.LocalDateTime;

public class Alerte {

    public enum TypeAlerte {
        STOCK_BAS("Stock bas"),
        PEREMPTION("Peremption proche"),
        RUPTURE("Rupture de stock"),
        DEMANDE_URGENTE("Demande urgente"),
        COMMANDE_RETARD("Commande en retard"),
        TEMPERATURE("Alerte temperature"),
        AUTRE("Autre");

        private final String libelle;

        TypeAlerte(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum Niveau {
        INFO("Information"),
        WARNING("Avertissement"),
        CRITICAL("Critique");

        private final String libelle;

        Niveau(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private TypeAlerte typeAlerte;
    private Niveau niveau;
    private String titre;
    private String message;
    private String entite;
    private Integer entiteId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private Integer luParId;
    private User luPar;
    private LocalDateTime dateResolution;
    private Integer resoluParId;
    private User resoluPar;
    private String notesResolution;

    public Alerte() {
        this.dateCreation = LocalDateTime.now();
        this.niveau = Niveau.WARNING;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(LocalDateTime dateLecture) {
        this.dateLecture = dateLecture;
    }

    public Integer getLuParId() {
        return luParId;
    }

    public void setLuParId(Integer luParId) {
        this.luParId = luParId;
    }

    public User getLuPar() {
        return luPar;
    }

    public void setLuPar(User luPar) {
        this.luPar = luPar;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Integer getResoluParId() {
        return resoluParId;
    }

    public void setResoluParId(Integer resoluParId) {
        this.resoluParId = resoluParId;
    }

    public User getResoluPar() {
        return resoluPar;
    }

    public void setResoluPar(User resoluPar) {
        this.resoluPar = resoluPar;
    }

    public String getNotesResolution() {
        return notesResolution;
    }

    public void setNotesResolution(String notesResolution) {
        this.notesResolution = notesResolution;
    }

    public boolean isLue() {
        return dateLecture != null;
    }

    public boolean isResolue() {
        return dateResolution != null;
    }

    @Override
    public String toString() {
        return "Alerte{" +
                "id=" + id +
                ", typeAlerte=" + typeAlerte +
                ", niveau=" + niveau +
                ", titre='" + titre + '\'' +
                '}';
    }
}
