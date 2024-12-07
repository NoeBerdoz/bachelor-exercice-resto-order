package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class Main {

    public static void main(String[] args) {

        // WORK IN PROGRESS
        Address address = new Address.Builder().withLocality("Lausanne").withCountryCode("CH").withPostalCode("1000").withStreet("Rue de la Gare").withStreetNumber("1").build();
        Restaurant restaurant = new Restaurant.Builder().withAddress(address).withName("McDo").build();

        EntityManager entityManager = HibernateUtil.getEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            entityManager.persist(restaurant);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
            HibernateUtil.shutdown();
        }

//        (new MainCLI()).run();
    }
}
