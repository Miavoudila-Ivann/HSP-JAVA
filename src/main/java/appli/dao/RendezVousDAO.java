package appli.dao;

import appli.model.Rendezvous;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    public List<Rendezvous> findAll() {
        List<Rendezvous> list = new ArrayList<>();
        String sql = """
                SELECT r.*,
                       p.nom AS patient_nom, p.prenom AS patient_prenom,
                       u.nom AS medecin_nom, u.prenom AS medecin_prenom
                FROM rendezvous r
                JOIN patients p ON r.patient_id = p.id
                JOIN users u ON r.medecin_id = u.id
                ORDER BY r.date_heure DESC
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.findAll : " + e.getMessage());
        }
        return list;
    }

    public List<Rendezvous> findAVenir() {
        List<Rendezvous> list = new ArrayList<>();
        String sql = """
                SELECT r.*,
                       p.nom AS patient_nom, p.prenom AS patient_prenom,
                       u.nom AS medecin_nom, u.prenom AS medecin_prenom
                FROM rendezvous r
                JOIN patients p ON r.patient_id = p.id
                JOIN users u ON r.medecin_id = u.id
                WHERE r.date_heure >= NOW() AND r.statut NOT IN ('ANNULE','REALISE')
                ORDER BY r.date_heure ASC
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.findAVenir : " + e.getMessage());
        }
        return list;
    }

    public List<Rendezvous> findByPatientId(int patientId) {
        List<Rendezvous> list = new ArrayList<>();
        String sql = """
                SELECT r.*,
                       p.nom AS patient_nom, p.prenom AS patient_prenom,
                       u.nom AS medecin_nom, u.prenom AS medecin_prenom
                FROM rendezvous r
                JOIN patients p ON r.patient_id = p.id
                JOIN users u ON r.medecin_id = u.id
                WHERE r.patient_id = ?
                ORDER BY r.date_heure DESC
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.findByPatientId : " + e.getMessage());
        }
        return list;
    }

    public List<Rendezvous> findByMedecinId(int medecinId) {
        List<Rendezvous> list = new ArrayList<>();
        String sql = """
                SELECT r.*,
                       p.nom AS patient_nom, p.prenom AS patient_prenom,
                       u.nom AS medecin_nom, u.prenom AS medecin_prenom
                FROM rendezvous r
                JOIN patients p ON r.patient_id = p.id
                JOIN users u ON r.medecin_id = u.id
                WHERE r.medecin_id = ?
                ORDER BY r.date_heure DESC
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medecinId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.findByMedecinId : " + e.getMessage());
        }
        return list;
    }

    public Rendezvous save(Rendezvous rdv) {
        String sql = """
                INSERT INTO rendezvous
                    (numero_rdv, patient_id, medecin_id, date_heure, duree_minutes,
                     type_rdv, statut, motif, notes, lieu, cree_par)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, rdv.getNumeroRdv());
            stmt.setInt(2, rdv.getPatientId());
            stmt.setInt(3, rdv.getMedecinId());
            stmt.setTimestamp(4, Timestamp.valueOf(rdv.getDateHeure()));
            stmt.setInt(5, rdv.getDureeMinutes());
            stmt.setString(6, rdv.getTypeRdv().name());
            stmt.setString(7, rdv.getStatut().name());
            stmt.setString(8, rdv.getMotif());
            stmt.setString(9, rdv.getNotes());
            stmt.setString(10, rdv.getLieu());
            if (rdv.getCreePar() != null) stmt.setInt(11, rdv.getCreePar());
            else stmt.setNull(11, Types.INTEGER);

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) rdv.setId(keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.save : " + e.getMessage());
            throw new RuntimeException("Erreur creation rendez-vous : " + e.getMessage());
        }
        return rdv;
    }

    public void update(Rendezvous rdv) {
        String sql = """
                UPDATE rendezvous
                SET patient_id=?, medecin_id=?, date_heure=?, duree_minutes=?,
                    type_rdv=?, statut=?, motif=?, notes=?, lieu=?
                WHERE id=?
                """;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rdv.getPatientId());
            stmt.setInt(2, rdv.getMedecinId());
            stmt.setTimestamp(3, Timestamp.valueOf(rdv.getDateHeure()));
            stmt.setInt(4, rdv.getDureeMinutes());
            stmt.setString(5, rdv.getTypeRdv().name());
            stmt.setString(6, rdv.getStatut().name());
            stmt.setString(7, rdv.getMotif());
            stmt.setString(8, rdv.getNotes());
            stmt.setString(9, rdv.getLieu());
            stmt.setInt(10, rdv.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.update : " + e.getMessage());
            throw new RuntimeException("Erreur modification rendez-vous : " + e.getMessage());
        }
    }

    public void updateStatut(int id, Rendezvous.Statut statut) {
        String sql = "UPDATE rendezvous SET statut=? WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.updateStatut : " + e.getMessage());
            throw new RuntimeException("Erreur mise a jour statut : " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM rendezvous WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.delete : " + e.getMessage());
            throw new RuntimeException("Erreur suppression rendez-vous : " + e.getMessage());
        }
    }

    public String generateNumeroRdv() {
        String prefix = "RDV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String sql = "SELECT COUNT(*) FROM rendezvous WHERE numero_rdv LIKE ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return prefix + String.format("%03d", count);
            }
        } catch (SQLException e) {
            System.err.println("RendezVousDAO.generateNumeroRdv : " + e.getMessage());
        }
        return prefix + "001";
    }

    private Rendezvous mapRow(ResultSet rs) throws SQLException {
        Rendezvous r = new Rendezvous();
        r.setId(rs.getInt("id"));
        r.setNumeroRdv(rs.getString("numero_rdv"));
        r.setPatientId(rs.getInt("patient_id"));
        r.setMedecinId(rs.getInt("medecin_id"));
        Timestamp ts = rs.getTimestamp("date_heure");
        if (ts != null) r.setDateHeure(ts.toLocalDateTime());
        r.setDureeMinutes(rs.getInt("duree_minutes"));
        r.setTypeRdv(Rendezvous.TypeRdv.valueOf(rs.getString("type_rdv")));
        r.setStatut(Rendezvous.Statut.valueOf(rs.getString("statut")));
        r.setMotif(rs.getString("motif"));
        r.setNotes(rs.getString("notes"));
        r.setLieu(rs.getString("lieu"));
        Timestamp dc = rs.getTimestamp("date_creation");
        if (dc != null) r.setDateCreation(dc.toLocalDateTime());
        int cp = rs.getInt("cree_par");
        if (!rs.wasNull()) r.setCreePar(cp);

        // Champs joints (peuvent ne pas exister selon la requete)
        try { r.setPatientNom(rs.getString("patient_nom")); } catch (SQLException ignored) {}
        try { r.setPatientPrenom(rs.getString("patient_prenom")); } catch (SQLException ignored) {}
        try { r.setMedecinNom(rs.getString("medecin_nom")); } catch (SQLException ignored) {}
        try { r.setMedecinPrenom(rs.getString("medecin_prenom")); } catch (SQLException ignored) {}

        return r;
    }
}
