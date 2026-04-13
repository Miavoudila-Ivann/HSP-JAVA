package appli.model;

import java.math.BigDecimal;

/**
 * Modele representant une chambre dans l'hopital.
 * Identifie par son numero et son etage, elle a une capacite en lits.
 * Peut etre mise en maintenance ou desactivee.
 * Le nombre de lits occupes est mis a jour de maniere transactionnelle lors
 * des hospitalisations et sorties via {@link appli.service.MedicalService}.
 */
public class Chambre {

    /** Type de chambre determinant son usage medical. */
    public enum TypeChambre {
        SIMPLE("Chambre simple"),
        DOUBLE("Chambre double"),
        SOINS_INTENSIFS("Soins intensifs"),
        REANIMATION("Reanimation"),
        URGENCE("Urgence"),
        PEDIATRIE("Pediatrie"),
        MATERNITE("Maternite");

        private final String libelle;

        TypeChambre(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private String numero;
    private int etage;
    private String batiment;
    private TypeChambre typeChambre;
    /** Nombre total de lits dans la chambre. */
    private int capacite;
    /** Nombre de lits actuellement occupes (mis a jour transactionnellement). */
    private int nbLitsOccupes;
    private String equipements;
    private BigDecimal tarifJournalier;
    private boolean actif;
    private boolean enMaintenance;
    private String notes;

    public Chambre() {
        this.batiment = "Principal";
        this.actif = true;
        this.enMaintenance = false;
        this.nbLitsOccupes = 0;
        this.capacite = 1;
    }

    public Chambre(String numero, int etage, TypeChambre typeChambre, int capacite) {
        this();
        this.numero = numero;
        this.etage = etage;
        this.typeChambre = typeChambre;
        this.capacite = capacite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getEtage() {
        return etage;
    }

    public void setEtage(int etage) {
        this.etage = etage;
    }

    public String getBatiment() {
        return batiment;
    }

    public void setBatiment(String batiment) {
        this.batiment = batiment;
    }

    public TypeChambre getTypeChambre() {
        return typeChambre;
    }

    public void setTypeChambre(TypeChambre typeChambre) {
        this.typeChambre = typeChambre;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public int getNbLitsOccupes() {
        return nbLitsOccupes;
    }

    public void setNbLitsOccupes(int nbLitsOccupes) {
        this.nbLitsOccupes = nbLitsOccupes;
    }

    public String getEquipements() {
        return equipements;
    }

    public void setEquipements(String equipements) {
        this.equipements = equipements;
    }

    public BigDecimal getTarifJournalier() {
        return tarifJournalier;
    }

    public void setTarifJournalier(BigDecimal tarifJournalier) {
        this.tarifJournalier = tarifJournalier;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean isEnMaintenance() {
        return enMaintenance;
    }

    public void setEnMaintenance(boolean enMaintenance) {
        this.enMaintenance = enMaintenance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /** Retourne le nombre de lits non occupes dans la chambre. */
    public int getLitsDisponibles() {
        return capacite - nbLitsOccupes;
    }

    /** Retourne {@code true} si la chambre est active, non en maintenance et a au moins un lit libre. */
    public boolean hasLitDisponible() {
        return nbLitsOccupes < capacite && actif && !enMaintenance;
    }

    /** Retourne une description courte lisible, ex: "101 - Chambre simple (Etage 1)". */
    public String getDescription() {
        return numero + " - " + typeChambre.getLibelle() + " (Etage " + etage + ")";
    }

    @Override
    public String toString() {
        return "Chambre{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", etage=" + etage +
                ", typeChambre=" + typeChambre +
                ", capacite=" + capacite +
                ", nbLitsOccupes=" + nbLitsOccupes +
                '}';
    }
}
