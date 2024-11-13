package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.data.CustomerDataMapper;
import ch.hearc.ig.orderresto.persistence.data.OrderDataMapper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Service class that provides methods for managing customers, including retrieving, adding, modifying,
 * and deleting customer records. It interacts with the data access layer (mappers) to perform operations
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

    private final CustomerDataMapper customerDataMapper = CustomerDataMapper.getInstance();
    private final OrderDataMapper orderDataMapper = OrderDataMapper.getInstance();

    /**
     * Retrieves all orders for a given customer, including product details for each order.
     *
     * @param customer the customer whose orders are to be retrieved.
     * @return a set of orders associated with the given customer, or null if an error occurs.
     */
    public Set<Order> getOrdersFromCustomer(Customer customer) {
        Set<Order> orders = null;

        try {
            orders = orderDataMapper.selectWhereCustomerId(customer.getId());

            // set the products of the order, and therefore the total amount of the order
            for (Order order : orders) {
                ProductOrderService.getInstance().getProductsFromOrder(order);
            }

            customer.setOrders(orders);
        } catch (SQLException e) {
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
        try {
            // Manage the already present technical debt given with the exercise
            // Gender in database is a char "O" or "N" and in the application it's "F" or "M"
            if (customer instanceof PrivateCustomer) {
                if (Objects.equals(((PrivateCustomer) customer).getGender(), "F")) {
                    ((PrivateCustomer) customer).setGender("O");
                } else {
                    ((PrivateCustomer) customer).setGender("N");
                }
            }
            return customerDataMapper.insert(customer);
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to add a customer: " + e.getMessage());
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

        try {
            return customerDataMapper.update(customer);
        } catch (SQLException e) {
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

        try {
            return customerDataMapper.delete(customer);
        } catch (SQLException e) {
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

        try {
            return customerDataMapper.selectById(id);
        } catch (SQLException e) {
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

        try {
            return customerDataMapper.selectAll();
        } catch (SQLException e) {
            SimpleLogger.error("An error occured while trying to get all customers: " + e.getMessage());
        }

        return List.of();
    }

}
