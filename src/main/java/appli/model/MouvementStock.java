package appli.model;

import java.time.LocalDateTime;

public class MouvementStock {

    public enum TypeMouvement {
        ENTREE("Entree"),
        SORTIE("Sortie"),
        TRANSFERT("Transfert"),
        AJUSTEMENT("Ajustement"),
        PEREMPTION("Peremption"),
        CASSE("Casse"),
        RETOUR("Retour");

        private final String libelle;

        TypeMouvement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private int id;
    private int stockId;
    private Stock stock;
    private int produitId;
    private Produit produit;
    private TypeMouvement typeMouvement;
    private int quantite;
    private int quantiteAvant;
    private int quantiteApres;
    private String motif;
    private String referenceDocument;
    private Integer emplacementSourceId;
    private EmplacementStock emplacementSource;
    private Integer emplacementDestinationId;
    private EmplacementStock emplacementDestination;
    private Integer dossierId;
    private DossierPriseEnCharge dossier;
    private Integer ordonnanceId;
    private Ordonnance ordonnance;
    private int userId;
    private User user;
    private LocalDateTime dateMouvement;
    private boolean valide;
    private LocalDateTime dateValidation;
    private Integer validateurId;
    private User validateur;

    public MouvementStock() {
        this.dateMouvement = LocalDateTime.now();
        this.valide = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getQuantiteAvant() {
        return quantiteAvant;
    }

    public void setQuantiteAvant(int quantiteAvant) {
        this.quantiteAvant = quantiteAvant;
    }

    public int getQuantiteApres() {
        return quantiteApres;
    }

    public void setQuantiteApres(int quantiteApres) {
        this.quantiteApres = quantiteApres;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(String referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    public Integer getEmplacementSourceId() {
        return emplacementSourceId;
    }

    public void setEmplacementSourceId(Integer emplacementSourceId) {
        this.emplacementSourceId = emplacementSourceId;
    }

    public EmplacementStock getEmplacementSource() {
        return emplacementSource;
    }

    public void setEmplacementSource(EmplacementStock emplacementSource) {
        this.emplacementSource = emplacementSource;
    }

    public Integer getEmplacementDestinationId() {
        return emplacementDestinationId;
    }

    public void setEmplacementDestinationId(Integer emplacementDestinationId) {
        this.emplacementDestinationId = emplacementDestinationId;
    }

    public EmplacementStock getEmplacementDestination() {
        return emplacementDestination;
    }

    public void setEmplacementDestination(EmplacementStock emplacementDestination) {
        this.emplacementDestination = emplacementDestination;
    }

    public Integer getDossierId() {
        return dossierId;
    }

    public void setDossierId(Integer dossierId) {
        this.dossierId = dossierId;
    }

    public DossierPriseEnCharge getDossier() {
        return dossier;
    }

    public void setDossier(DossierPriseEnCharge dossier) {
        this.dossier = dossier;
    }

    public Integer getOrdonnanceId() {
        return ordonnanceId;
    }

    public void setOrdonnanceId(Integer ordonnanceId) {
        this.ordonnanceId = ordonnanceId;
    }

    public Ordonnance getOrdonnance() {
        return ordonnance;
    }

    public void setOrdonnance(Ordonnance ordonnance) {
        this.ordonnance = ordonnance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public Integer getValidateurId() {
        return validateurId;
    }

    public void setValidateurId(Integer validateurId) {
        this.validateurId = validateurId;
    }

    public User getValidateur() {
        return validateur;
    }

    public void setValidateur(User validateur) {
        this.validateur = validateur;
    }

    @Override
    public String toString() {
        return "MouvementStock{" +
                "id=" + id +
                ", produitId=" + produitId +
                ", typeMouvement=" + typeMouvement +
                ", quantite=" + quantite +
                ", dateMouvement=" + dateMouvement +
                '}';
    }
}
