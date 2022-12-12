package repository;

import constants.Constants;
import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements AutoCloseable {

    private final Connection connection;

    public CategoryRepository() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);
    }

    public boolean add(Category category) {
        String sql = "INSERT INTO category (name, user_id) VALUES (?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            ps.setInt(2, category.getUserId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows <= 0) return false;

//            getGeneratedKeys() Retrieves any auto-generated keys created as a result of executing this Statement object.
//            If this Statement object did not generate any keys, an empty ResultSet object is returned.

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next())
                    category.setId(generatedKeys.getInt(1));
            }

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public List<Category> getCategories() {
        String sql = "SELECT * FROM category";
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Category category = new Category()
                            .setId(resultSet.getInt(1))
                            .setName(resultSet.getString(2))
                            .setUserId(resultSet.getInt(3));

                    categories.add(category);
                }
            }
        } catch (Exception ignored) {
        }
        return categories;
    }

    public List<Category> getCategories(int userId) {
        String sql = "SELECT * FROM category WHERE user_id = ?";
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Category category = new Category()
                            .setId(resultSet.getInt(1))
                            .setName(resultSet.getString(2))
                            .setUserId(resultSet.getInt(3));

                    categories.add(category);
                }
            }
        } catch (Exception ignored) {
        }
        return categories;
    }

    public Category getById(int id) {
        String sql = "SELECT * FROM category WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int categoryId = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    int userId = resultSet.getInt(3);

                    return new Category(categoryId, name, userId);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public boolean delete(Category category) {
        String sql = "DELETE FROM category WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, category.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean update(Category category) {
        String sql = "UPDATE category SET name=?, user_id=? WHERE category_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setInt(2, category.getUserId());
            ps.setInt(3, category.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
