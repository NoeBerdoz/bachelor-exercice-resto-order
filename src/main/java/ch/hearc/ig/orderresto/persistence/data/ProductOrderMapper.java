package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.helper.CacheProvider;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.math.BigDecimal;
import java.sql.Connection;import java.sql.PreparedStatement;import java.sql.ResultSet;import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ProductOrderMapper {

    private static ProductOrderMapper instance;

    private ProductOrderMapper() {}

    public static ProductOrderMapper getInstance() {
        if (instance == null) {
            instance = new ProductOrderMapper();
        }
        return instance;
    }

    // store each orders as id and their products
    public static final CacheProvider<Long, Set<Product>> cacheProvider = new CacheProvider<>();

    public void selectAll() throws SQLException {

        String sql = "SELECT FK_COMMANDE, PRODUIT.NUMERO, PRODUIT.FK_RESTO, PRODUIT.PRIX_UNITAIRE, PRODUIT.NOM, PRODUIT.DESCRIPTION FROM PRODUIT_COMMANDE " +
                     "JOIN PRODUIT " +
                     "ON PRODUIT_COMMANDE.FK_PRODUIT = PRODUIT.NUMERO";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long orderId = resultSet.getLong("FK_COMMANDE");

                Product product = ProductDataMapper.getInstance().mapToObject(resultSet);

                cacheProvider.cache
                        .computeIfAbsent(orderId, k -> new HashSet<>()) // Adds an empty HashSet with orderId as key if not present
                        .add(product); // Add product to the new HashSet
            }

            cacheProvider.setCacheValid();

            SimpleLogger.info("[SELECTED] ALL PRODUCT_ORDER");
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching all product order relations: " + e.getMessage());
            throw e;
        }
    }

    // TODO write java doc
    public Set<Product> selectProductsWhereOrder(Order order) throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid() && cacheProvider.cache.containsKey(order.getId())) {
            SimpleLogger.info("[CACHE][SELECTED] PRODUCT WHERE PRODUCT_ORDER ORDER ID: " + order.getId());
            return cacheProvider.cache.get(order.getId());
        }

        String sql = "SELECT * FROM PRODUIT " +
                     "JOIN PRODUIT_COMMANDE ON PRODUIT.NUMERO = PRODUIT_COMMANDE.FK_PRODUIT " +
                     "WHERE PRODUIT_COMMANDE.FK_COMMANDE = ?";

        cacheProvider.cache.put(order.getId(), new HashSet<>());

        Set<Product> products = new HashSet<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, order.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = ProductDataMapper.getInstance().mapToObject(resultSet);
                products.add(product);

                // Update Product cache
                ProductDataMapper.cacheProvider.cache.put(product.getId(), product);
                cacheProvider.cache.get(order.getId()).add(product);
            }

            // Update Order according to the freshly fetched products
            order.setProducts(products);

            // Set the total amount of the order
            BigDecimal orderTotalPrice = new BigDecimal(0);
            for (Product product : products) {
                orderTotalPrice = orderTotalPrice.add(product.getUnitPrice());
            }
            order.setTotalAmount(orderTotalPrice);
            OrderDataMapper.cacheProvider.cache.put(order.getId(), order);

            SimpleLogger.info("[DB][SELECTED] PRODUCT WHERE PRODUCT_ORDER ORDER ID: " + order.getId());
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching products by order ID: " + e.getMessage());
            throw e;
        }

        return products;
    }

    public boolean insertProductOrderRelation(Product product, Order order) throws SQLException {

        String sql = "INSERT INTO PRODUIT_COMMANDE (FK_PRODUIT, FK_COMMANDE) VALUES (?,?)";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    product.getId(),
                    order.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                cacheProvider.cache
                        .computeIfAbsent(order.getId(), k -> new HashSet<>()) // Adds an empty HashSet with orderId as key if not present
                        .add(product); // Add product to the new HashSet

                ProductDataMapper.cacheProvider.cache
                        .computeIfAbsent(product.getId(), k -> product) // Adds the product if not present
                        .getOrders().add(order); // Add order to the product's orders

                OrderDataMapper.cacheProvider.cache
                        .computeIfAbsent(order.getId(), k -> order) // Adds the order if not present
                        .getProducts().add(product); // Add product to the order's products

                SimpleLogger.info("[DB][INSERTED] PRODUCT_ORDER PRODUCT ID: " + product.getId() + " ORDER ID: " + product.getId());

                connection.commit();
                return true;
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while inserting product order relation: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    public boolean deleteProductOrderRelation(Long orderId) throws SQLException {

        String sql = "DELETE FROM PRODUIT_COMMANDE WHERE FK_COMMANDE = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(
                    statement,
                    orderId
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                cacheProvider.cache.remove(orderId);
                SimpleLogger.info("[DB][DELETED] PRODUCT_ORDER WHERE ORDER ID: " + orderId);

                connection.commit();
                return true;
            } else {
                SimpleLogger.info("[DB][DELETED] NO PRODUCT_ORDER FOUND WITH ORDER ID: " + orderId);
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting product order relation: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }
}
