package appli.repository;

import appli.dao.HospitalisationDAO;
import appli.model.Hospitalisation;

import java.util.List;
import java.util.Optional;

public class HospitalisationRepository {

    private final HospitalisationDAO hospitalisationDAO = new HospitalisationDAO();

    public Optional<Hospitalisation> getById(int id) {
        return Optional.ofNullable(hospitalisationDAO.findById(id));
    }

    public List<Hospitalisation> getByDossierId(int dossierId) {
        return hospitalisationDAO.findByDossierId(dossierId);
    }

    public List<Hospitalisation> getAll() {
        return hospitalisationDAO.findAll();
    }

    public List<Hospitalisation> getEnCours() {
        return hospitalisationDAO.findEnCours();
    }

    public List<Hospitalisation> getByMedecinId(int medecinId) {
        return hospitalisationDAO.findByMedecinId(medecinId);
    }

    public Hospitalisation save(Hospitalisation hospitalisation) {
        if (hospitalisation.getId() == 0) {
            int id = hospitalisationDAO.insert(hospitalisation);
            hospitalisation.setId(id);
        } else {
            hospitalisationDAO.update(hospitalisation);
        }
        return hospitalisation;
    }

    public void delete(int id) {
        hospitalisationDAO.delete(id);
    }
}
