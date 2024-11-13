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

/**
 * DataMapper implementation for managing Product entities in the database.
 * Provides CRUD operations and caching for products.
 */
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

    /**
     * Inserts a product into the database.
     *
     * @param product the product to insert.
     * @return true if insertion is successful, false otherwise.
     * @throws SQLException if a database error occurs.
     */
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
                    SimpleLogger.info("[DB][INSERTED] PRODUCT ID: " + product.getId());

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

    /**
     * Updates a product in the database.
     *
     * @param product the product to update.
     * @return true if update is successful, false otherwise.
     * @throws SQLException if a database error occurs.
     */
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
                SimpleLogger.info("[DB][UPDATED] PRODUCT ID: " + product.getId());

                cacheProvider.cache.put(product.getId(), product);

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DB][UPDATE] NO PRODUCT FOUND WITH ID: " + product.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while updating product: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Deletes a product from the database.
     *
     * @param product the product to delete.
     * @return true if deletion is successful, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    @Override
    public boolean delete(Product product) throws SQLException {

        String sql = "DELETE FROM PRODUIT WHERE NUMERO = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(statement, product.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DB][DELETED] PRODUCT ID: " + product.getId());

                cacheProvider.cache.remove(product.getId());

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DB][DELETE] NO PRODUCT FOUND WITH ID " + product.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting product: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve.
     * @return an Optional containing the product if found, empty otherwise.
     * @throws SQLException if a database error occurs.
     */
    @Override
    public Optional<Product> selectById(Long id) throws SQLException {

        // Check the cache first
        if (cacheProvider.cache.containsKey(id)) {
            SimpleLogger.info("[CACHE][SELECTED] PRODUCT ID: " + id);
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

                SimpleLogger.info("[DB][SELECTED] PRODUCT ID: " + id);
                return Optional.of(product);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching product by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();

    }

    /**
     * Retrieves all products from the database. Set the cache validity to true once fetched from the database.
     *
     * @return a list of all products.
     * @throws SQLException if a database error occurs.
     */
    @Override
    public List<Product> selectAll() throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid()){
            SimpleLogger.info("[CACHE][SELECTED] ALL PRODUCT" );
            return new ArrayList<>(cacheProvider.cache.values());
        }

        String sql = "SELECT * FROM PRODUIT";

        List<Product> products = new ArrayList<>();

        try {

            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = mapToObject(resultSet);
                products.add(product);

                cacheProvider.cache.put(product.getId(), product);
            }

            cacheProvider.setCacheValid();

            SimpleLogger.info("[DB][SELECTED] ALL PRODUCT");
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching products: " + e.getMessage());
            throw e;
        }

        return products;
    }

    /**
     * Retrieves products associated with a specific restaurant ID.
     *
     * @param restaurantId the restaurant ID.
     * @return a set of products related to the specified restaurant.
     * @throws SQLException if a database error occurs.
     */
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
                SimpleLogger.info("[CACHE][SELECTED] PRODUCT WHERE RESTAURANT ID: " + restaurantId);
                return products;
            }
        }

        String sql = "SELECT * FROM PRODUIT WHERE FK_RESTO = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(statement, restaurantId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = mapToObject(resultSet);
                products.add(product);

                cacheProvider.cache.put(product.getId(), product);
            }
            RestaurantDataMapper.cacheProvider.cache.get(restaurantId).setProductsCatalog(products);

            SimpleLogger.info("[SELECTED] PRODUCT WHERE RESTAURANT ID: " + restaurantId);
        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching products: " + e.getMessage());
            throw e;
        }

        return products;
    }

    /**
     * Maps a ResultSet to a Product object.
     *
     * @param resultSet the ResultSet to map.
     * @return the mapped Product object.
     * @throws SQLException if a database error occurs.
     */
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
