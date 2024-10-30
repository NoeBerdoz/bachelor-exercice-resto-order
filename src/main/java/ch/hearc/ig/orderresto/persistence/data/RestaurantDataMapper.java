package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.criteria.Condition;
import ch.hearc.ig.orderresto.persistence.criteria.Criteria;
import ch.hearc.ig.orderresto.utils.OracleConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDataMapper implements DataMapper<Restaurant> {
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_YELLOW = "\u001B[33m";
    final String ANSI_BLUE = "\u001B[34m";
    final String ANSI_MAGENTA = "\u001B[35m";
    final String ANSI_CYAN = "\u001B[36m";
    final String ANSI_WHITE = "\u001B[37m";
    final String ANSI_RESET = "\u001B[0m";
    private static final Logger logger = LoggerFactory.getLogger(RestaurantDataMapper.class);


    public RestaurantDataMapper() {
    }

    // Insert a Restaurant to the database.
    @Override
    public void insert(Restaurant restaurant) throws SQLException {
        // TODO insert the products of the restaurant

        String sql = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = OracleConnector.getConnectionFromPool();
                PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"})
        ) {
            statement.setString(1, restaurant.getName());
            statement.setString(2, restaurant.getAddress().getPostalCode());
            statement.setString(3, restaurant.getAddress().getLocality());
            statement.setString(4, restaurant.getAddress().getStreet());
            statement.setString(5, restaurant.getAddress().getStreetNumber());
            statement.setString(6, restaurant.getAddress().getCountryCode());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                // Set the new generated ID to the restaurant object
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    restaurant.setId(Long.valueOf(generatedId));
                    logger.info(ANSI_GREEN + "[INSERTED]" + ANSI_RESET + " RESTAURANT WITH ID {}", restaurant.getId());
                }
            }

        } catch (SQLException e) {
            logger.error("Error while inserting restaurant: {}", e.getMessage());
        }

    }

    // Update a Restaurant in the database based on its id.
    @Override
    public void update(Restaurant restaurant) {

        String sql = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";

        try (
                Connection connection = OracleConnector.getConnectionFromPool();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, restaurant.getName());
            statement.setString(2, restaurant.getAddress().getPostalCode());
            statement.setString(3, restaurant.getAddress().getLocality());
            statement.setString(4, restaurant.getAddress().getStreet());
            statement.setString(5, restaurant.getAddress().getStreetNumber());
            statement.setString(6, restaurant.getAddress().getCountryCode());
            statement.setLong(7, restaurant.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                logger.info(ANSI_GREEN + "[UPDATED]" + ANSI_RESET + " RESTAURANT WITH ID {}", restaurant.getId());
            }
        } catch (SQLException e) {
            logger.error("Error while updating restaurant: {}", e.getMessage());
        }
    }

    // Delete a Restaurant in the database based on its id
    @Override
    public boolean delete(Restaurant restaurant) {
        String sql = "DELETE FROM RESTAURANT WHERE NUMERO = ?";

        try (
                Connection connection = OracleConnector.getConnectionFromPool();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, restaurant.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                logger.info(ANSI_RED + "[DELETED]" + ANSI_RESET + " RESTAURANT WITH ID {}", restaurant.getId());
                return true;
            } else {
                logger.info(ANSI_YELLOW + "[DELETED]" + ANSI_RESET + " NO RESTAURANT FOUND WITH ID {}", restaurant.getId());
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error while deleting restaurant: {}", e.getMessage());
            return false;
        }
    }

    // Retrieve a Restaurant by its ID.
    @Override
    public Restaurant selectById(Long id) throws SQLException {
        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";

        Connection connection = OracleConnector.getConnectionFromPool();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        ResultSet resultSet = statement.executeQuery();

        // Move cursor to the first result row
        if (resultSet.next()) {
            return mapToObject(resultSet);
        }

        return null;
    }

    // Retrieves all restaurants from the database.
    @Override
    public List<Restaurant> selectAll() throws SQLException {

        String sql = "SELECT * FROM RESTAURANT";

        List<Restaurant> restaurants = new ArrayList<>();

        try (
                Connection connection = OracleConnector.getConnectionFromPool();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            Integer countRestaurant = 0;
            while (resultSet.next()) {
                Restaurant restaurant = mapToObject(resultSet);
                restaurants.add(restaurant);
                countRestaurant++;
            }
            logger.info(ANSI_MAGENTA + "[SELECTED]" + ANSI_RESET + " RESTAURANT COUNT {}", countRestaurant);
        } catch (SQLException e) {
            logger.error("Error while fetching restaurants: {}", e.getMessage());
        }

        return restaurants;
    }

    // Retrieve a Restaurant based on a search
    @Override
    public List<Restaurant> selectWhere(Criteria criteria) {

        StringBuilder sql = new StringBuilder("SELECT * FROM RESTAURANT");

        List<Condition> conditions = criteria.getConditions();
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            for (Condition condition : conditions) {
                sql.append(condition.getColumnName())
                   .append(" ")
                   .append(condition.getOperator())
                   .append(" ? AND ");
            }
            // Remove the last " AND "
            sql.setLength(sql.length() - 5);
        }

        List<Restaurant> foundRestaurants = new ArrayList<>();

        try (
                Connection connection = OracleConnector.getConnectionFromPool();
                PreparedStatement statement = connection.prepareStatement(sql.toString())
        ) {
            int index = 1;

            // Prepare SQL query dynamically
            for (Condition condition : conditions) {
                if (condition.getOperator().equals("LIKE")) {
                    statement.setString(index++, condition.getValue().toString());
                } else {
                    statement.setObject(index++, condition.getValue());
                }
            }
            Integer countRestaurant = 0;
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                foundRestaurants.add(mapToObject(resultSet));
                countRestaurant++;
            }
            logger.info(ANSI_MAGENTA + "[SELECTED]" + ANSI_RESET + " RESTAURANT COUNT {}", countRestaurant);
            return foundRestaurants;

        } catch (SQLException e) {
            logger.error("Error while fetching restaurants: {}", e.getMessage());
            return null;
        }
    }

    // Map an SQL ResultSet to a Restaurant Java object.
    public Restaurant mapToObject(ResultSet resultSet) throws SQLException {

        Address address = new Address(
                resultSet.getString("pays"),
                resultSet.getString("code_postal"),
                resultSet.getString("localite"),
                resultSet.getString("rue"),
                resultSet.getString("num_rue")
        );

        return new Restaurant.Builder()
                .withId(resultSet.getLong("numero"))
                .withName(resultSet.getString("nom"))
                .withAddress(address)
                .build();
    }

}
