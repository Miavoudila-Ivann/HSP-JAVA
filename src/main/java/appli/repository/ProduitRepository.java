package appli.repository;

import appli.dao.ProduitDAO;
import appli.model.Produit;

import java.util.List;
import java.util.Optional;

public class ProduitRepository {

    private final ProduitDAO produitDAO = new ProduitDAO();

    public Optional<Produit> getById(int id) {
        return Optional.ofNullable(produitDAO.findById(id));
    }

    public Optional<Produit> getByCode(String code) {
        return Optional.ofNullable(produitDAO.findByCode(code));
    }

    public List<Produit> getAll() {
        return produitDAO.findAll();
    }

    public List<Produit> getByCategorie(String categorie) {
        return produitDAO.findByCategorie(categorie);
    }

    public Produit save(Produit produit) {
        if (produit.getId() == 0) {
            int id = produitDAO.insert(produit);
            produit.setId(id);
        } else {
            produitDAO.update(produit);
        }
        return produit;
    }

    public void delete(int id) {
        produitDAO.delete(id);
    }
}
