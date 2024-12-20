package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.utils.FakeDbHibernateUtil;
import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {

        SimpleLogger.info("Starting OrderResto application");

        // As Hibernate is very verbose by default, set its logging off
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);

        // Create some dummy data when the database is empty
        FakeDbHibernateUtil fakeDbHibernateUtil = new FakeDbHibernateUtil();
        fakeDbHibernateUtil.initFakePopulation();

        (new MainCLI()).run();
    }
}
