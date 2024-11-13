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
                    SimpleLogger.info("[INSERTED] ORDER WITH ID: " + order.getId());

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
                SimpleLogger.info("[UPDATED] ORDER WITH ID: " + order.getId());

                cacheProvider.cache.put(order.getId(), order);

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[UPDATED] NO ORDER TO UPDATE WITH ID: " + order.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while updating order: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

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
                SimpleLogger.info("[DELETED] ORDER WITH ID: " + order.getId());

                cacheProvider.cache.remove(order.getId());

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DELETED] NO ORDER TO DELETE WITH ID: " + order.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting order: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    @Override
    public Optional<Order> selectById(Long id) throws SQLException {

        // Check the cache first
        if (cacheProvider.cache.containsKey(id)) {
            SimpleLogger.info("[CACHE] Selected ORDER with ID: " + id);
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

                SimpleLogger.info("[SELECTED] ORDER with ID: " + id);
                return Optional.of(order);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching order by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();
    }

    @Override
    public List<Order> selectAll() throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid()){
            SimpleLogger.info("[CACHE] Selected all ORDER" );
            return new ArrayList<>(cacheProvider.cache.values());
        }

        String sql = "SELECT * FROM COMMANDE";

        List<Order> orders = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            Integer countOrder = 0;
            while (resultSet.next()) {
                Order order = mapToObject(resultSet);
                orders.add(order);

                cacheProvider.cache.put(order.getId(), order);

                countOrder++;
            }

            cacheProvider.setCacheValid();

            SimpleLogger.info("[SELECTED] ORDER COUNT: " + countOrder);

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching all orders: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    public Set<Order> selectWhereRestaurantId(Long restaurantId) throws SQLException {

        // Check the cache first, fill it if it is invalid
        if (!ProductOrderMapper.cacheProvider.isCacheValid()) {
            ProductOrderMapper.getInstance().selectAll();
        } else {
            SimpleLogger.info("[CACHE] Selected ORDER WHERE ORDER RESTAURANT ID: " + restaurantId);
            return RestaurantDataMapper.cacheProvider.cache.get(restaurantId).getOrders();
        }

        String sql = "SELECT * FROM COMMANDE WHERE fk_resto = ?";

        Set<Order> orders = new HashSet<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, restaurantId);

            ResultSet resultSet = statement.executeQuery();

            Integer countOrder = 0;
            while (resultSet.next()) {
                Order order = mapToObject(resultSet);
                orders.add(order);

                cacheProvider.cache.put(order.getId(), order);

                countOrder++;
            }
            SimpleLogger.info("[SELECTED] RESTAURANT ORDER COUNT: " + countOrder);

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching orders by restaurant ID: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    public Set<Order> selectWhereCustomerId(Long customerId) throws SQLException {

        // Check the cache first, fill it if it is invalid
        if (!ProductOrderMapper.cacheProvider.isCacheValid()) {
            ProductOrderMapper.getInstance().selectAll();
        } else {
            SimpleLogger.info("[CACHE] Selected ORDER WHERE ORDER CUSTOMER ID: " + customerId);
            return CustomerDataMapper.cacheProvider.cache.get(customerId).getOrders();
        }

        String sql = "SELECT * FROM COMMANDE WHERE fk_client = ?";

        Set<Order> orders = new HashSet<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, customerId);

            ResultSet resultSet = statement.executeQuery();

            Integer countOrder = 0;
            while (resultSet.next()) {
                Order order = mapToObject(resultSet);
                orders.add(order);

                cacheProvider.cache.put(order.getId(), order);

                countOrder++;
            }
            SimpleLogger.info("[SELECTED] CUSTOMER ORDER COUNT: " + countOrder);

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching orders by customer ID: " + e.getMessage());
            throw e;
        }

        return orders;
    }

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
