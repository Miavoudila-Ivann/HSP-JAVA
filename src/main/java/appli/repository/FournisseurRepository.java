package appli.repository;

import appli.model.Fournisseur;

import java.util.List;
import java.util.Optional;

public interface FournisseurRepository {

    Fournisseur save(Fournisseur fournisseur);

    Fournisseur update(Fournisseur fournisseur);

    Optional<Fournisseur> findById(int id);

    List<Fournisseur> findAll();
}
