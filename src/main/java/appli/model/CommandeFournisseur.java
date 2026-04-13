package appli.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modele representant une commande passee aupres d'un fournisseur.
 * Creee en BROUILLON, elle evolue jusqu'a LIVREE apres reception.
 * Le montant HT et TTC est calcule depuis les {@link LigneCommande}.
 */
public class CommandeFournisseur {

    /** Cycle de vie de la commande fournisseur. */
    public enum Statut {
        BROUILLON("Brouillon"),
        ENVOYEE("Envoyee"),
        CONFIRMEE("Confirmee"),
        EN_LIVRAISON("En livraison"),
        LIVREE_PARTIELLE("Livree partielle"),
        LIVREE("Livree"),
        ANNULEE("Annulee");

        private final String libelle;

        Statut(String libelle) { this.libelle = libelle; }

        public String getLibelle() { return libelle; }

        @Override
        public String toString() { return libelle; }
    }

    private int id;
    private String numeroCommande;
    private int fournisseurId;
    private Fournisseur fournisseur;
    private LocalDateTime dateCommande;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonEffective;
    private Statut statut;
    private BigDecimal montantHt;
    private BigDecimal montantTtc;
    private String notes;
    private int createurId;
    private Integer validateurId;

    public CommandeFournisseur() {
        this.statut = Statut.BROUILLON;
        this.montantHt = BigDecimal.ZERO;
        this.montantTtc = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroCommande() { return numeroCommande; }
    public void setNumeroCommande(String numeroCommande) { this.numeroCommande = numeroCommande; }

    public int getFournisseurId() { return fournisseurId; }
    public void setFournisseurId(int fournisseurId) { this.fournisseurId = fournisseurId; }

    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public LocalDate getDateLivraisonPrevue() { return dateLivraisonPrevue; }
    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) { this.dateLivraisonPrevue = dateLivraisonPrevue; }

    public LocalDate getDateLivraisonEffective() { return dateLivraisonEffective; }
    public void setDateLivraisonEffective(LocalDate dateLivraisonEffective) { this.dateLivraisonEffective = dateLivraisonEffective; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public BigDecimal getMontantHt() { return montantHt; }
    public void setMontantHt(BigDecimal montantHt) { this.montantHt = montantHt; }

    public BigDecimal getMontantTtc() { return montantTtc; }
    public void setMontantTtc(BigDecimal montantTtc) { this.montantTtc = montantTtc; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreateurId() { return createurId; }
    public void setCreateurId(int createurId) { this.createurId = createurId; }

    public Integer getValidateurId() { return validateurId; }
    public void setValidateurId(Integer validateurId) { this.validateurId = validateurId; }
}
