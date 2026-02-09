package appli.repository;

import appli.dao.FournisseurDAO;
import appli.model.Fournisseur;

import java.util.List;
import java.util.Optional;

public class FournisseurRepository {

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    public Optional<Fournisseur> getById(int id) {
        return Optional.ofNullable(fournisseurDAO.findById(id));
    }

    public List<Fournisseur> getAll() {
        return fournisseurDAO.findAll();
    }

    public List<Fournisseur> search(String searchTerm) {
        return fournisseurDAO.search(searchTerm);
    }

    public Fournisseur save(Fournisseur fournisseur) {
        if (fournisseur.getId() == 0) {
            int id = fournisseurDAO.insert(fournisseur);
            fournisseur.setId(id);
        } else {
            fournisseurDAO.update(fournisseur);
        }
        return fournisseur;
    }

    public void delete(int id) {
        fournisseurDAO.delete(id);
    }
}
