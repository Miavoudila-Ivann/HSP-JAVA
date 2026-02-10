package appli.repository;

import appli.model.Hospitalisation;
import appli.model.LigneOrdonnance;
import appli.model.Ordonnance;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicalRepository {

    Ordonnance createPrescription(Ordonnance ordonnance, List<LigneOrdonnance> lignes);

    Hospitalisation createHospitalization(Hospitalisation hospitalisation);

    void endHospitalization(int hospitalisationId, LocalDateTime dateSortie,
                            String diagnosticSortie, Hospitalisation.TypeSortie typeSortie,
                            int medecinSortieId);
}
