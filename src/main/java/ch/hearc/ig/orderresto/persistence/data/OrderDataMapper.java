package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.helper.CacheProvider;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DataMapper implementation for managing Order entities in the database.
 * Provides CRUD operations and caching for orders.
 */
public class OrderDataMapper implements DataMapper<Order> {

    private static OrderDataMapper instance;

    public OrderDataMapper() {}

    public static OrderDataMapper getInstance() {
        if (instance == null) {
            instance = new OrderDataMapper();
        }
        return instance;
    }

    public static final CacheProvider<Long, Order> cacheProvider = new CacheProvider<>();

    /**
     * Inserts a new order into the database.
     *
     * @param order The order to be inserted.
     * @return true if the order was successfully inserted, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean insert(Order order) throws SQLException {

        String sql = "INSERT INTO COMMANDE (fk_client, fk_resto, a_emporter, quand) VALUES (?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});
            StatementHelper.bindStatementParameters(
                    statement,
                    order.getCustomer().getId(),
                    order.getRestaurant().getId(),
                    order.getTakeAway(),
                    order.getWhen()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                // Set the new generated ID to the order object
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    order.setId(Long.valueOf(generatedId));
                    SimpleLogger.info("[DB][INSERTED] ORDER ID: " + order.getId());

                    cacheProvider.cache.put(order.getId(), order);

                    connection.commit();
                    return true;
                }
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while inserting order: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Updates an existing order in the database.
     *
     * @param order The order to be updated.
     * @return true if the order was successfully updated, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean update(Order order) throws SQLException {

        String sql = "UPDATE COMMANDE SET fk_client = ?, fk_resto = ?, a_emporter = ?, quand = ? WHERE numero = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    order.getCustomer().getId(),
                    order.getRestaurant().getId(),
                    order.getTakeAway(),
                    order.getWhen(),
                    order.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DB][UPDATED] ORDER ID: " + order.getId());

                cacheProvider.cache.put(order.getId(), order);

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DB][UPDATE] NO ORDER FOUND WITH ID: " + order.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while updating order: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Deletes an order from the database.
     *
     * @param order The order to be deleted.
     * @return true if the order was successfully deleted, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean delete(Order order) throws SQLException {

        String sql = "DELETE FROM COMMANDE WHERE numero = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    order.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DB][DELETED] ORDER ID: " + order.getId());

                cacheProvider.cache.remove(order.getId());

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DB][DELETE] NO ORDER FOUND WITH ID: " + order.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting order: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Selects an order by its ID.
     *
     * @param id The ID of the order to be selected.
     * @return an Optional containing the order if found, otherwise an empty Optional.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Optional<Order> selectById(Long id) throws SQLException {

        // Check the cache first
        if (cacheProvider.cache.containsKey(id)) {
            SimpleLogger.info("[CACHE][SELECTED] ORDER ID: " + id);
            return Optional.of(cacheProvider.cache.get(id));
        }

        String sql = "SELECT * FROM COMMANDE WHERE numero = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, id);

            ResultSet resultSet = statement.executeQuery();

            // Move cursor to the first result row
            if (resultSet.next()) {
                Order order = mapToObject(resultSet);

                cacheProvider.cache.put(order.getId(), order);

                SimpleLogger.info("[DB][SELECTED] ORDER ID: " + id);
                return Optional.of(order);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching order by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();
    }

    /**
     * Selects all orders from the database or from the cache when it is valid.
     * Set the cache valid once the data is fetched from the database.
     *
     * @return a list of all orders.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<Order> selectAll() throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid()){
            SimpleLogger.info("[CACHE][SELECTED] ALL ORDER" );
            return new ArrayList<>(cacheProvider.cache.values());
        }

        String sql = "SELECT * FROM COMMANDE";

        List<Order> orders = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Order order = mapToObject(resultSet);
                orders.add(order);

                cacheProvider.cache.put(order.getId(), order);
            }

            cacheProvider.setCacheValid();

            SimpleLogger.info("[DB][SELECTED] ALL ORDER");

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching all orders: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    /**
     * Selects orders by restaurant ID.
     *
     * @param restaurantId The restaurant ID used to filter the orders.
     * @return a set of orders for the given restaurant ID.
     * @throws SQLException if a database access error occurs.
     */
    public Set<Order> selectWhereRestaurantId(Long restaurantId) throws SQLException {

        // Check the cache first, fill it if it is invalid
        if (!ProductOrderMapper.cacheProvider.isCacheValid()) {
            ProductOrderMapper.getInstance().selectAll();
        } else {
            SimpleLogger.info("[CACHE][SELECTED] ORDER WHERE RESTAURANT ID: " + restaurantId);
            return RestaurantDataMapper.cacheProvider.cache.get(restaurantId).getOrders();
        }

        String sql = "SELECT * FROM COMMANDE WHERE fk_resto = ?";

        Set<Order> orders = new HashSet<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, restaurantId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Order order = mapToObject(resultSet);
                orders.add(order);

                cacheProvider.cache.put(order.getId(), order);
            }
            SimpleLogger.info("[DB][SELECTED] ORDER WHERE RESTAURANT ID: " + restaurantId);

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching orders by restaurant ID: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    /**
     * Selects orders by customer ID.
     *
     * @param customerId The customer ID used to filter the orders.
     * @return a set of orders for the given customer ID.
     * @throws SQLException if a database access error occurs.
     */
    public Set<Order> selectWhereCustomerId(Long customerId) throws SQLException {

        // Check the cache first, fill it if it is invalid
        if (!ProductOrderMapper.cacheProvider.isCacheValid()) {
            ProductOrderMapper.getInstance().selectAll();
        } else {
            SimpleLogger.info("[CACHE][SELECTED] ORDER WHERE CUSTOMER ID: " + customerId);
            return CustomerDataMapper.cacheProvider.cache.get(customerId).getOrders();
        }

        String sql = "SELECT * FROM COMMANDE WHERE fk_client = ?";

        Set<Order> orders = new HashSet<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, customerId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Order order = mapToObject(resultSet);
                orders.add(order);

                cacheProvider.cache.put(order.getId(), order);
            }
            SimpleLogger.info("[DB][SELECTED] ORDER WHERE CUSTOMER ID: " + customerId);

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching orders by customer ID: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    /**
     * Maps a ResultSet to an Order object.
     *
     * @param resultSet The ResultSet containing the order data.
     * @return the mapped Order object.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Order mapToObject(ResultSet resultSet) throws SQLException {
        return new Order.Builder()
                .withId(resultSet.getLong("numero"))
                .withCustomer(
                        CustomerDataMapper.getInstance()
                                .selectById(resultSet.getLong("FK_CLIENT"))
                                .orElseThrow(() -> new IllegalArgumentException("Given FK_CLIENT not found in database"))
                )
                .withRestaurant(
                        RestaurantDataMapper.getInstance()
                                .selectById(resultSet.getLong("FK_RESTO"))
                                .orElseThrow(() -> new IllegalArgumentException("Given FK_RESTO not found in database"))
                )
                .withTakeAway(resultSet.getBoolean("a_emporter"))
                .withWhen(resultSet.getTimestamp("quand").toLocalDateTime())
                .build();
    }
}
