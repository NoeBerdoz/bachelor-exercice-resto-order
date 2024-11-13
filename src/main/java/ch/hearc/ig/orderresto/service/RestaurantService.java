package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.data.OrderDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductDataMapper;
import ch.hearc.ig.orderresto.persistence.data.RestaurantDataMapper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RestaurantService {

    private static RestaurantService instance;
    private RestaurantService() {}

    public static RestaurantService getInstance() {
        if(instance == null) {
            instance = new RestaurantService();
        }
        return instance;
    }

    private final RestaurantDataMapper restaurantDataMapper = RestaurantDataMapper.getInstance();
    private final OrderDataMapper orderDataMapper = OrderDataMapper.getInstance();
    private final ProductDataMapper productDataMapper = ProductDataMapper.getInstance();

    public Set<Order> getOrdersFromRestaurant(Restaurant restaurant) {
        Set<Order> orders = null;

        try {
            orders = orderDataMapper.selectWhereRestaurantId(restaurant.getId());
            restaurant.setOrders(orders);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get the orders of a restaurant: " + e.getMessage() );
        }

        return orders;
    }

    public Set<Product> getProductsFromRestaurant(Restaurant restaurant) {
        Set<Product> products = null;

        try {
            products = productDataMapper.selectWhereRestaurantId(restaurant.getId());
            restaurant.setProductsCatalog(products);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get the products of a restaurant: " + e.getMessage() );
        }

        return products;
    }

    public Optional<Restaurant> getRestaurantById(Long id) {

        try {
            return restaurantDataMapper.selectById(id);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get a restaurant by id: " + e.getMessage() );
        }

        return Optional.empty();
    }

    public List<Restaurant> getAllRestaurants() {

        try {
            return restaurantDataMapper.selectAll();
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get all restaurants: " + e.getMessage() );
        }

        return List.of();
    }

    public boolean addRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.insert(restaurant);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to add a restaurant: " + e.getMessage() );
        }

        return false;
    }

    public boolean modifyRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.update(restaurant);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to modify a restaurant: " + e.getMessage() );
        }

        return false;
    }

    public boolean removeRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.delete(restaurant);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to remove a restaurant: " + e.getMessage() );
        }

        return false;
    }

}
