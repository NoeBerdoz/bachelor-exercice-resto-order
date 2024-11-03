package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.filter.Condition;
import ch.hearc.ig.orderresto.persistence.filter.Filter;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantDataMapper implements DataMapper<Restaurant> {

    public RestaurantDataMapper() {
    }

    // Bind parameters to a prepared statement.
    private void bindStatementParameters(PreparedStatement statement, Object... sqlParameters) {
        for (int i = 0; i < sqlParameters.length; i++) {
            Object parameter = sqlParameters[i];
            try {
                if (parameter instanceof String) {
                    statement.setString(i + 1, (String) parameter);
                } else if (parameter instanceof Long) {
                    statement.setLong(i + 1, (Long) parameter);
                } else {
                    statement.setObject(i + 1, parameter);
                }
            } catch (SQLException e) {
                SimpleLogger.error("Error while binding statement: " + e.getMessage());
            }
        }
    }

    // Insert a Restaurant to the database.
    @Override
    public boolean insert(Restaurant restaurant) {

        String sql = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});

            bindStatementParameters(
                    statement,
                    restaurant.getName(),
                    restaurant.getAddress().getPostalCode(),
                    restaurant.getAddress().getLocality(),
                    restaurant.getAddress().getStreet(),
                    restaurant.getAddress().getStreetNumber(),
                    restaurant.getAddress().getCountryCode()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                // Set the new generated ID to the restaurant object
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    restaurant.setId(Long.valueOf(generatedId));
                    SimpleLogger.info("[INSERTED] RESTAURANT WITH ID: " + restaurant.getId());

                    return true;
                }
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while inserting restaurant: " + e.getMessage());
        }

        return false;
    }

    // Update a Restaurant in the database based on its id.
    @Override
    public boolean update(Restaurant restaurant) throws SQLException {

        String sql = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            bindStatementParameters(
                    statement,
                    restaurant.getName(),
                    restaurant.getAddress().getPostalCode(),
                    restaurant.getAddress().getLocality(),
                    restaurant.getAddress().getStreet(),
                    restaurant.getAddress().getStreetNumber(),
                    restaurant.getAddress().getCountryCode(),
                    restaurant.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[UPDATED] RESTAURANT WITH ID: " + restaurant.getId());
                return true;
            } else {
                SimpleLogger.warning("[UPDATED] NO RESTAURANT TO UPDATE WITH ID: " + restaurant.getId());
            }
        } catch (SQLException e) {
            SimpleLogger.error("Error while updating restaurant: " + e.getMessage());
            throw e;
        }

        return false;
    }

    // Delete a Restaurant in the database based on its id
    @Override
    public boolean delete(Restaurant restaurant) {
        String sql = "DELETE FROM RESTAURANT WHERE NUMERO = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            bindStatementParameters(statement, restaurant.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DELETED] RESTAURANT WITH ID: " + restaurant.getId());
                return true;
            } else {
                SimpleLogger.info("[DELETED] NO RESTAURANT FOUND WITH ID: " + restaurant.getId());
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while deleting restaurant: " + e.getMessage());
        }

        return false;
    }

    // Retrieve a Restaurant by its ID.
    @Override
    public Optional<Restaurant> selectById(Long id) {
        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            bindStatementParameters(statement, id);

            ResultSet resultSet = statement.executeQuery();

            // Move cursor to the first result row
            if (resultSet.next()) {
                return Optional.of(mapToObject(resultSet));
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching restaurant by ID: " + e.getMessage());
        }

        return Optional.empty();

    }

    // Retrieves all restaurants from the database.
    @Override
    public List<Restaurant> selectAll() {

        String sql = "SELECT * FROM RESTAURANT";

        List<Restaurant> restaurants = new ArrayList<>();

        try {

            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            Integer countRestaurant = 0;
            while (resultSet.next()) {
                Restaurant restaurant = mapToObject(resultSet);
                restaurants.add(restaurant);
                countRestaurant++;
            }
            SimpleLogger.info("[SELECTED] RESTAURANT COUNT: " + countRestaurant);
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    // Retrieve a Restaurant based on a search
    // WARNING THIS IS NOT PROPERLY IMPLEMENTED
    @Override
    public List<Restaurant> selectWhere(Filter filter) {

        StringBuilder sql = new StringBuilder("SELECT * FROM RESTAURANT");

        List<Condition> conditions = filter.getConditions();
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

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql.toString());

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
            SimpleLogger.info("[SELECTED] RESTAURANT COUNT: " + countRestaurant);
            return foundRestaurants;

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching restaurants: " + e.getMessage());
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
