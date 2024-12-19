package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.utils.HibernateUtil;
import ch.hearc.ig.orderresto.utils.SimpleLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

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


    /**
     * Retrieves all orders associated with a specific restaurant.
     *
     * @param restaurant the restaurant whose orders are to be retrieved.
     * @return a set of orders associated with the restaurant, or null if an error occurs.
     */
    public Set<Order> getOrdersFromRestaurant(Restaurant restaurant) {

        try {
            return restaurant.getOrders();
        } catch (Exception e) {
            SimpleLogger.error("An error occured while trying to get the orders of a restaurant: " + e.getMessage() );
        }

        return null;
    }

    /**
     * Retrieves all products associated with a specific restaurant.
     *
     * @param restaurant the restaurant whose products are to be retrieved.
     * @return a set of products associated with the restaurant, or null if an error occurs.
     */
    public Set<Product> getProductsFromRestaurant(Restaurant restaurant) {

        try {
            return restaurant.getProductsCatalog();
        } catch (Exception e) {
            SimpleLogger.error("An error occured while trying to get the products of a restaurant: " + e.getMessage() );
        }

        return null;
    }

    /**
     * Retrieves a restaurant by its ID.
     *
     * @param id the ID of the restaurant to retrieve.
     * @return an Optional containing the restaurant if found, or an empty Optional if not found or an error occurs.
     */
    public Optional<Restaurant> getRestaurantById(Long id) {

        EntityManager entityManager = HibernateUtil.getEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Restaurant.class, id));
        } catch (Exception e) {
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

        EntityManager entityManager = HibernateUtil.getEntityManager();

        try {
            return entityManager.createQuery("SELECT r FROM Restaurant r", Restaurant.class).getResultList();
        } catch (Exception e) {
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

        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.persist(restaurant);

            transaction.commit();

            return true;
        } catch (Exception e) {
            transaction.rollback();
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

        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.merge(restaurant);

            transaction.commit();

            return true;
        } catch (Exception e) {
            transaction.rollback();
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

        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.remove(restaurant);

            transaction.commit();

            return true;
        } catch (Exception e) {
            transaction.rollback();
            SimpleLogger.error("An error occured while trying to remove a restaurant: " + e.getMessage() );
        }

        return false;
    }

}
