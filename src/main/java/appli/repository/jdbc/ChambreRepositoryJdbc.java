package appli.repository.jdbc;

import appli.dao.ChambreDAO;
import appli.model.Chambre;
import appli.repository.ChambreRepository;

import java.util.List;
import java.util.Optional;

public class ChambreRepositoryJdbc implements ChambreRepository {

    private final ChambreDAO chambreDAO = new ChambreDAO();

    @Override
    public Optional<Chambre> findById(int id) {
        return Optional.ofNullable(chambreDAO.findById(id));
    }

    @Override
    public List<Chambre> findAvailableRooms() {
        return chambreDAO.findDisponibles();
    }

    @Override
    public Optional<Chambre> findAvailableRoomForUpdate() {
        return Optional.ofNullable(chambreDAO.findFirstAvailable());
    }

    @Override
    public void setAvailable(int chambreId, boolean available) {
        chambreDAO.setAvailable(chambreId, available);
    }
}
