package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.data.OrderDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductOrderMapper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.SQLException;
import java.util.Set;

/**
 * Service class that handles operations related to orders and products, including adding, modifying,
 * removing, and retrieving orders and products in the context of a restaurant. It interacts with the
 * data access layer to manage the relationships between orders and products.
 */
public class ProductOrderService {

    private static ProductOrderService instance;

    private ProductOrderService() {}

    public static ProductOrderService getInstance() {
        if(instance == null) {
            instance = new ProductOrderService();
        }
        return instance;
    }

    private final OrderDataMapper orderDataMapper = OrderDataMapper.getInstance();
    private final ProductDataMapper productDataMapper = ProductDataMapper.getInstance();
    private final ProductOrderMapper productOrderMapper = ProductOrderMapper.getInstance();

    /**
     * Retrieves all products associated with a specific order.
     *
     * @param order the order whose products are to be retrieved.
     * @return a set of products associated with the order, or null if an error occurs.
     */
    public Set<Product> getProductsFromOrder(Order order) {
        Set<Product> products = null;

        try {
            // Set products of the order
            products = productOrderMapper.selectProductsWhereOrder(order);

        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get the products of an order: " + e.getMessage() );
        }

        return products;
    }

    /**
     * Adds a new order to the restaurant, including associating products with the order.
     *
     * @param order the order to be added to the restaurant.
     * @return true if the order was successfully added, false otherwise.
     */
    public boolean addOrderToRestaurant(Order order) {

        try {
            orderDataMapper.insert(order);
            for (Product product : order.getProducts()) {
                productOrderMapper.insertProductOrderRelation(product, order);
            }
            return true;
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to add an order to the restaurant: " + e.getMessage() );
        }

        return false;
    }

    /**
     * Adds a new product to the restaurant's inventory.
     *
     * @param product the product to be added to the restaurant.
     * @return true if the product was successfully added, false otherwise.
     */
    public boolean addProductToRestaurant(Product product) {

        try {
            return productDataMapper.insert(product);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to add a product to the restaurant: " + e.getMessage() );
        }

        return false;
    }

    /**
     * Modifies the details of an existing order.
     *
     * @param order the order to be modified.
     * @return true if the order was successfully modified, false otherwise.
     */
    public boolean modifyOrder(Order order) {

        try {
            return orderDataMapper.update(order);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to modify an order: " + e.getMessage() );
        }

        return false;
    }

    /**
     * Modifies the details of an existing product.
     *
     * @param product the product that has been modified.
     * @return true if the product was successfully modified, false otherwise.
     */
    public boolean modifyProduct(Product product) {

        try {
            return productDataMapper.update(product);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to modify a product: " + e.getMessage() );
        }

        return false;
    }

    /**
     * Removes an order from the restaurant, including its associated product-order relationships.
     *
     * @param order the order to be removed.
     * @return true if the order was successfully removed, false otherwise.
     */
    public boolean removeOrder(Order order) {

        try {
            productOrderMapper.deleteProductOrderRelation(order.getId());
            return orderDataMapper.delete(order);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to remove an order: " + e.getMessage() );
        }

        return false;
    }

    /**
     * Removes a product from the restaurant's inventory.
     * Note: This operation may not be fully supported and could cause issues.
     *
     * @param product the product to be removed.
     * @return true if the product was successfully removed, false otherwise.
     */
    public boolean removeProduct(Product product) {

        try {
            return productDataMapper.delete(product);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to remove a product: " + e.getMessage() );
        }

        return false;
    }

}
