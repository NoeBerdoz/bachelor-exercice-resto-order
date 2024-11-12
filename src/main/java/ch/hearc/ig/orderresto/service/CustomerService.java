package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.data.CustomerDataMapper;
import ch.hearc.ig.orderresto.persistence.data.OrderDataMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
            e.printStackTrace();
        }

        return orders;
    }

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
            e.printStackTrace();
        }

        return false;
    }

    public boolean modifyCustomer(Customer customer) {

        try {
            return customerDataMapper.update(customer);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeCustomer(Customer customer) {

        try {
            return customerDataMapper.delete(customer);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Optional<Customer> getCustomerById(Long id) {

        try {
            return customerDataMapper.selectById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Customer> getAllCustomers() {

        try {
            return customerDataMapper.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return List.of();
    }

}
