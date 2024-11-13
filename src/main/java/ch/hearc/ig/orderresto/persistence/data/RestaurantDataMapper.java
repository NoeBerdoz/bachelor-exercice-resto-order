package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.helper.CacheProvider;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.*;
import java.util.*;

/**
 * DataMapper implementation for managing Restaurant entities in the database.
 * Provides CRUD operations and caching for restaurants.
 */
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

    /**
     * Inserts a new restaurant into the database.
     *
     * @param restaurant the restaurant to insert
     * @return true if the restaurant was successfully inserted, false otherwise
     * @throws SQLException if a database error occurs
     */
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
                    SimpleLogger.info("[DB][INSERTED] RESTAURANT ID: " + restaurant.getId());

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

    /**
     * Updates an existing restaurant in the database.
     *
     * @param restaurant the restaurant to update
     * @return true if the restaurant was successfully updated, false otherwise
     * @throws SQLException if a database error occurs
     */
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
                SimpleLogger.info("[DB][UPDATED] RESTAURANT ID: " + restaurant.getId());

                cacheProvider.cache.put(restaurant.getId(), restaurant);

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DB][UPDATE] NO RESTAURANT FOUND WITH ID: " + restaurant.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while updating restaurant: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Deletes a restaurant from the database.
     *
     * @param restaurant the restaurant to delete
     * @return true if the restaurant was successfully deleted, false otherwise
     * @throws SQLException if a database error occurs
     */
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
                SimpleLogger.info("[DB][DELETED] RESTAURANT ID: " + restaurant.getId());

                cacheProvider.cache.remove(restaurant.getId());

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DB][DELETE] NO RESTAURANT FOUND WITH ID: " + restaurant.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting restaurant: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Retrieves a restaurant by its ID.
     *
     * @param id the ID of the restaurant to retrieve in the database
     * @return an Optional containing the restaurant if found, or an empty Optional if not found
     * @throws SQLException if a database error occurs
     */
    @Override
    public Optional<Restaurant> selectById(Long id) throws SQLException {

        // Check the cache first
        if (cacheProvider.cache.containsKey(id)) {
            SimpleLogger.info("[CACHE][SELECTED] RESTAURANT ID: " + id);
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

                SimpleLogger.info("[DB][SELECTED] RESTAURANT ID: " + id);
                return Optional.of(restaurant);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching restaurant by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();

    }

    /**
     * Retrieves all restaurants from the database.
     * Set the cache valid once the data is fetched from the database.
     *
     * @return a list of all restaurants
     * @throws SQLException if a database error occurs
     */
    @Override
    public List<Restaurant> selectAll() throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid()){
            SimpleLogger.info("[CACHE][SELECTED] ALL RESTAURANT" );
            return new ArrayList<>(cacheProvider.cache.values());
        }

        String sql = "SELECT * FROM RESTAURANT";

        List<Restaurant> restaurants = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Restaurant restaurant = mapToObject(resultSet);
                restaurants.add(restaurant);

                cacheProvider.cache.put(restaurant.getId(), restaurant);
            }

            // set cache valid, we want to fetch the all data only once
            cacheProvider.setCacheValid();

            SimpleLogger.info("[DB][SELECTED] ALL RESTAURANT");
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching restaurants: " + e.getMessage());
            throw e;
        }

        return restaurants;
    }

    /**
     * Maps a SQL ResultSet to a Restaurant object.
     *
     * @param resultSet the result set to map
     * @return the mapped Restaurant object
     * @throws SQLException if a database error occurs
     */
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
