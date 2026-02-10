package appli.repository.jdbc;

import appli.dao.HospitalisationDAO;
import appli.dao.LigneOrdonnanceDAO;
import appli.dao.OrdonnanceDAO;
import appli.model.Hospitalisation;
import appli.model.LigneOrdonnance;
import appli.model.Ordonnance;
import appli.repository.MedicalRepository;

import java.time.LocalDateTime;
import java.util.List;

public class MedicalRepositoryJdbc implements MedicalRepository {

    private final OrdonnanceDAO ordonnanceDAO = new OrdonnanceDAO();
    private final LigneOrdonnanceDAO ligneOrdonnanceDAO = new LigneOrdonnanceDAO();
    private final HospitalisationDAO hospitalisationDAO = new HospitalisationDAO();

    @Override
    public Ordonnance createPrescription(Ordonnance ordonnance, List<LigneOrdonnance> lignes) {
        int ordonnanceId = ordonnanceDAO.insert(ordonnance);
        ordonnance.setId(ordonnanceId);

        for (LigneOrdonnance ligne : lignes) {
            ligne.setOrdonnanceId(ordonnanceId);
            int ligneId = ligneOrdonnanceDAO.insert(ligne);
            ligne.setId(ligneId);
        }

        return ordonnance;
    }

    @Override
    public Hospitalisation createHospitalization(Hospitalisation hospitalisation) {
        int id = hospitalisationDAO.insert(hospitalisation);
        hospitalisation.setId(id);
        return hospitalisation;
    }

    @Override
    public void endHospitalization(int hospitalisationId, LocalDateTime dateSortie,
                                   String diagnosticSortie, Hospitalisation.TypeSortie typeSortie,
                                   int medecinSortieId) {
        hospitalisationDAO.endHospitalisation(hospitalisationId, dateSortie,
                diagnosticSortie, typeSortie, medecinSortieId);
    }
}
