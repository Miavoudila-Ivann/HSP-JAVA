package appli.repository.jdbc;

import appli.dao.DossierPriseEnChargeDAO;
import appli.model.DossierPriseEnCharge;
import appli.repository.TriageRepository;

import java.util.List;
import java.util.Optional;

public class TriageRepositoryJdbc implements TriageRepository {

    private final DossierPriseEnChargeDAO dossierDAO = new DossierPriseEnChargeDAO();

    @Override
    public DossierPriseEnCharge createCase(DossierPriseEnCharge dossier) {
        int id = dossierDAO.insert(dossier);
        dossier.setId(id);
        return dossier;
    }

    @Override
    public DossierPriseEnCharge save(DossierPriseEnCharge dossier) {
        if (dossier.getId() == 0) {
            int id = dossierDAO.insert(dossier);
            dossier.setId(id);
        } else {
            dossierDAO.update(dossier);
        }
        return dossier;
    }

    @Override
    public Optional<DossierPriseEnCharge> findById(int id) {
        return Optional.ofNullable(dossierDAO.findById(id));
    }

    @Override
    public List<DossierPriseEnCharge> listOpenCasesSorted() {
        return dossierDAO.findOpenCasesSorted();
    }

    @Override
    public void updateStatus(int caseId, DossierPriseEnCharge.Statut statut) {
        dossierDAO.updateStatut(caseId, statut);
    }
}
