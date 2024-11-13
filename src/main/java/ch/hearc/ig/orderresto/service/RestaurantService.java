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

/**
 * Service class that handles operations related to restaurants, including retrieving, adding, modifying,
 * and removing restaurants. It also manages retrieving related orders and products for each restaurant.
 * This service interacts with the data access layer to perform these operations.
 */
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

    /**
     * Retrieves all orders associated with a specific restaurant.
     *
     * @param restaurant the restaurant whose orders are to be retrieved.
     * @return a set of orders associated with the restaurant, or null if an error occurs.
     */
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

    /**
     * Retrieves all products associated with a specific restaurant.
     *
     * @param restaurant the restaurant whose products are to be retrieved.
     * @return a set of products associated with the restaurant, or null if an error occurs.
     */
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

    /**
     * Retrieves a restaurant by its ID.
     *
     * @param id the ID of the restaurant to retrieve.
     * @return an Optional containing the restaurant if found, or an empty Optional if not found or an error occurs.
     */
    public Optional<Restaurant> getRestaurantById(Long id) {

        try {
            return restaurantDataMapper.selectById(id);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get a restaurant by id: " + e.getMessage() );
        }

        return Optional.empty();
    }

    /**
     * Retrieves a list of all restaurants.
     *
     * @return a list of all restaurants, or an empty list if an error occurs.
     */
    public List<Restaurant> getAllRestaurants() {

        try {
            return restaurantDataMapper.selectAll();
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get all restaurants: " + e.getMessage() );
        }

        return List.of();
    }

    /**
     * Adds a new restaurant to the system.
     *
     * @param restaurant the restaurant to be added.
     * @return true if the restaurant was successfully added, false otherwise.
     */
    public boolean addRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.insert(restaurant);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to add a restaurant: " + e.getMessage() );
        }

        return false;
    }

    /**
     * Modifies the details of an existing restaurant.
     *
     * @param restaurant the restaurant to be modified.
     * @return true if the restaurant was successfully modified, false otherwise.
     */
    public boolean modifyRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.update(restaurant);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to modify a restaurant: " + e.getMessage() );
        }

        return false;
    }

    /**
     * Removes a restaurant from the system.
     *
     * @param restaurant the restaurant to be removed.
     * @return true if the restaurant was successfully removed, false otherwise.
     */
    public boolean removeRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.delete(restaurant);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to remove a restaurant: " + e.getMessage() );
        }

        return false;
    }

}
