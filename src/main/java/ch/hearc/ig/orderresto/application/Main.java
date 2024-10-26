package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.data.RestaurantDataMapper;
import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.OracleConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {

        // TODO
        // Add validation/error handling for PropertiesLoader in case properties are missing or fail to load.
        // Integrate a proper logging system (SLF4J with a logger implementation like Logback).
        // Ensure the connection pool is always closed properly during application shutdown.
        // I will need to manage the database commits myself i think

        OracleConnector.isDatabaseConnectable();

        OracleConnector.getConnectionFromPool();

        logger.info("Trying to get a restaurant by its ID");
        RestaurantDataMapper restaurantDataMapper = new RestaurantDataMapper();

        Restaurant restaurantFound = restaurantDataMapper.selectById(1L);

        logger.info("Restaurant found: {}", restaurantFound.toString());

        Address addressToInsert = new Address("CH", "2048", "Boot", "Loader", "42");
        Restaurant restaurantToInsert = new Restaurant.Builder()
                .withId(1L)
                .withName("Eat Binary")
                .withAddress(addressToInsert)
                .build();

        restaurantDataMapper.insert(restaurantToInsert);


        OracleConnector.closePool();

//        (new MainCLI()).run();
    }
}
