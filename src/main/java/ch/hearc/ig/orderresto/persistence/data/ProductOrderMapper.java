package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;import ch.hearc.ig.orderresto.utils.SimpleLogger;import java.sql.Connection;import java.sql.PreparedStatement;import java.sql.ResultSet;import java.sql.SQLException;
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

    // store each orders id made for a product id
    // not properly implemented as it is a many to many relation
    public final Map<Long, Set<Order>> cache = new HashMap<>();

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
            cache.put(productId, orders);
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

                SimpleLogger.info("[INSERTED] PRODUCT ORDER RELATION with PRODUCT ID: " + productId + " and ORDER ID: " + orderId);
            }

            return true;
        } catch (SQLException e) {
            SimpleLogger.error("Error while inserting product order relation: " + e.getMessage());
            throw e;
        }
    }

}
