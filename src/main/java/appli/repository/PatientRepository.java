package appli.repository;

import appli.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    Patient save(Patient patient);

    Patient update(Patient patient);

    Optional<Patient> findById(int id);

    Optional<Patient> findBySsn(String numeroSecuriteSociale);

    List<Patient> findAll();

    List<Patient> searchByName(String searchTerm);

    boolean existsBySsn(String numeroSecuriteSociale);

    void delete(int id);
}
