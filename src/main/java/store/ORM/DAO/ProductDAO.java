package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.business_logic.filter.ProductFilter;
import store.domain_model.inventory.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    Connection connection;
    private ClothingDAO clothingDAO;
    private FoodDAO foodDAO;
    private FurnitureDAO furnitureDAO;

    public ProductDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
        this.clothingDAO = new ClothingDAO();
        this.foodDAO = new FoodDAO();
        this.furnitureDAO = new FurnitureDAO();
    }

    public Product getProductById(String id) {
        if(id.startsWith("CL")) {
            return clothingDAO.getClothingById(id);
        } else if (id.startsWith("FD")) {
            return foodDAO.getFoodById(id);
        } else {
            return furnitureDAO.getFurnitureById(id);
        }
    }

    public boolean checkProductId(Product p) {
        String id = p.getId();

        if (id.startsWith("CL")) {
            if (p instanceof Clothes) {
                return true;
            } else {
                System.err.println("ERROR: id 'CL' used for clothes was assigned to an item of a different type%n");
                return false;
            }
        }

        if (id.startsWith("FD")) {
            if (p instanceof Food) {
                return true;
            } else {
                System.err.println("ERROR: id 'FD' used for food was assigned to an item of a different type%n");
                return false;
            }
        }

        if (id.startsWith("FR")) {
            if (p instanceof Furniture) {
                return true;
            } else {
                System.err.println("ERROR: id 'FR' used for furniture was assigned to an item of a different type%n");
                return false;
            }
        }

        System.err.println("ERROR: unkown id%n");
        return false;

    }

    public void addProduct(Product p) {
        String sql = """
                INSERT INTO public."Product" (id, name, brand, price) VALUES
                (?, ?, ?, ?)
                """;
        if ((p.getId().startsWith("CL") || p.getId().startsWith("FD") || p.getId().startsWith("FR")) && p.getId().length() == 6) {

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, p.getId());
                pst.setString(2, p.getName());
                pst.setString(3, p.getBrand());
                pst.setDouble(4, p.getPrice());
                pst.executeUpdate();

                if (p instanceof Clothes) {
                    clothingDAO.addClothing((Clothes) p);
                }
                else if (p instanceof Food) {
                    foodDAO.addFood((Food) p);
                }
                else if (p instanceof Furniture) {
                    furnitureDAO.addFurniture((Furniture) p);
                }


            } catch (SQLException e) {
                System.err.println("ERROR WHILE REGISTERING ITEM IN PRODUCT TABLE: " + e.getMessage());
            }
        } else {
            System.err.printf("ERROR: id is wrong, it must start with CL for clothes, FR for furniture or FD for food and it must contain 6 characters. %n");
        }
    }

    public void removeProductById(String id) {
        String sql = """
                DELETE FROM public."Product" WHERE id = ?
                """;
        if ((id.startsWith("CL") || id.startsWith("FD") || id.startsWith("FR")) && id.length() == 6) {
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, id);
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.printf("Product " + id + " removed succesfully.");
                }
            } catch (SQLException e) {
                System.err.printf(e.getMessage());
            }

        } else {
            System.err.printf("ERROR: id is wrong, it must start with CL for clothes, FR for furniture or FD for food and it must contain 6 characters. %n");
        }
    }

    public List<String> searchDynamic(String id, ProductFilter filter) {
        List<String> foundIds = new ArrayList<>();

        String joinClause = "";
        if (id.startsWith("CL")) {
            joinClause = "JOIN public.\"Clothing\" v ON p.id = v.id";
        } else if (id.startsWith("FD")) {
            joinClause = "JOIN public.\"Food\" v ON p.id = v.id";
        } else if (id.startsWith("FR")) {
            joinClause = "JOIN public.\"Furniture\" v ON p.id = v.id";
        }

        StringBuilder sql = new StringBuilder(
                "SELECT p.id FROM public.\"Product\" p " +
                        joinClause +
                        " WHERE 1=1 "
        );
        List<Object> parameters = new ArrayList<>();
        filter.applyAll(sql, parameters);
        try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                pst.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                foundIds.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return foundIds;
    }

    public boolean update(Product product) {
        String sql = """
                UPDATE public."Product" SET name = ?, brand = ?, price = ?
                WHERE id = ?
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, product.getName());
            pst.setString(2, product.getBrand());
            pst.setDouble(3, product.getPrice());
            pst.setString(4, product.getId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }



}
