package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.data.OrderDataMapper;
import ch.hearc.ig.orderresto.persistence.data.ProductDataMapper;
import ch.hearc.ig.orderresto.persistence.data.RestaurantDataMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RestaurantService {

    private final RestaurantDataMapper restaurantDataMapper = RestaurantDataMapper.getInstance();
    private final OrderDataMapper orderDataMapper = OrderDataMapper.getInstance();
    private final ProductDataMapper productDataMapper = ProductDataMapper.getInstance();

    public Set<Order> getOrdersFromRestaurant(Restaurant restaurant) throws SQLException {

        Set<Order> orders = orderDataMapper.selectWhereRestaurantId(restaurant.getId());
        restaurant.setOrders(orders);

        return orders;

    }

    public Set<Product> getProductFromRestaurant(Restaurant restaurant) throws SQLException {

        Set<Product> products = productDataMapper.selectWhereRestaurantId(restaurant.getId());
        restaurant.setProductsCatalog(products);

        return products;
    }

    public boolean addRestaurant(Restaurant restaurant) throws SQLException {
        return restaurantDataMapper.insert(restaurant);
    }

    public boolean modifyRestaurant(Restaurant restaurant) throws SQLException {
        return restaurantDataMapper.update(restaurant);
    }

    public boolean removeRestaurant(Restaurant restaurant) throws SQLException {
        return restaurantDataMapper.delete(restaurant);
    }

    public Optional<Restaurant> getRestaurantById(Long id) throws SQLException {
        return restaurantDataMapper.selectById(id);
    }

    public List<Restaurant> getAllRestaurants() throws SQLException {
        return restaurantDataMapper.selectAll();
    }

}
