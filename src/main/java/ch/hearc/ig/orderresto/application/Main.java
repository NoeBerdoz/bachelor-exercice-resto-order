package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.filter.Filter;
import ch.hearc.ig.orderresto.persistence.data.RestaurantDataMapper;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException {

        // TODO
        // Add validation/error handling for PropertiesLoader in case properties are missing or fail to load.
        // Ensure the connection pool is always closed properly during application shutdown.
        // I will need to manage the database commits myself i think

        DatabaseConnection.getConnection();

        SimpleLogger.info("Trying to get a restaurant by its ID");
        RestaurantDataMapper restaurantDataMapper = new RestaurantDataMapper();

        Restaurant restaurantFound = restaurantDataMapper.selectById(2L);

        SimpleLogger.info("Restaurant found: " + restaurantFound.getName());

        Address addressToInsert = new Address("CH", "2048", "Boot", "Loader", "42");
        Restaurant restaurantToInsert = new Restaurant.Builder()
                .withId(1L)
                .withName("Eat Binary")
                .withAddress(addressToInsert)
                .build();

        restaurantDataMapper.insert(restaurantToInsert);

        List<Restaurant> restaurantList = restaurantDataMapper.selectAll();

        restaurantFound.setName("Eat Binary 2 UPDATED");
        restaurantDataMapper.update(restaurantFound);

        restaurantDataMapper.delete(restaurantToInsert);
        Filter filter = new Filter();
        filter.add("=", "nom", "Eat Binary");
        restaurantDataMapper.selectWhere(filter);



        DatabaseConnection.closeConnection();

//        (new MainCLI()).run();
    }
}
