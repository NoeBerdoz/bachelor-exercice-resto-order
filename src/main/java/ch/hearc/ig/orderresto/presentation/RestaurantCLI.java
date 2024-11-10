package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.presentation.AbstractCLI;
import ch.hearc.ig.orderresto.service.RestaurantService;

public class RestaurantCLI extends AbstractCLI {

    public Restaurant getExistingRestaurant() {
        this.ln("Choisissez un restaurant:");
        Object[] allRestaurants = RestaurantService.getInstance().getAllRestaurants().toArray();
        for (int i = 0 ; i < allRestaurants.length ; i++) {
            Restaurant restaurant = (Restaurant) allRestaurants[i];
            this.ln(String.format("%d. %s.", i, restaurant.getName()));
        }
        int index = this.readIntFromUser(allRestaurants.length - 1);
        return (Restaurant) allRestaurants[index];
    }
}
