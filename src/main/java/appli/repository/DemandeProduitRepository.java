package appli.repository;

import appli.dao.DemandeProduitDAO;
import appli.model.DemandeProduit;

import java.util.List;
import java.util.Optional;

public class DemandeProduitRepository {

    private final DemandeProduitDAO demandeProduitDAO = new DemandeProduitDAO();

    public Optional<DemandeProduit> getById(int id) {
        return Optional.ofNullable(demandeProduitDAO.findById(id));
    }

    public List<DemandeProduit> getAll() {
        return demandeProduitDAO.findAll();
    }

    public List<DemandeProduit> getByStatut(DemandeProduit.Statut statut) {
        return demandeProduitDAO.findByStatut(statut);
    }

    public List<DemandeProduit> getByMedecinId(int medecinId) {
        return demandeProduitDAO.findByMedecinId(medecinId);
    }

    public DemandeProduit save(DemandeProduit demande) {
        if (demande.getId() == 0) {
            int id = demandeProduitDAO.insert(demande);
            demande.setId(id);
        } else {
            demandeProduitDAO.update(demande);
        }
        return demande;
    }

    public void delete(int id) {
        demandeProduitDAO.delete(id);
    }
}
