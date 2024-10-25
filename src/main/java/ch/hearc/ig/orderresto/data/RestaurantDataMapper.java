package ch.hearc.ig.orderresto.data;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.utils.OracleConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RestaurantDataMapper {

    public RestaurantDataMapper() {
    }

    //    A method to insert a Restaurant to the database.
    public void insert(Restaurant restaurant) {
        String sql = "INSERT INTO RESTAURANT (numero, nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
