package appli.dao;

import appli.model.DemandeProduit;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
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

    public DemandeProduit findByNumeroDemande(String numeroDemande) {
        String sql = "SELECT * FROM demandes_produits WHERE numero_demande = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroDemande);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDemandeProduit(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la demande par numero : " + e.getMessage());
        }
        return null;
    }

    public List<DemandeProduit> findAll() {
        List<DemandeProduit> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_produits ORDER BY urgence DESC, priorite ASC, date_demande";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
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
        String sql = "SELECT * FROM demandes_produits WHERE statut = ? ORDER BY urgence DESC, priorite ASC, date_demande";
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

    public List<DemandeProduit> findByDossierId(int dossierId) {
        List<DemandeProduit> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_produits WHERE dossier_id = ? ORDER BY date_demande DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dossierId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des demandes par dossier : " + e.getMessage());
        }
        return demandes;
    }

    public List<DemandeProduit> findEnAttente() {
        List<DemandeProduit> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_produits WHERE statut = 'EN_ATTENTE' ORDER BY urgence DESC, priorite ASC, date_demande";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des demandes en attente : " + e.getMessage());
        }
        return demandes;
    }

    public int insert(DemandeProduit demande) {
        String sql = "INSERT INTO demandes_produits (numero_demande, produit_id, quantite_demandee, quantite_livree, medecin_id, " +
                "dossier_id, hospitalisation_id, ordonnance_id, emplacement_destination_id, date_demande, date_besoin, urgence, " +
                "priorite, motif, statut, gestionnaire_id, date_traitement, commentaire_traitement, date_livraison, livreur_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, demande.getNumeroDemande());
            stmt.setInt(2, demande.getProduitId());
            stmt.setInt(3, demande.getQuantiteDemandee());
            if (demande.getQuantiteLivree() != null) {
                stmt.setInt(4, demande.getQuantiteLivree());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, demande.getMedecinId());
            if (demande.getDossierId() != null) {
                stmt.setInt(6, demande.getDossierId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (demande.getHospitalisationId() != null) {
                stmt.setInt(7, demande.getHospitalisationId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            if (demande.getOrdonnanceId() != null) {
                stmt.setInt(8, demande.getOrdonnanceId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            if (demande.getEmplacementDestinationId() != null) {
                stmt.setInt(9, demande.getEmplacementDestinationId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setTimestamp(10, Timestamp.valueOf(demande.getDateDemande()));
            if (demande.getDateBesoin() != null) {
                stmt.setDate(11, Date.valueOf(demande.getDateBesoin()));
            } else {
                stmt.setNull(11, Types.DATE);
            }
            stmt.setBoolean(12, demande.isUrgence());
            stmt.setInt(13, demande.getPriorite());
            stmt.setString(14, demande.getMotif());
            stmt.setString(15, demande.getStatut().name());
            if (demande.getGestionnaireId() != null) {
                stmt.setInt(16, demande.getGestionnaireId());
            } else {
                stmt.setNull(16, Types.INTEGER);
            }
            if (demande.getDateTraitement() != null) {
                stmt.setTimestamp(17, Timestamp.valueOf(demande.getDateTraitement()));
            } else {
                stmt.setNull(17, Types.TIMESTAMP);
            }
            stmt.setString(18, demande.getCommentaireTraitement());
            if (demande.getDateLivraison() != null) {
                stmt.setTimestamp(19, Timestamp.valueOf(demande.getDateLivraison()));
            } else {
                stmt.setNull(19, Types.TIMESTAMP);
            }
            if (demande.getLivreurId() != null) {
                stmt.setInt(20, demande.getLivreurId());
            } else {
                stmt.setNull(20, Types.INTEGER);
            }
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
        String sql = "UPDATE demandes_produits SET numero_demande = ?, produit_id = ?, quantite_demandee = ?, quantite_livree = ?, " +
                "medecin_id = ?, dossier_id = ?, hospitalisation_id = ?, ordonnance_id = ?, emplacement_destination_id = ?, " +
                "date_demande = ?, date_besoin = ?, urgence = ?, priorite = ?, motif = ?, statut = ?, gestionnaire_id = ?, " +
                "date_traitement = ?, commentaire_traitement = ?, date_livraison = ?, livreur_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, demande.getNumeroDemande());
            stmt.setInt(2, demande.getProduitId());
            stmt.setInt(3, demande.getQuantiteDemandee());
            if (demande.getQuantiteLivree() != null) {
                stmt.setInt(4, demande.getQuantiteLivree());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, demande.getMedecinId());
            if (demande.getDossierId() != null) {
                stmt.setInt(6, demande.getDossierId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (demande.getHospitalisationId() != null) {
                stmt.setInt(7, demande.getHospitalisationId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            if (demande.getOrdonnanceId() != null) {
                stmt.setInt(8, demande.getOrdonnanceId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            if (demande.getEmplacementDestinationId() != null) {
                stmt.setInt(9, demande.getEmplacementDestinationId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setTimestamp(10, Timestamp.valueOf(demande.getDateDemande()));
            if (demande.getDateBesoin() != null) {
                stmt.setDate(11, Date.valueOf(demande.getDateBesoin()));
            } else {
                stmt.setNull(11, Types.DATE);
            }
            stmt.setBoolean(12, demande.isUrgence());
            stmt.setInt(13, demande.getPriorite());
            stmt.setString(14, demande.getMotif());
            stmt.setString(15, demande.getStatut().name());
            if (demande.getGestionnaireId() != null) {
                stmt.setInt(16, demande.getGestionnaireId());
            } else {
                stmt.setNull(16, Types.INTEGER);
            }
            if (demande.getDateTraitement() != null) {
                stmt.setTimestamp(17, Timestamp.valueOf(demande.getDateTraitement()));
            } else {
                stmt.setNull(17, Types.TIMESTAMP);
            }
            stmt.setString(18, demande.getCommentaireTraitement());
            if (demande.getDateLivraison() != null) {
                stmt.setTimestamp(19, Timestamp.valueOf(demande.getDateLivraison()));
            } else {
                stmt.setNull(19, Types.TIMESTAMP);
            }
            if (demande.getLivreurId() != null) {
                stmt.setInt(20, demande.getLivreurId());
            } else {
                stmt.setNull(20, Types.INTEGER);
            }
            stmt.setInt(21, demande.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la demande : " + e.getMessage());
        }
        return false;
    }

    public boolean updateStatut(int id, DemandeProduit.Statut statut, Integer gestionnaireId,
                                String commentaire, LocalDateTime dateTraitement) {
        String sql = "UPDATE demandes_produits SET statut = ?, gestionnaire_id = ?, " +
                "commentaire_traitement = ?, date_traitement = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            if (gestionnaireId != null) {
                stmt.setInt(2, gestionnaireId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, commentaire);
            if (dateTraitement != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(dateTraitement));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour du statut de la demande : " + e.getMessage());
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
        demande.setNumeroDemande(rs.getString("numero_demande"));
        demande.setProduitId(rs.getInt("produit_id"));
        demande.setQuantiteDemandee(rs.getInt("quantite_demandee"));

        int quantiteLivree = rs.getInt("quantite_livree");
        if (!rs.wasNull()) {
            demande.setQuantiteLivree(quantiteLivree);
        }

        demande.setMedecinId(rs.getInt("medecin_id"));

        int dossierId = rs.getInt("dossier_id");
        if (!rs.wasNull()) {
            demande.setDossierId(dossierId);
        }

        int hospitalisationId = rs.getInt("hospitalisation_id");
        if (!rs.wasNull()) {
            demande.setHospitalisationId(hospitalisationId);
        }

        int ordonnanceId = rs.getInt("ordonnance_id");
        if (!rs.wasNull()) {
            demande.setOrdonnanceId(ordonnanceId);
        }

        int emplacementDestinationId = rs.getInt("emplacement_destination_id");
        if (!rs.wasNull()) {
            demande.setEmplacementDestinationId(emplacementDestinationId);
        }

        Timestamp dateDemande = rs.getTimestamp("date_demande");
        if (dateDemande != null) {
            demande.setDateDemande(dateDemande.toLocalDateTime());
        }

        Date dateBesoin = rs.getDate("date_besoin");
        if (dateBesoin != null) {
            demande.setDateBesoin(dateBesoin.toLocalDate());
        }

        demande.setUrgence(rs.getBoolean("urgence"));
        demande.setPriorite(rs.getInt("priorite"));
        demande.setMotif(rs.getString("motif"));

        String statut = rs.getString("statut");
        if (statut != null) {
            demande.setStatut(DemandeProduit.Statut.valueOf(statut));
        }

        int gestionnaireId = rs.getInt("gestionnaire_id");
        if (!rs.wasNull()) {
            demande.setGestionnaireId(gestionnaireId);
        }

        Timestamp dateTraitement = rs.getTimestamp("date_traitement");
        if (dateTraitement != null) {
            demande.setDateTraitement(dateTraitement.toLocalDateTime());
        }

        demande.setCommentaireTraitement(rs.getString("commentaire_traitement"));

        Timestamp dateLivraison = rs.getTimestamp("date_livraison");
        if (dateLivraison != null) {
            demande.setDateLivraison(dateLivraison.toLocalDateTime());
        }

        int livreurId = rs.getInt("livreur_id");
        if (!rs.wasNull()) {
            demande.setLivreurId(livreurId);
        }

        return demande;
    }
}
