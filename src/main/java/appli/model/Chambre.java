package appli.model;

public class Chambre {

    public enum TypeChambre {
        SIMPLE("Chambre simple"),
        DOUBLE("Chambre double"),
        SOINS_INTENSIFS("Soins intensifs"),
        URGENCE("Urgence");

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
    private TypeChambre typeChambre;
    private int capacite;
    private boolean occupee;

    public Chambre() {}

    public Chambre(String numero, int etage, TypeChambre typeChambre, int capacite) {
        this.numero = numero;
        this.etage = etage;
        this.typeChambre = typeChambre;
        this.capacite = capacite;
        this.occupee = false;
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

    public boolean isOccupee() {
        return occupee;
    }

    public void setOccupee(boolean occupee) {
        this.occupee = occupee;
    }

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
                ", occupee=" + occupee +
                '}';
    }
}
