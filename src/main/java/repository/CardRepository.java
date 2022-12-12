package repository;

import constants.Constants;
import model.Card;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CardRepository implements AutoCloseable {

    private final Connection connection;

    public CardRepository() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(Constants.DB_URL, Constants.USERNAME, Constants.PASSWORD);
    }

    public boolean add(Card card) {
        String sql = "INSERT INTO card (question, answer, category_id, creationDate) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, card.getQuestion());
            ps.setString(2, card.getAnswer());
            ps.setInt(3, card.getCategoryId());
            ps.setDate(4, Date.valueOf(card.getCreationDate()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows <= 0) return false;

//            getGeneratedKeys() Retrieves any auto-generated keys created as a result of executing this Statement object.
//            If this Statement object did not generate any keys, an empty ResultSet object is returned.

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next())
                    card.setId(generatedKeys.getInt(1));
            }

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public List<Card> getCards() {
        String sql = "SELECT * FROM card";
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    cards.add(
                            new Card()
                                    .setId(resultSet.getInt(1))
                                    .setQuestion(resultSet.getString(2))
                                    .setAnswer(resultSet.getString(3))
                                    .setCategoryId(resultSet.getInt(4))
                                    .setCreationDate(resultSet.getDate(5).toLocalDate())
                    );
                }
            }
        } catch (Exception ignored) {
        }
        return cards;
    }

    public Card getById(int id) {
        String sql = "SELECT * FROM card WHERE card_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet resultSet = ps.executeQuery();
                 CategoryRepository categoryRepository = new CategoryRepository()) {
                if (resultSet.next()) {
                    int cardId = resultSet.getInt(1);
                    String question = resultSet.getString(2);
                    String answer = resultSet.getString(3);
                    int categoryId = resultSet.getInt(4);
                    LocalDate creationDate = resultSet.getDate(5).toLocalDate();

                    return new Card(cardId, question, answer, categoryId, creationDate);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<Card> getByCategoryId(int categoryId) {
        String sql = "SELECT * FROM card WHERE category_id = ?";
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    cards.add(
                            new Card()
                                    .setId(resultSet.getInt(1))
                                    .setQuestion(resultSet.getString(2))
                                    .setAnswer(resultSet.getString(3))
                                    .setCategoryId(resultSet.getInt(4))
                                    .setCreationDate(resultSet.getDate(5).toLocalDate())
                    );
                }
            }
        } catch (Exception ignored) {
        }
        return cards;
    }

    public boolean delete(Card card) {
        String sql = "DELETE FROM card WHERE card_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, card.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean update(Card card) {
        String sql = "UPDATE card SET question=?, answer=?, category_id=?, creationDate=? WHERE card_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, card.getQuestion());
            ps.setString(2, card.getAnswer());
            ps.setInt(3, card.getCategoryId());
            ps.setDate(4, Date.valueOf(card.getCreationDate()));
            ps.setInt(5, card.getId());

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
