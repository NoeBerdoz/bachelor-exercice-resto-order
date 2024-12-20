package ch.hearc.ig.orderresto.persistence.utils;

import ch.hearc.ig.orderresto.utils.SimpleLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility class for managing Hibernate's EntityManagerFactory and EntityManager.
 * Provides thread-local EntityManager instances and handles resource cleanup.
 */
public class HibernateUtil {

    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ch.hearc.ig.orderresto");

    // Thread-local EntityManager, it will ensure that only one instance is used per thread
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = ThreadLocal.withInitial(() -> entityManagerFactory.createEntityManager());

    /**
     * Provides the thread-local EntityManager instance.
     *
     * @return EntityManager for the current thread.
     */
    public static EntityManager getEntityManager() {
        return entityManagerThreadLocal.get();
    }

    /**
     * Shuts down the EntityManagerFactory and releases resources.
     */
    public static void shutdown() {
        if (entityManagerFactory != null) {
            SimpleLogger.info("Closing EntityManagerFactory");
            entityManagerFactory.close();
        }
    }

}
