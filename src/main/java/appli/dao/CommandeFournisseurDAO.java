package appli.dao;

import appli.model.CommandeFournisseur;
import appli.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeFournisseurDAO {

    public int insert(CommandeFournisseur commande) {
        String sql = "INSERT INTO commandes_fournisseurs " +
                "(numero_commande, fournisseur_id, date_commande, date_livraison_prevue, " +
                "statut, montant_ht, montant_ttc, notes, createur_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, commande.getNumeroCommande());
            stmt.setInt(2, commande.getFournisseurId());
            stmt.setTimestamp(3, commande.getDateCommande() != null
                    ? Timestamp.valueOf(commande.getDateCommande()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setObject(4, commande.getDateLivraisonPrevue());
            stmt.setString(5, commande.getStatut().name());
            stmt.setBigDecimal(6, commande.getMontantHt());
            stmt.setBigDecimal(7, commande.getMontantTtc());
            stmt.setString(8, commande.getNotes());
            stmt.setInt(9, commande.getCreateurId());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur insert CommandeFournisseur: " + e.getMessage(), e);
        }
        return -1;
    }

    public void update(CommandeFournisseur commande) {
        String sql = "UPDATE commandes_fournisseurs SET " +
                "fournisseur_id=?, date_livraison_prevue=?, date_livraison_effective=?, " +
                "statut=?, montant_ht=?, montant_ttc=?, notes=?, validateur_id=? " +
                "WHERE id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commande.getFournisseurId());
            stmt.setObject(2, commande.getDateLivraisonPrevue());
            if (commande.getDateLivraisonEffective() != null)
                stmt.setTimestamp(3, Timestamp.valueOf(commande.getDateLivraisonEffective().atStartOfDay()));
            else
                stmt.setNull(3, Types.TIMESTAMP);
            stmt.setString(4, commande.getStatut().name());
            stmt.setBigDecimal(5, commande.getMontantHt());
            stmt.setBigDecimal(6, commande.getMontantTtc());
            stmt.setString(7, commande.getNotes());
            if (commande.getValidateurId() != null) stmt.setInt(8, commande.getValidateurId());
            else stmt.setNull(8, Types.INTEGER);
            stmt.setInt(9, commande.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update CommandeFournisseur: " + e.getMessage(), e);
        }
    }

    public CommandeFournisseur findById(int id) {
        String sql = "SELECT cf.*, f.nom AS fournisseur_nom FROM commandes_fournisseurs cf " +
                "LEFT JOIN fournisseurs f ON cf.fournisseur_id = f.id WHERE cf.id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById CommandeFournisseur: " + e.getMessage(), e);
        }
        return null;
    }

    public List<CommandeFournisseur> findAll() {
        String sql = "SELECT cf.*, f.nom AS fournisseur_nom FROM commandes_fournisseurs cf " +
                "LEFT JOIN fournisseurs f ON cf.fournisseur_id = f.id ORDER BY cf.date_commande DESC";
        List<CommandeFournisseur> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll CommandeFournisseur: " + e.getMessage(), e);
        }
        return list;
    }

    public List<CommandeFournisseur> findByFournisseurId(int fournisseurId) {
        String sql = "SELECT cf.*, f.nom AS fournisseur_nom FROM commandes_fournisseurs cf " +
                "LEFT JOIN fournisseurs f ON cf.fournisseur_id = f.id " +
                "WHERE cf.fournisseur_id = ? ORDER BY cf.date_commande DESC";
        List<CommandeFournisseur> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fournisseurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByFournisseurId: " + e.getMessage(), e);
        }
        return list;
    }

    public List<CommandeFournisseur> findByStatut(CommandeFournisseur.Statut statut) {
        String sql = "SELECT cf.*, f.nom AS fournisseur_nom FROM commandes_fournisseurs cf " +
                "LEFT JOIN fournisseurs f ON cf.fournisseur_id = f.id " +
                "WHERE cf.statut = ? ORDER BY cf.date_commande DESC";
        List<CommandeFournisseur> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByStatut: " + e.getMessage(), e);
        }
        return list;
    }

    private CommandeFournisseur mapResultSet(ResultSet rs) throws SQLException {
        CommandeFournisseur c = new CommandeFournisseur();
        c.setId(rs.getInt("id"));
        c.setNumeroCommande(rs.getString("numero_commande"));
        c.setFournisseurId(rs.getInt("fournisseur_id"));
        Timestamp ts = rs.getTimestamp("date_commande");
        if (ts != null) c.setDateCommande(ts.toLocalDateTime());
        Date dlp = rs.getDate("date_livraison_prevue");
        if (dlp != null) c.setDateLivraisonPrevue(dlp.toLocalDate());
        Timestamp dle = rs.getTimestamp("date_livraison_effective");
        if (dle != null) c.setDateLivraisonEffective(dle.toLocalDateTime().toLocalDate());
        String statut = rs.getString("statut");
        if (statut != null) {
            try { c.setStatut(CommandeFournisseur.Statut.valueOf(statut)); }
            catch (IllegalArgumentException ignored) {}
        }
        c.setMontantHt(rs.getBigDecimal("montant_ht"));
        c.setMontantTtc(rs.getBigDecimal("montant_ttc"));
        c.setNotes(rs.getString("notes"));
        c.setCreateurId(rs.getInt("createur_id"));
        int valId = rs.getInt("validateur_id");
        if (!rs.wasNull()) c.setValidateurId(valId);

        // Fournisseur nom (join)
        try {
            String fNom = rs.getString("fournisseur_nom");
            if (fNom != null) {
                appli.model.Fournisseur f = new appli.model.Fournisseur();
                f.setId(c.getFournisseurId());
                f.setNom(fNom);
                c.setFournisseur(f);
            }
        } catch (SQLException ignored) {}

        return c;
    }
}
