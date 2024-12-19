package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.FakeDbHibernate;
import ch.hearc.ig.orderresto.presentation.MainCLI;

public class Main {

    public static void main(String[] args) {

        // CREATE A RESTAURANT
        FakeDbHibernate fakeDbHibernate = new FakeDbHibernate();
        fakeDbHibernate.initFakePopulation();

        (new MainCLI()).run();
    }
}
