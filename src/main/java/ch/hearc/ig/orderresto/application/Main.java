package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.data.ProductDataMapper;
import ch.hearc.ig.orderresto.persistence.filter.Filter;
import ch.hearc.ig.orderresto.persistence.data.RestaurantDataMapper;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws SQLException {

        // TODO
        // Add validation/error handling for PropertiesLoader in case properties are missing or fail to load.
        // Ensure the connection pool is always closed properly during application shutdown.
        // I will need to manage the database commits myself i think it's currently on auto-commit.
        // Identity Map pattern integration

        DatabaseConnection.getConnection();

        SimpleLogger.info("Trying to get a restaurant by its ID");

        RestaurantDataMapper restaurantDataMapper = new RestaurantDataMapper();
        ProductDataMapper productDataMapper = new ProductDataMapper();

        Optional<Restaurant> restaurantFound = restaurantDataMapper.selectById(2L);

        if (restaurantFound.isEmpty()) {
            SimpleLogger.error("Restaurant not found");
        } else {
            SimpleLogger.info("Restaurant found: " + restaurantFound.get().getName());
        }

        Address addressToInsert = new Address("CH", "2048", "Boot", "Loader", "42");
        Restaurant restaurantToInsert = new Restaurant.Builder()
                .withId(1L)
                .withName("Eat Binary")
                .withAddress(addressToInsert)
                .build();

        restaurantDataMapper.insert(restaurantToInsert);

        List<Restaurant> restaurantList = restaurantDataMapper.selectAll();

        restaurantToInsert.setName("Eat Binary 2 UPDATED");
        restaurantDataMapper.update(restaurantToInsert);

        restaurantDataMapper.delete(restaurantToInsert);

        Filter filter = new Filter();
        filter.add("=", "nom", "Eat Binary");
        restaurantDataMapper.selectWhere(filter);

        Restaurant restaurantThatHasProduct = new Restaurant.Builder()
                .withId(1L)
                .withName("Brioche Master")
                .withAddress(addressToInsert)
                .build();

        restaurantDataMapper.insert(restaurantThatHasProduct);

        Product productToInsert1 = new Product.Builder()
                .withId(1L)
                .withName("Salade")
                .withUnitPrice(BigDecimal.valueOf(2.9))
                .withDescription("Verte")
                .withRestaurant(restaurantThatHasProduct)
                .build();

        Product productToInsert2 = new Product.Builder()
                .withId(1L)
                .withName("Pain")
                .withUnitPrice(BigDecimal.valueOf(1.3))
                .withDescription("Sec")
                .withRestaurant(restaurantThatHasProduct)
                .build();

        Product productToInsert3 = new Product.Builder()
                .withId(1L)
                .withName("Fromage")
                .withUnitPrice(BigDecimal.valueOf(1.3))
                .withDescription("Bleu")
                .withRestaurant(restaurantThatHasProduct)
                .build();

        productDataMapper.insert(productToInsert1);
        productDataMapper.insert(productToInsert2);
        productDataMapper.insert(productToInsert3);

        productToInsert2.setName("Pain UPDATED");
        productDataMapper.update(productToInsert2);

        productDataMapper.delete(productToInsert3);

        Product productFound = productDataMapper.selectById(21L).orElseThrow();

        SimpleLogger.info("Product found: " + productFound.getName());

        DatabaseConnection.closeConnection();

        (new MainCLI()).run();
    }
}
