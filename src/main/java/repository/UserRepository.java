package repository;
// database -> edu.yushchenko.cardfx.repository to allow CRUD operations -> edu.yushchenko.cardfx.servlet to execute commands

import constants.Constants;
import model.User;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements AutoCloseable {

    private final Connection connection;

    public UserRepository() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);
    }

    public boolean add(User user) {
        String sql = "INSERT INTO user (login, password, name, regDate) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getRegDate()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows <= 0) return false;

//            getGeneratedKeys() Retrieves any auto-generated keys created as a result of executing this Statement object.
//            If this Statement object did not generate any keys, an empty ResultSet object is returned.

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next())
                    user.setId(generatedKeys.getInt(1));
            }

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public List<User> getUsers() {
        String sql = "SELECT * FROM user";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt(1));
                    user.setLogin(resultSet.getString(2));
                    user.setPassword(resultSet.getString(3));
                    user.setName(resultSet.getString(4));
                    user.setRegDate(Date.valueOf(String.valueOf(resultSet.getDate(5))).toLocalDate());

                    users.add(user);
                }
            }
        } catch (Exception ignored) {
        }
        return users;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int userId = resultSet.getInt(1);
                    String login = resultSet.getString(2);
                    String password = resultSet.getString(3);
                    String name = resultSet.getString(4);
                    LocalDate regDate = resultSet.getDate(5).toLocalDate();

                    return new User(userId, login, password, name, regDate);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public User getUserByLoginAndPassword(String login, String password) {
        login = login.toLowerCase();
        String statement = "SELECT * FROM user WHERE login = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement)) {
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                boolean isUsernameValid = resultSet.getString("login").equals(login);
                boolean isPasswordValid = resultSet.getString("password").equals(password);

                if (isUsernameValid && isPasswordValid) {
                    int id = Integer.parseInt(resultSet.getString("user_id"));
                    String name = resultSet.getString("name");
                    LocalDate regDate = LocalDate.parse(resultSet.getString("regDate"));
                    return new User(id, login, password, name, regDate);
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    public boolean delete(User user) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean update(User user) {
        String sql = "UPDATE user SET login=?, password=?, name=?, regDate=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getRegDate()));
            ps.setInt(5, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception ignored) {
        }
        return false;
    }

//    public boolean login(String login, String password) {
//        login = login.toLowerCase();
//        String statement = "select * from user where login = ?";
//
//        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement)) {
//            preparedStatement.setString(1, login);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.isBeforeFirst()) {
//                resultSet.next();
//                boolean isUsernameValid = resultSet.getString("login").equals(login);
//                boolean isPasswordValid = resultSet.getString("password").equals(password);
//
//                if (isUsernameValid && isPasswordValid)
//                    return true;
//            }
//        } catch (Exception ignored) {
//        }
//        return false;
//    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
