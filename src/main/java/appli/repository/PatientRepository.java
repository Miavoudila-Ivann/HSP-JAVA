package appli.repository;

import appli.dao.PatientDAO;
import appli.model.Patient;

import java.util.List;
import java.util.Optional;

public class PatientRepository {

    private final PatientDAO patientDAO = new PatientDAO();

    public Optional<Patient> getById(int id) {
        return Optional.ofNullable(patientDAO.findById(id));
    }

    public Optional<Patient> getByNumeroSecuriteSociale(String numeroSecu) {
        return Optional.ofNullable(patientDAO.findByNumeroSecuriteSociale(numeroSecu));
    }

    public List<Patient> getAll() {
        return patientDAO.findAll();
    }

    public List<Patient> search(String searchTerm) {
        return patientDAO.search(searchTerm);
    }

    public Patient save(Patient patient) {
        if (patient.getId() == 0) {
            int id = patientDAO.insert(patient);
            patient.setId(id);
        } else {
            patientDAO.update(patient);
        }
        return patient;
    }

    public void delete(int id) {
        patientDAO.delete(id);
    }
}
