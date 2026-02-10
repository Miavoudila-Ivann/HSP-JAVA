package appli.repository;

import appli.model.DossierPriseEnCharge;

import java.util.List;
import java.util.Optional;

public interface TriageRepository {

    DossierPriseEnCharge createCase(DossierPriseEnCharge dossier);

    DossierPriseEnCharge save(DossierPriseEnCharge dossier);

    Optional<DossierPriseEnCharge> findById(int id);

    List<DossierPriseEnCharge> listOpenCasesSorted();

    void updateStatus(int caseId, DossierPriseEnCharge.Statut statut);
}
