package appli.repository;

import appli.dao.DossierPriseEnChargeDAO;
import appli.model.DossierPriseEnCharge;

import java.util.List;
import java.util.Optional;

public class DossierPriseEnChargeRepository {

    private final DossierPriseEnChargeDAO dossierDAO = new DossierPriseEnChargeDAO();

    public Optional<DossierPriseEnCharge> getById(int id) {
        return Optional.ofNullable(dossierDAO.findById(id));
    }

    public List<DossierPriseEnCharge> getByPatientId(int patientId) {
        return dossierDAO.findByPatientId(patientId);
    }

    public List<DossierPriseEnCharge> getAll() {
        return dossierDAO.findAll();
    }

    public List<DossierPriseEnCharge> getByStatut(DossierPriseEnCharge.Statut statut) {
        return dossierDAO.findByStatut(statut);
    }

    public List<DossierPriseEnCharge> getEnAttenteTriage() {
        return dossierDAO.findEnAttenteTriage();
    }

    public DossierPriseEnCharge save(DossierPriseEnCharge dossier) {
        if (dossier.getId() == 0) {
            int id = dossierDAO.insert(dossier);
            dossier.setId(id);
        } else {
            dossierDAO.update(dossier);
        }
        return dossier;
    }

    public void delete(int id) {
        dossierDAO.delete(id);
    }
}
