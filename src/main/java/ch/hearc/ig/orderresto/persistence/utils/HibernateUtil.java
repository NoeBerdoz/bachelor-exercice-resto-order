package ch.hearc.ig.orderresto.persistence.utils;

import ch.hearc.ig.orderresto.utils.SimpleLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {

    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ch.hearc.ig.orderresto");

    // Thread-local EntityManager, it will ensure that only one instance is used per thread
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = ThreadLocal.withInitial(() -> entityManagerFactory.createEntityManager());

    public static EntityManager getEntityManager() {
        return entityManagerThreadLocal.get();
    }

    public static void shutdown() {
        if (entityManagerFactory != null) {
            SimpleLogger.info("Closing EntityManagerFactory");
            entityManagerFactory.close();
        }
    }

}
