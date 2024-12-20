package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.utils.HibernateUtil;
import ch.hearc.ig.orderresto.utils.SimpleLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class that provides methods for managing customers, including retrieving, adding, modifying,
 * and deleting customer records. It interacts with Hibernate to perform operations
 * related to customers and their associated orders.
 */
public class CustomerService {

    private static CustomerService instance;
    private CustomerService() {}

    public static CustomerService getInstance() {
        if(instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    /**
     * Retrieves all orders for a given customer, including product details for each order.
     *
     * @param customer the customer whose orders are to be retrieved.
     * @return a set of orders associated with the given customer, or null if an error occurs.
     */
    public Set<Order> getOrdersFromCustomer(Customer customer) {
        Set<Order> orders = null;

        try {

            orders = customer.getOrders();

        } catch (Exception e) {
            SimpleLogger.error("An error occured while trying to get the orders of a customer: " + e.getMessage() );
        }

        return orders;
    }

    /**
     * Adds a new customer to the system. If the customer is a PrivateCustomer, the gender is converted
     * from the application representation ("F" or "H") to the database representation ("O" or "N").
     *
     * @param customer the customer to be added.
     * @return true if the customer was successfully added, false otherwise.
     */
    public boolean addCustomer(Customer customer) {

        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {

            transaction.begin();

            entityManager.persist(customer);

            transaction.commit();

            return true;
        } catch (Exception e) {
            transaction.rollback();
            SimpleLogger.error("An error occured while trying to add a customer: " + e.getMessage());
            e.printStackTrace();  // Log the full stack trace to diagnose the issue
        }

        return false;
    }

    /**
     * Modifies the details of an existing customer.
     *
     * @param customer the customer to be modified.
     * @return true if the customer was successfully modified, false otherwise.
     */
    public boolean modifyCustomer(Customer customer) {

        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.merge(customer);

            transaction.commit();

            return true;
        } catch (Exception e) {
            transaction.rollback();
            SimpleLogger.error("An error occured while trying to modify a customer: " + e.getMessage());
        }

        return false;
    }

    /**
     * Removes a customer from the system.
     *
     * @param customer the customer to be removed.
     * @return true if the customer was successfully removed, false otherwise.
     */
    public boolean removeCustomer(Customer customer) {

        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.remove(customer);

            transaction.commit();

            return true;
        } catch (Exception e) {
            transaction.rollback();
            SimpleLogger.error("An error occured while trying to remove a customer: " + e.getMessage());
        }

        return false;
    }

    /**
     * Retrieves a customer by their unique ID.
     *
     * @param id the ID of the customer to be retrieved.
     * @return an Optional containing the customer if found, or an empty Optional if not found.
     */
    public Optional<Customer> getCustomerById(Long id) {

        EntityManager entityManager = HibernateUtil.getEntityManager();

        try {
            return Optional.ofNullable(entityManager.find(Customer.class, id));
        } catch (Exception e) {
            SimpleLogger.error("An error occured while trying to get a customer by id: " + e.getMessage());
        }

        return Optional.empty();
    }


    /**
     * Retrieves a list of all customers in the system.
     *
     * @return a list of all customers, or an empty list if an error occurs.
     */
    public List<Customer> getAllCustomers() {

        EntityManager entityManager = HibernateUtil.getEntityManager();

        try {
            return entityManager.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        } catch (Exception e) {
            SimpleLogger.error("An error occured while trying to get all customers: " + e.getMessage());
        }

        return List.of();
    }

}
