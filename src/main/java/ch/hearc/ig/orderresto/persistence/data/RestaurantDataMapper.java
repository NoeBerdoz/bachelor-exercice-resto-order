package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.helper.CacheProvider;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.*;
import java.util.*;

public class RestaurantDataMapper implements DataMapper<Restaurant> {

    private static RestaurantDataMapper instance;

    public RestaurantDataMapper() {}

    public static RestaurantDataMapper getInstance() {
        if (instance == null) {
            instance = new RestaurantDataMapper();
        }
        return instance;
    }

    public static final CacheProvider<Long, Restaurant> cacheProvider = new CacheProvider<>();

    // Insert a Restaurant to the database.
    @Override
    public boolean insert(Restaurant restaurant) throws SQLException {

        String sql = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});
            StatementHelper.bindStatementParameters(
                    statement,
                    restaurant.getName(),
                    restaurant.getAddress().getPostalCode(),
                    restaurant.getAddress().getLocality(),
                    restaurant.getAddress().getStreet(),
                    restaurant.getAddress().getStreetNumber() != null ? restaurant.getAddress().getStreetNumber() : null,
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

                    cacheProvider.cache.put(restaurant.getId(), restaurant);

                    connection.commit();
                    return true;
                }
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while inserting restaurant: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    // Update a Restaurant in the database based on its id.
    @Override
    public boolean update(Restaurant restaurant) throws SQLException {

        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    restaurant.getName(),
                    restaurant.getAddress().getPostalCode(),
                    restaurant.getAddress().getLocality(),
                    restaurant.getAddress().getStreet(),
                    restaurant.getAddress().getStreetNumber() != null ? restaurant.getAddress().getStreetNumber() : null,
                    restaurant.getAddress().getCountryCode(),
                    restaurant.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[UPDATED] RESTAURANT WITH ID: " + restaurant.getId());

                cacheProvider.cache.put(restaurant.getId(), restaurant);

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[UPDATED] NO RESTAURANT TO UPDATE WITH ID: " + restaurant.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while updating restaurant: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    // Delete a Restaurant in the database based on its id
    @Override
    public boolean delete(Restaurant restaurant) throws SQLException {

        String sql = "DELETE FROM RESTAURANT WHERE NUMERO = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    restaurant.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DELETED] RESTAURANT WITH ID: " + restaurant.getId());

                cacheProvider.cache.remove(restaurant.getId());

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DELETED] NO RESTAURANT FOUND WITH ID: " + restaurant.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting restaurant: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    // Retrieve a Restaurant by its ID.
    @Override
    public Optional<Restaurant> selectById(Long id) throws SQLException {

        // Check the cache first
        if (cacheProvider.cache.containsKey(id)) {
            SimpleLogger.info("[CACHE] Selected RESTAURANT with ID: " + id);
            return Optional.of(cacheProvider.cache.get(id));
        }

        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(statement, id);

            ResultSet resultSet = statement.executeQuery();

            // Move cursor to the first result row
            if (resultSet.next()) {
                Restaurant restaurant = mapToObject(resultSet);

                cacheProvider.cache.put(restaurant.getId(), restaurant);

                SimpleLogger.info("[SELECTED] RESTAURANT with ID: " + id);
                return Optional.of(restaurant);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching restaurant by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();

    }

    // Retrieves all restaurants from the database.
    @Override
    public List<Restaurant> selectAll() throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid()){
            SimpleLogger.info("[CACHE] Selected all RESTAURANT" );
            return new ArrayList<>(cacheProvider.cache.values());
        }

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

                cacheProvider.cache.put(restaurant.getId(), restaurant);

                countRestaurant++;
            }

            // set cache valid, we want to fetch the all data only once
            cacheProvider.setCacheValid();

            SimpleLogger.info("[SELECTED] RESTAURANT COUNT: " + countRestaurant);
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching restaurants: " + e.getMessage());
            throw e;
        }

        return restaurants;
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
