package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class CustomerDataMapper implements DataMapper<Customer> {
    // TODO manage orders

    private static final CustomerDataMapper instance = new CustomerDataMapper();
    private final Map<Long, Customer> cache = new HashMap<>();

    public CustomerDataMapper() {}

    public static CustomerDataMapper getInstance() {
        return instance;
    }

    @Override
    public boolean insert(Customer customer) throws SQLException {

        String sql = "INSERT INTO CLIENT (email, telephone, nom, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_sociale, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
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
                    SimpleLogger.info("[INSERTED] CUSTOMER WITH ID: " + customer.getId());

                    cache.put(customer.getId(), customer);

                    return true;
                }
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while inserting customer: " + e.getMessage());
            throw e;
        }

        return false;
    }

    @Override
    public boolean update(Customer customer) throws SQLException {

        String sql = "UPDATE CLIENT SET email = ?, telephone = ?, nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ?, est_une_femme = ?, prenom = ?, forme_sociale = ?, type = ? WHERE NUMERO = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
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
                SimpleLogger.info("[UPDATED] CUSTOMER WITH ID: " + customer.getId());

                cache.put(customer.getId(), customer);

                return true;
            } else {
                SimpleLogger.error("[UPDATED] NO CUSTOMER TO UPDATE WITH ID: " + customer.getId());
            }
        } catch (SQLException e) {
            SimpleLogger.error("Error while updating customer: " + e.getMessage());
            throw e;
        }

        return false;
    }

    @Override
    public boolean delete(Customer customer) throws SQLException {
        String sql = "DELETE FROM CLIENT WHERE NUMERO = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            StatementHelper.bindStatementParameters(
                    statement,
                    customer.getId()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                SimpleLogger.info("[DELETED] CUSTOMER WITH ID: " + customer.getId());

                cache.remove(customer.getId());

                return true;
            } else {
                SimpleLogger.warning("[DELETED] NO CUSTOMER TO DELETE WITH ID: " + customer.getId());
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while deleting customer: " + e.getMessage());
            throw e;
        }

        return false;
    }

    @Override
    public Optional<Customer> selectById(Long id) throws SQLException {

        if (cache.containsKey(id)) {
            SimpleLogger.info("[CACHE] Selected CUSTOMER with ID: " + id);
            return Optional.of(cache.get(id));
        }

        String sql = "SELECT * FROM CLIENT WHERE NUMERO = ?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            StatementHelper.bindStatementParameters(statement, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                Customer customer = mapToObject(resultSet);

                cache.put(customer.getId(), customer);

                SimpleLogger.info("[SELECTED] CUSTOMER with ID: " + id);
                return Optional.of(customer);
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching customer by ID: " + e.getMessage());
            throw e;
        }

        return Optional.empty();
    }

    @Override public List<Customer> selectAll() throws SQLException {

        String sql = "SELECT * FROM CLIENT";

        List<Customer> customers = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            Integer countCustomer = 0;
            while (resultSet.next()) {
                Customer customer = mapToObject(resultSet);
                customers.add(customer);

                cache.put(customer.getId(), customer);

                countCustomer++;
            }
            SimpleLogger.info("[SELECTED] CLIENT COUNT: " + countCustomer);

        } catch (SQLException e) {
            SimpleLogger.error("Error while fetching all customers: " + e.getMessage());
            throw e;
        }

        return customers;
    }

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
