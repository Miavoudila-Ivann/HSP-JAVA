package appli.repository;

import appli.model.Chambre;

import java.util.List;
import java.util.Optional;

public interface ChambreRepository {

    Optional<Chambre> findById(int id);

    List<Chambre> findAvailableRooms();

    Optional<Chambre> findAvailableRoomForUpdate();

    void setAvailable(int chambreId, boolean available);
}
