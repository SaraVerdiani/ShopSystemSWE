package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.inventory.Furniture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FurnitureDAO {

    Connection connection;

    public FurnitureDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
    }

    public Furniture getFurnitureById(String id) {
        String sql = """ 
                SELECT p.id, p.name, p.brand, p.price, fr.height, fr.width, fr.length, fr.color
                FROM public."Product" p, public."Furniture" fr
                WHERE p.id = fr.id AND fr.id = ?
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1,id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return new Furniture(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getFloat("height"),
                        rs.getFloat("width"),
                        rs.getFloat("length"),
                        rs.getString("color")
                );
            }
        } catch(SQLException e) {
            System.err.printf("ERROR: " + e.getMessage());
        }
        return null;
    }

    public void addFurniture(Furniture furniture) {
        String sql = """
                INSERT INTO public."Furniture" (id, height, width, length, color) VALUES
                (?, ?, ?, ?, ?)
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, furniture.getId());
            pst.setDouble(2, furniture.getHeight());
            pst.setDouble(3, furniture.getWidth());
            pst.setDouble(4, furniture.getLength());
            pst.setString(5, furniture.getColor());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }
    }

    public boolean update(Furniture furniture) {
        String sql = """
                UPDATE public."Furniture" SET height = ?, width = ?, length = ?, color = ?
                WHERE id = ?
                """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setFloat(1, furniture.getHeight());
            pst.setFloat(2, furniture.getWidth());
            pst.setFloat(3, furniture.getLength());
            pst.setString(4, furniture.getColor());
            pst.setString(5, furniture.getId());

            return pst.executeUpdate() > 0;
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}

