package appli.dao;

import appli.model.DemandeProduit;
import appli.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeProduitDAO {

    public DemandeProduit findById(int id) {
        String sql = "SELECT * FROM demandes_produits WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDemandeProduit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la demande : " + e.getMessage());
        }
        return null;
    }

    public List<DemandeProduit> findAll() {
        List<DemandeProduit> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_produits ORDER BY urgence DESC, date_demande";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recuperation des demandes : " + e.getMessage());
        }
        return demandes;
    }

    public List<DemandeProduit> findByStatut(DemandeProduit.Statut statut) {
        List<DemandeProduit> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_produits WHERE statut = ? ORDER BY urgence DESC, date_demande";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des demandes par statut : " + e.getMessage());
        }
        return demandes;
    }

    public List<DemandeProduit> findByMedecinId(int medecinId) {
        List<DemandeProduit> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_produits WHERE medecin_id = ? ORDER BY date_demande DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medecinId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des demandes par medecin : " + e.getMessage());
        }
        return demandes;
    }

    public int insert(DemandeProduit demande) {
        String sql = "INSERT INTO demandes_produits (produit_id, quantite_demandee, medecin_id, dossier_id, date_demande, urgence, motif, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, demande.getProduitId());
            stmt.setInt(2, demande.getQuantiteDemandee());
            stmt.setInt(3, demande.getMedecinId());
            if (demande.getDossierId() != null) {
                stmt.setInt(4, demande.getDossierId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setTimestamp(5, Timestamp.valueOf(demande.getDateDemande()));
            stmt.setBoolean(6, demande.isUrgence());
            stmt.setString(7, demande.getMotif());
            stmt.setString(8, demande.getStatut().name());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la demande : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(DemandeProduit demande) {
        String sql = "UPDATE demandes_produits SET produit_id = ?, quantite_demandee = ?, medecin_id = ?, dossier_id = ?, urgence = ?, motif = ?, statut = ?, gestionnaire_id = ?, date_traitement = ?, commentaire_traitement = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, demande.getProduitId());
            stmt.setInt(2, demande.getQuantiteDemandee());
            stmt.setInt(3, demande.getMedecinId());
            if (demande.getDossierId() != null) {
                stmt.setInt(4, demande.getDossierId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setBoolean(5, demande.isUrgence());
            stmt.setString(6, demande.getMotif());
            stmt.setString(7, demande.getStatut().name());
            if (demande.getGestionnaireId() != null) {
                stmt.setInt(8, demande.getGestionnaireId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.setTimestamp(9, demande.getDateTraitement() != null ? Timestamp.valueOf(demande.getDateTraitement()) : null);
            stmt.setString(10, demande.getCommentaireTraitement());
            stmt.setInt(11, demande.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la demande : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM demandes_produits WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la demande : " + e.getMessage());
        }
        return false;
    }

    private DemandeProduit mapResultSetToDemandeProduit(ResultSet rs) throws SQLException {
        DemandeProduit demande = new DemandeProduit();
        demande.setId(rs.getInt("id"));
        demande.setProduitId(rs.getInt("produit_id"));
        demande.setQuantiteDemandee(rs.getInt("quantite_demandee"));
        demande.setMedecinId(rs.getInt("medecin_id"));
        int dossierId = rs.getInt("dossier_id");
        if (!rs.wasNull()) {
            demande.setDossierId(dossierId);
        }
        Timestamp dateDemande = rs.getTimestamp("date_demande");
        if (dateDemande != null) {
            demande.setDateDemande(dateDemande.toLocalDateTime());
        }
        demande.setUrgence(rs.getBoolean("urgence"));
        demande.setMotif(rs.getString("motif"));
        demande.setStatut(DemandeProduit.Statut.valueOf(rs.getString("statut")));
        int gestionnaireId = rs.getInt("gestionnaire_id");
        if (!rs.wasNull()) {
            demande.setGestionnaireId(gestionnaireId);
        }
        Timestamp dateTraitement = rs.getTimestamp("date_traitement");
        if (dateTraitement != null) {
            demande.setDateTraitement(dateTraitement.toLocalDateTime());
        }
        demande.setCommentaireTraitement(rs.getString("commentaire_traitement"));
        return demande;
    }
}
