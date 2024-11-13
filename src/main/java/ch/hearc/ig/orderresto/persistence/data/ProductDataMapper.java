package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.helper.CacheProvider;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProductDataMapper implements DataMapper<Product> {

    private static ProductDataMapper instance;

    public ProductDataMapper() {}

    public static ProductDataMapper getInstance() {
        if(instance == null) {
            instance = new ProductDataMapper();
        }
        return instance;
    }

    public static final CacheProvider<Long, Product> cacheProvider = new CacheProvider<>();

    // Insert a Product to the database.
    @Override
    public boolean insert(Product product) throws SQLException {

        String sql = "INSERT INTO PRODUIT (FK_RESTO, PRIX_UNITAIRE, NOM, DESCRIPTION) VALUES (?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});
            StatementHelper.bindStatementParameters(
                    statement,
                    product.getRestaurant().getId(),
                    product.getUnitPrice(),
                    product.getName(),
                    product.getDescription()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                // Set the new generated ID to the product object
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    product.setId(Long.valueOf(generatedId));
                    SimpleLogger.info("[INSERTED] PRODUCT WITH ID: " + product.getId());

                    cacheProvider.cache.put(product.getId(), product);

                    connection.commit();
                    return true;
                }
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while inserting product: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    // Update a Product in the database based on its id.
    @Override
    public boolean update(Product product) throws SQLException {

        String sql = "UPDATE PRODUIT SET FK_RESTO = ?, PRIX_UNITAIRE = ?, NOM = ?, DESCRIPTION = ? WHERE NUMERO = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    product.getRestaurant().getId(),
                    product.getUnitPrice(),
                    product.getName(),
                    product.getDescription(),
                    product.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[UPDATED] PRODUCT WITH ID: " + product.getId());

                cacheProvider.cache.put(product.getId(), product);

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[UPDATED] NO PRODUCT TO UPDATE WITH ID: " + product.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while updating product: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    // Delete a Product in the database based on its id
    @Override
    public boolean delete(Product product) throws SQLException {

        String sql = "DELETE FROM PRODUIT WHERE NUMERO = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, product.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DELETED] PRODUCT WITH ID: " + product.getId());

                cacheProvider.cache.remove(product.getId());

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DELETED] NO PRODUCT FOUND WITH ID: " + product.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting product: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    // Retrieve a Product by its ID.
    @Override
    public Optional<Product> selectById(Long id) throws SQLException {

        // Check the cache first
        if (cacheProvider.cache.containsKey(id)) {
            SimpleLogger.info("[CACHE] Selected PRODUCT with ID: " + id);
            return Optional.of(cacheProvider.cache.get(id));
        }

        String sql = "SELECT * FROM PRODUIT WHERE numero = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(statement, id);

            ResultSet resultSet = statement.executeQuery();

            // Move cursor to the first result row
            if (resultSet.next()) {
                Product product = mapToObject(resultSet);

                cacheProvider.cache.put(product.getId(), product);

                SimpleLogger.info("[SELECTED] PRODUCT with ID: " + id);
                return Optional.of(product);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching product by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();

    }

    // Retrieves all Products from the database.
    @Override
    public List<Product> selectAll() throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid()){
            SimpleLogger.info("[CACHE] Selected all PRODUCT" );
            return new ArrayList<>(cacheProvider.cache.values());
        }

        String sql = "SELECT * FROM PRODUIT";

        List<Product> products = new ArrayList<>();

        try {

            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            Integer countProduct = 0;
            while (resultSet.next()) {
                Product product = mapToObject(resultSet);
                products.add(product);

                cacheProvider.cache.put(product.getId(), product);

                countProduct++;
            }

            cacheProvider.setCacheValid();

            SimpleLogger.info("[SELECTED] PRODUCT COUNT: " + countProduct);
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching products: " + e.getMessage());
            throw e;
        }

        return products;
    }

    public Set<Product> selectWhereRestaurantId(Long restaurantId) throws SQLException {

        Set<Product> products = new HashSet<>();

        // if a restaurant is found in the product cache, return the cached products
        if (
                RestaurantDataMapper.cacheProvider.isCacheValid()
                && RestaurantDataMapper.cacheProvider.cache.containsKey(restaurantId)
        ) {
            boolean isFoundInCache = false;
            for (Product product : cacheProvider.cache.values()) {
                if (product.getRestaurant().equals(RestaurantDataMapper.cacheProvider.cache.get(restaurantId))) {
                    isFoundInCache = true;
                    products.add(product);
                }
            }
            if (isFoundInCache) {
                SimpleLogger.info("[CACHE] Selected PRODUCT WHERE RESTAURANT ID: " + restaurantId);
                return products;
            }
        }

        String sql = "SELECT * FROM PRODUIT WHERE FK_RESTO = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(statement, restaurantId);

            ResultSet resultSet = statement.executeQuery();

            Integer countProduct = 0;
            while (resultSet.next()) {
                Product product = mapToObject(resultSet);
                products.add(product);

                cacheProvider.cache.put(product.getId(), product);

                countProduct++;
            }
            RestaurantDataMapper.cacheProvider.cache.get(restaurantId).setProductsCatalog(products);

            SimpleLogger.info("[SELECTED] PRODUCT COUNT: " + countProduct);
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching products: " + e.getMessage());
            throw e;
        }

        return products;
    }

    @Override
    public Product mapToObject(ResultSet resultSet) throws SQLException {

        return new Product.Builder()
                .withId(resultSet.getLong("NUMERO"))
                .withName(resultSet.getString("NOM"))
                .withUnitPrice(resultSet.getBigDecimal("PRIX_UNITAIRE"))
                .withDescription(resultSet.getString("DESCRIPTION"))
                .withRestaurant(
                        RestaurantDataMapper.getInstance()
                                .selectById(resultSet.getLong("FK_RESTO"))
                                .orElseThrow(() -> new IllegalArgumentException("Given FK_RESTO not found in database"))
                )
                .build();
    }

}
