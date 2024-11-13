package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.helper.CacheProvider;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DataMapper implementation for managing Customer entities in the database.
 * Provides CRUD operations and caching for customers.
 * It handles the logic of the Customer entity and its subclasses PrivateCustomer and OrganizationCustomer.
 */
public class CustomerDataMapper implements DataMapper<Customer> {

    private static CustomerDataMapper instance;

    public CustomerDataMapper() {}

    public static CustomerDataMapper getInstance() {
        if (instance == null) {
            instance = new CustomerDataMapper();
        }
        return instance;
    }

    public static final CacheProvider<Long, Customer> cacheProvider = new CacheProvider<>();

    /**
     * Inserts a new customer into the database.
     *
     * @param customer The customer to insert.
     * @return true if the insertion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during insertion.
     */
    @Override
    public boolean insert(Customer customer) throws SQLException {

        String sql = "INSERT INTO CLIENT (email, telephone, nom, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_sociale, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});
            StatementHelper.bindStatementParameters(
                    statement,
                    customer.getEmail(),
                    customer.getPhone(),
                    customer instanceof OrganizationCustomer ? ((OrganizationCustomer) customer).getName() : ((PrivateCustomer) customer).getLastName(),
                    customer.getAddress().getPostalCode(),
                    customer.getAddress().getLocality(),
                    customer.getAddress().getStreet(),
                    customer.getAddress().getStreetNumber() != null ? customer.getAddress().getStreetNumber() : null,
                    customer.getAddress().getCountryCode(),
                    customer instanceof OrganizationCustomer ? null : ((PrivateCustomer) customer).getGender(),
                    customer instanceof OrganizationCustomer ? null : ((PrivateCustomer) customer).getFirstName(),
                    customer instanceof OrganizationCustomer ? ((OrganizationCustomer) customer).getLegalForm() : null,
                    customer instanceof OrganizationCustomer ? "O" : "P"
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                // Set the new generated ID to the product object
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    customer.setId(Long.valueOf(generatedId));
                    SimpleLogger.info("[DB][INSERTED] CUSTOMER ID: " + customer.getId());

                    cacheProvider.cache.put(customer.getId(), customer);

                    connection.commit();
                    return true;
                }
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while inserting customer: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Updates an existing customer in the database.
     *
     * @param customer The customer to update.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the update.
     */
    @Override
    public boolean update(Customer customer) throws SQLException {

        String sql = "UPDATE CLIENT SET email = ?, telephone = ?, nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ?, est_une_femme = ?, prenom = ?, forme_sociale = ?, type = ? WHERE NUMERO = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    customer.getEmail(),
                    customer.getPhone(),
                    customer instanceof OrganizationCustomer ? ((OrganizationCustomer) customer).getName() : ((PrivateCustomer) customer).getLastName(),
                    customer.getAddress().getPostalCode(),
                    customer.getAddress().getLocality(),
                    customer.getAddress().getStreet(),
                    customer.getAddress().getStreetNumber() != null ? customer.getAddress().getStreetNumber() : null,
                    customer.getAddress().getCountryCode(),
                    customer instanceof OrganizationCustomer ? null : ((PrivateCustomer) customer).getGender(),
                    customer instanceof OrganizationCustomer ? null : ((PrivateCustomer) customer).getFirstName(),
                    customer instanceof OrganizationCustomer ? ((OrganizationCustomer) customer).getLegalForm() : null,
                    customer instanceof OrganizationCustomer ? "O" : "P",
                    customer.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DB][UPDATED] CUSTOMER ID: " + customer.getId());

                cacheProvider.cache.put(customer.getId(), customer);

                connection.commit();
                return true;
            } else {
                SimpleLogger.error("[DB][UPDATE] NO CUSTOMER FOUND WITH ID: " + customer.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while updating customer: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Deletes a customer from the database.
     *
     * @param customer The customer to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If an SQL error occurs during deletion.
     */
    @Override
    public boolean delete(Customer customer) throws SQLException {

        String sql = "DELETE FROM CLIENT WHERE NUMERO = ?";

        Connection connection = DatabaseConnection.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(
                    statement,
                    customer.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DB][DELETED] CUSTOMER ID: " + customer.getId());

                cacheProvider.cache.remove(customer.getId());

                connection.commit();
                return true;
            } else {
                SimpleLogger.warning("[DB][DELETE] NO CUSTOMER FOUND WITH ID: " + customer.getId());
            }

        } catch (SQLException e) {
            connection.rollback();
            SimpleLogger.error("Error while deleting customer: " + e.getMessage());
            throw e;
        }

        connection.commit();
        return false;
    }

    /**
     * Selects a customer by its ID from the database or cache.
     *
     * @param id The ID of the customer to retrieve.
     * @return An Optional containing the customer if found, empty otherwise.
     * @throws SQLException If an SQL error occurs during the selection.
     */
    @Override
    public Optional<Customer> selectById(Long id) throws SQLException {

        if (cacheProvider.cache.containsKey(id)) {
            SimpleLogger.info("[CACHE][SELECTED] CUSTOMER ID: " + id);
            return Optional.of(cacheProvider.cache.get(id));
        }

        String sql = "SELECT * FROM CLIENT WHERE NUMERO = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(statement, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                Customer customer = mapToObject(resultSet);

                cacheProvider.cache.put(customer.getId(), customer);

                SimpleLogger.info("[DB][SELECTED] CUSTOMER ID: " + id);
                return Optional.of(customer);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching customer by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();
    }

    /**
     * Selects all customers from the database or cache.
     *
     * @return A list of all customers.
     * @throws SQLException If an SQL error occurs during the selection.
     */
    @Override
    public List<Customer> selectAll() throws SQLException {

        // Check the cache first
        if (cacheProvider.isCacheValid()){
            SimpleLogger.info("[CACHE][SELECTED] ALL CUSTOMER" );
            return new ArrayList<>(cacheProvider.cache.values());
        }

        String sql = "SELECT * FROM CLIENT";

        List<Customer> customers = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Customer customer = mapToObject(resultSet);
                customers.add(customer);

                cacheProvider.cache.put(customer.getId(), customer);
            }

            cacheProvider.setCacheValid();

            SimpleLogger.info("[DB][SELECTED] ALL CUSTOMER");

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching all customers: " + e.getMessage());
            throw e;
        }

        return customers;
    }

    /**
     * Maps a ResultSet to a Customer object.
     * Depending on the customer type, returns a corresponding Customer Object
     *
     * @param resultSet The ResultSet to map.
     * @return A Customer object corresponding to the ResultSet.
     * @throws SQLException If an SQL error occurs during mapping.
     */
    @Override
    public Customer mapToObject(ResultSet resultSet) throws SQLException {

        Address address = new Address(
                resultSet.getString("pays"),
                resultSet.getString("code_postal"),
                resultSet.getString("localite"),
                resultSet.getString("rue"),
                resultSet.getString("num_rue")
        );

        Long id = resultSet.getLong("numero");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("telephone");
        String name = resultSet.getString("nom");
        String gender = resultSet.getString("est_une_femme");
        String firstName = resultSet.getString("prenom");
        String legalForm = resultSet.getString("forme_sociale");
        String type = resultSet.getString("type");

        // type "O" in database defines an organization customer
        if ("O".equals(type)) {
            return new OrganizationCustomer(id, phone, email, address, name, legalForm);
        }
        // type "P" in database defines a private customer
        else if ("P".equals(type)) {
            return new PrivateCustomer(id, phone, email, address, gender, firstName, name);
        }
        else {
            throw new SQLException("Unknown customer type: " + type);
        }
    }

}
