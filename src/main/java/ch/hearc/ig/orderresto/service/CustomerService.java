package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.data.CustomerDataMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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


    public boolean addCustomer(Customer customer) {

        try {
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

    public boolean remvoeCustomer(Customer customer) {

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
