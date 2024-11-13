package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.data.OrderDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductOrderMapper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.SQLException;
import java.util.Set;

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

    public boolean addProductToRestaurant(Product product) {

        try {
            return productDataMapper.insert(product);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to add a product to the restaurant: " + e.getMessage() );
        }

        return false;
    }

    public boolean modifyOrder(Order order) {

        try {
            return orderDataMapper.update(order);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to modify an order: " + e.getMessage() );
        }

        return false;
    }

    public boolean modifyProduct(Product product) {

        try {
            return productDataMapper.update(product);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to modify a product: " + e.getMessage() );
        }

        return false;
    }

    public boolean removeOrder(Order order) {

        try {
            productOrderMapper.deleteProductOrderRelation(order.getId());
            return orderDataMapper.delete(order);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to remove an order: " + e.getMessage() );
        }

        return false;
    }

    // Not fully supported, this could cause issues
    public boolean removeProduct(Product product) {

        try {
            return productDataMapper.delete(product);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to remove a product: " + e.getMessage() );
        }

        return false;
    }

}
