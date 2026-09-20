package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.inventory.Food;

import java.sql.*;

public class FoodDAO {

    Connection connection;

    public FoodDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
    }

    public Food getFoodById(String id) {
        String sql = """ 
                SELECT p.id, p.name, p.brand, p.price, f.expiration_date, f.type
                FROM public."Product" p, public."Food" f
                WHERE p.id = f.id AND f.id = ?
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1,id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return new Food(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getDate("expiration_date"),
                        rs.getString("type")
                );
            }
        } catch(SQLException e) {
            System.err.printf("ERROR: " + e.getMessage());
        }
        return null;
    }

    public void addFood(Food food) {
        String sql = """
                INSERT INTO public."Food" (id, expiration_date, type) VALUES
                (?, ?, ?)
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, food.getId());
            pst.setDate(2, new Date(food.getExpirationDate().getTime()));
            pst.setString(3, food.getType());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }
    }

    public boolean update(Food food) {
        String sql = """
                UPDATE public."Food" SET expiration_date = ?, type = ?
                WHERE id = ?
                """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setDate(1, new Date(food.getExpirationDate().getTime()));
            pst.setString(2, food.getType());
            pst.setString(3, food.getId());

            return pst.executeUpdate() > 0;
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}




