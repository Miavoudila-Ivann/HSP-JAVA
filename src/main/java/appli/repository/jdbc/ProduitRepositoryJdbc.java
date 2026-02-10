package appli.repository.jdbc;

import appli.dao.ProduitDAO;
import appli.model.Produit;
import appli.repository.ProduitRepository;

import java.util.List;
import java.util.Optional;

public class ProduitRepositoryJdbc implements ProduitRepository {

    private final ProduitDAO produitDAO = new ProduitDAO();

    @Override
    public Produit save(Produit produit) {
        if (produit.getId() == 0) {
            int id = produitDAO.insert(produit);
            produit.setId(id);
        } else {
            produitDAO.update(produit);
        }
        return produit;
    }

    @Override
    public Produit update(Produit produit) {
        produitDAO.update(produit);
        return produit;
    }

    @Override
    public Optional<Produit> findById(int id) {
        return Optional.ofNullable(produitDAO.findById(id));
    }

    @Override
    public Optional<Produit> findByCode(String code) {
        return Optional.ofNullable(produitDAO.findByCode(code));
    }

    @Override
    public List<Produit> findAll() {
        return produitDAO.findAll();
    }

    @Override
    public List<Produit> findByCategorieId(int categorieId) {
        return produitDAO.findByCategorieId(categorieId);
    }

    @Override
    public List<Produit> findAllWithStock() {
        return produitDAO.findAllWithStock();
    }

    @Override
    public List<Produit> findLowStock() {
        return produitDAO.findLowStock();
    }
}
