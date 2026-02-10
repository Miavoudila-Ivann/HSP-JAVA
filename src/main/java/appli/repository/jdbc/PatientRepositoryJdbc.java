package appli.repository.jdbc;

import appli.dao.PatientDAO;
import appli.model.Patient;
import appli.repository.PatientRepository;

import java.util.List;
import java.util.Optional;

public class PatientRepositoryJdbc implements PatientRepository {

    private final PatientDAO patientDAO = new PatientDAO();

    @Override
    public Patient save(Patient patient) {
        if (patient.getId() == 0) {
            int id = patientDAO.insert(patient);
            patient.setId(id);
        } else {
            patientDAO.update(patient);
        }
        return patient;
    }

    @Override
    public Patient update(Patient patient) {
        patientDAO.update(patient);
        return patient;
    }

    @Override
    public Optional<Patient> findById(int id) {
        return Optional.ofNullable(patientDAO.findById(id));
    }

    @Override
    public Optional<Patient> findBySsn(String numeroSecuriteSociale) {
        return Optional.ofNullable(patientDAO.findByNumeroSecuriteSociale(numeroSecuriteSociale));
    }

    @Override
    public List<Patient> findAll() {
        return patientDAO.findAll();
    }

    @Override
    public List<Patient> searchByName(String searchTerm) {
        return patientDAO.search(searchTerm);
    }

    @Override
    public boolean existsBySsn(String numeroSecuriteSociale) {
        return patientDAO.findByNumeroSecuriteSociale(numeroSecuriteSociale) != null;
    }

    @Override
    public void delete(int id) {
        patientDAO.delete(id);
    }
}
