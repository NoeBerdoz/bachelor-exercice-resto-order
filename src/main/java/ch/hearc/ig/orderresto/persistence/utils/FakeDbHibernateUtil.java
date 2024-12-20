package ch.hearc.ig.orderresto.persistence.utils;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.service.CustomerService;
import ch.hearc.ig.orderresto.service.ProductOrderService;
import ch.hearc.ig.orderresto.service.RestaurantService;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.math.BigDecimal;

/**
 * Utility class to populate the database with fake data concerning customers, restaurants, and products.
 * The data is added only if the database is empty to prevent duplicate entries.
 */
public class FakeDbHibernateUtil {

    RestaurantService restaurantService = RestaurantService.getInstance();
    CustomerService customerService = CustomerService.getInstance();
    ProductOrderService productOrderService = ProductOrderService.getInstance();

    public void initFakePopulation() {
        if (isDatabaseEmpty()) {
            populateCustomer();
            populateRestaurant();
            SimpleLogger.info("Populated database with fake data");
        } else {
            SimpleLogger.warning("Skipped fake data population, database is not empty");
        }
    }

    private boolean isDatabaseEmpty() {
        return restaurantService.getAllRestaurants().isEmpty() && customerService.getAllCustomers().isEmpty();
    }

    private void populateCustomer() {

        Address address1 = new Address("CH", "2525", "Le Landeron", "Rue du test", "2");
        PrivateCustomer customer1 = new PrivateCustomer(null, "+41 76 000 00 00", "vincent.pazeller@he-arc.ch", address1, "M", "Vincent", "Pazeller");

        customerService.addCustomer(customer1);

        Address address2 = new Address("CH", "2000", "Neuchâtel", "Rue du test", "5b");
        OrganizationCustomer customer2 = new OrganizationCustomer(null, "+41 32 000 00 00", "test@gmail.com", address2, "Hôpital Pourtales", "SA");

        customerService.addCustomer(customer2);
    }

    private void populateRestaurant() {
        Address address1 = new Address("CH", "2000", "Neuchâtel", "Place de La Gare", "2");
        Restaurant restaurant1 = new Restaurant(null, "Alpes Et Lac", address1);
        Product product1 = new Product(null, "Tartare de chevreuil", new BigDecimal(20),  "De saison", restaurant1);

        restaurantService.addRestaurant(restaurant1);
        productOrderService.addProductToRestaurant(product1);

        Address address2 = new Address("CH", "2000", "Neuchâtel", "Pl. Blaise-Cendrars", "5");
        Restaurant restaurant2 = new Restaurant(null, "Les Belgeries", address2);
        Product product2 = new Product(null, "Frites mini", new BigDecimal("5"),  "150g de frites + sauce au choix", restaurant2);
        Product product3 = new Product(null, "Frites normales", new BigDecimal("7.5"),  "250g de frites + sauce au choix", restaurant2);

        restaurantService.addRestaurant(restaurant2);
        productOrderService.addProductToRestaurant(product2);
        productOrderService.addProductToRestaurant(product3);

        Address address3 = new Address("CH", "2000", "Neuchâtel", "Espa. de l'Europe", "1/3");
        Restaurant restaurant3 = new Restaurant(null, "Domino's Pizza", address3);
        Product product4 = new Product(null, "MARGHERITA", new BigDecimal("16"),  "Sauce tomate, extra mozzarella (45% MG/ES)", restaurant3);
        Product product5 = new Product(null, "VÉGÉTARIENNE", new BigDecimal("18"),  "Sauce tomate, mozzarella (45% MG/ES), champignons, poivrons, tomates cherry, olives, oignons rouges", restaurant3);
        Product product6 = new Product(null, "CHEESE & HAM", new BigDecimal("21"),  "Sauce tomate, mozzarella (45% MG/ES), jambon (CH)", restaurant3);

        restaurantService.addRestaurant(restaurant3);
        productOrderService.addProductToRestaurant(product4);
        productOrderService.addProductToRestaurant(product5);
        productOrderService.addProductToRestaurant(product6);

    }
}
