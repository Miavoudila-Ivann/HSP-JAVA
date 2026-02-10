package appli.dao;

import appli.model.LigneOrdonnance;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LigneOrdonnanceDAO {

    public LigneOrdonnance findById(int id) {
        String sql = "SELECT * FROM lignes_ordonnance WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLigneOrdonnance(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la ligne d'ordonnance : " + e.getMessage());
        }
        return null;
    }

    public List<LigneOrdonnance> findByOrdonnanceId(int ordonnanceId) {
        List<LigneOrdonnance> lignes = new ArrayList<>();
        String sql = "SELECT * FROM lignes_ordonnance WHERE ordonnance_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ordonnanceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lignes.add(mapResultSetToLigneOrdonnance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des lignes par ordonnance : " + e.getMessage());
        }
        return lignes;
    }

    public List<LigneOrdonnance> findByProduitId(int produitId) {
        List<LigneOrdonnance> lignes = new ArrayList<>();
        String sql = "SELECT * FROM lignes_ordonnance WHERE produit_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lignes.add(mapResultSetToLigneOrdonnance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des lignes par produit : " + e.getMessage());
        }
        return lignes;
    }

    public int insert(LigneOrdonnance ligne) {
        String sql = "INSERT INTO lignes_ordonnance (ordonnance_id, produit_id, posologie, quantite, duree_jours, frequence, voie_administration, instructions, date_debut, date_fin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ligne.getOrdonnanceId());
            stmt.setInt(2, ligne.getProduitId());
            stmt.setString(3, ligne.getPosologie());
            stmt.setInt(4, ligne.getQuantite());
            if (ligne.getDureeJours() != null) {
                stmt.setInt(5, ligne.getDureeJours());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setString(6, ligne.getFrequence());
            stmt.setString(7, ligne.getVoieAdministration().name());
            stmt.setString(8, ligne.getInstructions());
            stmt.setDate(9, ligne.getDateDebut() != null ? Date.valueOf(ligne.getDateDebut()) : null);
            stmt.setDate(10, ligne.getDateFin() != null ? Date.valueOf(ligne.getDateFin()) : null);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la ligne d'ordonnance : " + e.getMessage());
        }
        return -1;
    }

    public boolean update(LigneOrdonnance ligne) {
        String sql = "UPDATE lignes_ordonnance SET ordonnance_id = ?, produit_id = ?, posologie = ?, quantite = ?, duree_jours = ?, frequence = ?, voie_administration = ?, instructions = ?, date_debut = ?, date_fin = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ligne.getOrdonnanceId());
            stmt.setInt(2, ligne.getProduitId());
            stmt.setString(3, ligne.getPosologie());
            stmt.setInt(4, ligne.getQuantite());
            if (ligne.getDureeJours() != null) {
                stmt.setInt(5, ligne.getDureeJours());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setString(6, ligne.getFrequence());
            stmt.setString(7, ligne.getVoieAdministration().name());
            stmt.setString(8, ligne.getInstructions());
            stmt.setDate(9, ligne.getDateDebut() != null ? Date.valueOf(ligne.getDateDebut()) : null);
            stmt.setDate(10, ligne.getDateFin() != null ? Date.valueOf(ligne.getDateFin()) : null);
            stmt.setInt(11, ligne.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise a jour de la ligne d'ordonnance : " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM lignes_ordonnance WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la ligne d'ordonnance : " + e.getMessage());
        }
        return false;
    }

    public boolean deleteByOrdonnanceId(int ordonnanceId) {
        String sql = "DELETE FROM lignes_ordonnance WHERE ordonnance_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ordonnanceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des lignes par ordonnance : " + e.getMessage());
        }
        return false;
    }

    private LigneOrdonnance mapResultSetToLigneOrdonnance(ResultSet rs) throws SQLException {
        LigneOrdonnance ligne = new LigneOrdonnance();
        ligne.setId(rs.getInt("id"));
        ligne.setOrdonnanceId(rs.getInt("ordonnance_id"));
        ligne.setProduitId(rs.getInt("produit_id"));
        ligne.setPosologie(rs.getString("posologie"));
        ligne.setQuantite(rs.getInt("quantite"));
        int dureeJours = rs.getInt("duree_jours");
        if (!rs.wasNull()) {
            ligne.setDureeJours(dureeJours);
        }
        ligne.setFrequence(rs.getString("frequence"));
        String voie = rs.getString("voie_administration");
        if (voie != null) {
            ligne.setVoieAdministration(LigneOrdonnance.VoieAdministration.valueOf(voie));
        }
        ligne.setInstructions(rs.getString("instructions"));
        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) {
            ligne.setDateDebut(dateDebut.toLocalDate());
        }
        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            ligne.setDateFin(dateFin.toLocalDate());
        }
        return ligne;
    }
}
