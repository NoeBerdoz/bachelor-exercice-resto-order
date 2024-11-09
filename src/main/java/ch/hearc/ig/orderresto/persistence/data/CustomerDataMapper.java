package ch.hearc.ig.orderresto.persistence.data;

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


public class CustomerDataMapper implements DataMapper {

    private static final CustomerDataMapper instance = new CustomerDataMapper();
    private final Map<Long, Customer> cache = new HashMap<>();

    public CustomerDataMapper() {}

    public static CustomerDataMapper getInstance() {
        return instance;
    }

    @Override
    public boolean insert(Customer customer) {

        String sql = "INSERT INTO CLIENT (email, telephone, nom, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_sociale, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});

            setInsertParametersBasedOnType(statement, customer);

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
                    customer.getAddress().getStreetNumber(),
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


    // I don't know if i should do this way, or with the ternary operations...
    private void setInsertParametersBasedOnType (PreparedStatement statement, Customer customer) {

        if (customer instanceof OrganizationCustomer) {
            OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
            StatementHelper.bindStatementParameters(
                    statement,
                    organizationCustomer.getEmail(),
                    organizationCustomer.getPhone(),
                    organizationCustomer.getName(),
                    organizationCustomer.getAddress().getPostalCode(),
                    organizationCustomer.getAddress().getLocality(),
                    organizationCustomer.getAddress().getStreet(),
                    organizationCustomer.getAddress().getStreetNumber(),
                    organizationCustomer.getAddress().getCountryCode(),
                    null, // organizationCustomer has no genre
                    null, // organizationCustomer has no first name
                    organizationCustomer.getLegalForm(),
                    "O" // type organization in database is "O"
            );
        }

        if (customer instanceof PrivateCustomer) {
            PrivateCustomer privateCustomer = (PrivateCustomer) customer;
            StatementHelper.bindStatementParameters(
                    statement,
                    privateCustomer.getEmail(),
                    privateCustomer.getPhone(),
                    privateCustomer.getLastName(), // privateCustomer has no name
                    privateCustomer.getAddress().getPostalCode(),
                    privateCustomer.getAddress().getLocality(),
                    privateCustomer.getAddress().getStreet(),
                    privateCustomer.getAddress().getStreetNumber(),
                    privateCustomer.getAddress().getCountryCode(),
                    privateCustomer.getGender(),
                    privateCustomer.getFirstName(),
                    null // privateCustomer has no legal form
            );
        }

    }



}
