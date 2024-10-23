package ch.hearc.ig.orderresto.data;

import ch.hearc.ig.orderresto.business.Restaurant;

import java.sql.SQLException;

public class RestaurantDataMapper {

    public RestaurantDataMapper() {
    }

    //    A method to insert a Restaurant to the database.
    public void insert(Restaurant restaurant) {
        String sql = "INSERT INTO RESTAURANT (numero, nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

//    A method to retrieve a Restaurant by its ID.
    public void selectById(Long id) {
        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";


    }

//    Possibly a method to list all restaurants (optional for now).
    public void selectAll() {
        String sql = "SELECT * FROM RESTAURANT";
    }

}
