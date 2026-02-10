package appli.repository;

import appli.model.Produit;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository {

    Produit save(Produit produit);

    Produit update(Produit produit);

    Optional<Produit> findById(int id);

    Optional<Produit> findByCode(String code);

    List<Produit> findAll();

    List<Produit> findByCategorieId(int categorieId);

    List<Produit> findAllWithStock();

    List<Produit> findLowStock();
}
