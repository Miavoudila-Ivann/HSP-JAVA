package appli.repository.jdbc;

import appli.dao.FournisseurDAO;
import appli.model.Fournisseur;
import appli.repository.FournisseurRepository;

import java.util.List;
import java.util.Optional;

public class FournisseurRepositoryJdbc implements FournisseurRepository {

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @Override
    public Fournisseur save(Fournisseur fournisseur) {
        if (fournisseur.getId() == 0) {
            int id = fournisseurDAO.insert(fournisseur);
            fournisseur.setId(id);
        } else {
            fournisseurDAO.update(fournisseur);
        }
        return fournisseur;
    }

    @Override
    public Fournisseur update(Fournisseur fournisseur) {
        fournisseurDAO.update(fournisseur);
        return fournisseur;
    }

    @Override
    public Optional<Fournisseur> findById(int id) {
        return Optional.ofNullable(fournisseurDAO.findById(id));
    }

    @Override
    public List<Fournisseur> findAll() {
        return fournisseurDAO.findAll();
    }
}
