package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.data.OrderDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductOrderMapper;
import ch.hearc.ig.orderresto.persistence.data.RestaurantDataMapper;

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
    private final ProductOrderMapper productOrderMapper = ProductOrderMapper.getInstance();

    public Set<Order> getOrdersFromRestaurant(Restaurant restaurant) {
        Set<Order> orders = null;

        try {
            orders = orderDataMapper.selectWhereRestaurantId(restaurant.getId());
            restaurant.setOrders(orders);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Set<Product> getProductsFromRestaurant(Restaurant restaurant) {
        Set<Product> products = null;

        try {
            products = productDataMapper.selectWhereRestaurantId(restaurant.getId());
            restaurant.setProductsCatalog(products);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public Optional<Restaurant> getRestaurantById(Long id) {

        try {
            return restaurantDataMapper.selectById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Restaurant> getAllRestaurants() {

        try {
            return restaurantDataMapper.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return List.of();
    }

    public boolean addRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.insert(restaurant);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addOrderToRestaurant(Order order) {

        try {
            orderDataMapper.insert(order);
            for (Product product : order.getProducts()) {
                productOrderMapper.insertProductOrderRelation(product.getId(), order.getId());
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean modifyRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.update(restaurant);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeRestaurant(Restaurant restaurant) {

        try {
            return restaurantDataMapper.delete(restaurant);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
