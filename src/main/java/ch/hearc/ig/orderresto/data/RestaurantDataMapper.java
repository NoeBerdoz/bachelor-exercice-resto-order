package ch.hearc.ig.orderresto.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.utils.OracleConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class RestaurantDataMapper {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantDataMapper.class);


    public RestaurantDataMapper() {
    }

    //    A method to insert a Restaurant to the database.
    public void insert(Restaurant restaurant) throws SQLException {
        // TODO insert the products of the restaurant

        String sql = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?)";

        Connection connection = OracleConnector.getConnectionFromPool();

        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, restaurant.getName());
        statement.setString(2, restaurant.getAddress().getPostalCode());
        statement.setString(3, restaurant.getAddress().getLocality());
        statement.setString(4, restaurant.getAddress().getStreet());
        statement.setString(5, restaurant.getAddress().getStreetNumber());
        statement.setString(6, restaurant.getAddress().getCountryCode());

        int affectedRows = statement.executeUpdate();
        if (affectedRows > 0) {
            logger.info("Restaurant inserted: {}", restaurant.getName());
        }

        // I don't know why this doesn't work yet
//        // Set the new generated ID to the restaurant object
//        ResultSet generatedKeys = statement.getGeneratedKeys();
//        if (generatedKeys.next()) {
//            long generatedId = generatedKeys.getLong(1);
//            restaurant.setId(generatedId);
//            logger.info("Restaurant has now id: {}", restaurant.getId());
//        }
    }

    // A method to retrieve a Restaurant by its ID.
    public Restaurant selectById(Long id) throws SQLException {
        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";

        Connection connection = OracleConnector.getConnectionFromPool();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        ResultSet resultSet = statement.executeQuery();

        // Move cursor to the first result row
        if (resultSet.next()) {
            return mapToRestaurant(resultSet);
        }

        return null;
    }

    // Possibly a method to list all restaurants (optional for now).
    public void selectAll() {
        String sql = "SELECT * FROM RESTAURANT";
    }

    // Map an SQL ResultSet to a Restaurant Java object.
    private Restaurant mapToRestaurant(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("numero");
        String name = resultSet.getString("nom");

        Address address = new Address(
                resultSet.getString("code_postal"),
                resultSet.getString("localite"),
                resultSet.getString("rue"),
                resultSet.getString("num_rue"),
                resultSet.getString("pays")
        );

        return new Restaurant(id, name, address);
    }

}
