package appli.repository;

import appli.dao.ChambreDAO;
import appli.model.Chambre;

import java.util.List;
import java.util.Optional;

public class ChambreRepository {

    private final ChambreDAO chambreDAO = new ChambreDAO();

    public Optional<Chambre> getById(int id) {
        return Optional.ofNullable(chambreDAO.findById(id));
    }

    public Optional<Chambre> getByNumero(String numero) {
        return Optional.ofNullable(chambreDAO.findByNumero(numero));
    }

    public List<Chambre> getAll() {
        return chambreDAO.findAll();
    }

    public List<Chambre> getDisponibles() {
        return chambreDAO.findDisponibles();
    }

    public List<Chambre> getByType(Chambre.TypeChambre type) {
        return chambreDAO.findByType(type);
    }

    public Chambre save(Chambre chambre) {
        if (chambre.getId() == 0) {
            int id = chambreDAO.insert(chambre);
            chambre.setId(id);
        } else {
            chambreDAO.update(chambre);
        }
        return chambre;
    }

    public void updateOccupation(int chambreId, boolean occupee) {
        chambreDAO.updateOccupation(chambreId, occupee);
    }

    public void delete(int id) {
        chambreDAO.delete(id);
    }
}
