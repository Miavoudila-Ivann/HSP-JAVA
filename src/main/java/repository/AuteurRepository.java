package repository;

import appli.database.Database;
import model.Auteur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class AuteurRepository {

    private Connection connection;

    public AuteurRepository(){
        connection = Database.getConnexion();
    }

    public ArrayList<Auteur> get_ALlAuteur(){
        String sql = "select * from Auteur";
        ArrayList<Auteur> listAuteur = null;
        try{
            PreparedStatement requete = this.connection.prepareStatement(sql);
            ResultSet rs = requete.executeQuery();
            listAuteur = new ArrayList<Auteur>();
            while(rs.next()){
                new Auteur(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5));
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Auteur get_Auteur(int idAuteur){
        String sql = "select * from Auteur where idAuteur = ?";
        ArrayList<Auteur> listeAuteur = null;
        return null;
    }
    public boolean addAuteur(Auteur auteur){
        return false;
    }
}
