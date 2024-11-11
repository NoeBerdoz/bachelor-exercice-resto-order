package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.Connection;import java.sql.PreparedStatement;import java.sql.ResultSet;import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;public class ProductOrderMapper {

    private static ProductOrderMapper instance;

    private ProductOrderMapper() {}

    public static ProductOrderMapper getInstance() {
        if (instance == null) {
            instance = new ProductOrderMapper();
        }
        return instance;
    }

    // store each product id made for an order id
    // not properly implemented as it is a many to many relation
    public final Map<Long, Set<Long>> cache = new HashMap<>();

    // Doesn't support the cache as it would need to complexify the cache logic
    public Set<Order> selectOrdersWhereProductId(Long productId) throws SQLException {

        String sql = "SELECT * FROM COMMANDE " +
                "JOIN PRODUIT_COMMANDE ON COMMANDE.NUMERO = PRODUIT_COMMANDE.FK_COMMANDE " +
                "WHERE PRODUIT_COMMANDE.FK_PRODUIT = ?";

        Set<Order> orders = new HashSet<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, productId);

            ResultSet resultSet = statement.executeQuery();

            Integer countOrdersFromProduct = 0;
            while (resultSet.next()) {
                Order order = OrderDataMapper.getInstance().mapToObject(resultSet);
                orders.add(order);

                OrderDataMapper.getInstance().cache.put(order.getId(), order);

                countOrdersFromProduct++;
            }

            SimpleLogger.info("[SELECTED] ORDER COUNT: " + countOrdersFromProduct);
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching products by order ID: " + e.getMessage());
            throw e;
        }

        return orders;
    }

    // this method doesn't handle the cache as it would need to complexify the cache logic
    public Set<Product> selectProductsWhereOrderId(Long orderId) throws SQLException {

        String sql = "SELECT * FROM PRODUIT " +
                "JOIN PRODUIT_COMMANDE ON PRODUIT.NUMERO = PRODUIT_COMMANDE.FK_PRODUIT " +
                "WHERE PRODUIT_COMMANDE.FK_COMMANDE = ?";

        cache.put(orderId, new HashSet<>());

        Set<Product> products = new HashSet<>();


        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, orderId);

            ResultSet resultSet = statement.executeQuery();

            Integer countProductsInOrder = 0;
            while (resultSet.next()) {
                Product product = ProductDataMapper.getInstance().mapToObject(resultSet);
                products.add(product);

                ProductDataMapper.getInstance().cache.put(product.getId(), product);
                cache.get(orderId).add(product.getId());
                countProductsInOrder++;
            }

            SimpleLogger.info("[SELECTED] ORDER COUNT: " + countProductsInOrder);
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching products by order ID: " + e.getMessage());
            throw e;
        }

        return products;
    }

    // this method doesn't handle the cache as it would need to complexify the cache logic
    public boolean insertProductOrderRelation(Long productId, Long orderId) throws SQLException {

        String sql = "INSERT INTO PRODUIT_COMMANDE (FK_PRODUIT, FK_COMMANDE) VALUES (?,?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(
                    statement,
                    productId,
                    orderId
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                // Add productId to the set associated with orderId, creating a new set if necessary
                cache.computeIfAbsent(orderId, k -> new HashSet<>()).add(productId);

                SimpleLogger.info("[INSERTED] PRODUCT ORDER RELATION with PRODUCT ID: " + productId + " and ORDER ID: " + orderId);
            }

            return true;
        } catch (SQLException e) {
            SimpleLogger.error("Error while inserting product order relation: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteProductOrderRelation(Long orderId) throws SQLException {
        String sql = "DELETE FROM PRODUIT_COMMANDE WHERE FK_COMMANDE = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(
                    statement,
                    orderId
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                cache.remove(orderId);
                SimpleLogger.info("[DELETED] PRODUCT ORDER RELATION with ORDER ID: " + orderId);
                return true;
            } else {
                SimpleLogger.info("[DELETED] NO ORDER FOUND IN PRODUCT ORDER RELATION with ORDER ID: " + orderId);
            }
        } catch (SQLException e) {
            SimpleLogger.error("Error while deleting product order relation: " + e.getMessage());
            throw e;
        }

        return false;
    }
}
