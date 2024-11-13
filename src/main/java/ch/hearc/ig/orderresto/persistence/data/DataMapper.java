package ch.hearc.ig.orderresto.persistence.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface for data access operations, to be implemented by specific DataMappers.
 * Provides basic CRUD operations and the ability to map result sets to objects.
 *
 * @param <T> the type of entity being mapped
 */
public interface DataMapper<T> {

    /**
     * Inserts the given entity into the database.
     *
     * @param entity the entity to insert
     * @return true if the entity was successfully inserted, false otherwise
     * @throws SQLException if there is a database error
     */
    boolean insert(T entity) throws SQLException;

    /**
     * Updates the given entity in the database.
     *
     * @param entity the entity to update
     * @return true if the entity was successfully updated, false otherwise
     * @throws SQLException if there is a database error
     */
    boolean update(T entity) throws SQLException;

    /**
     * Deletes the given entity from the database.
     *
     * @param entity the entity to delete
     * @return true if the entity was successfully deleted, false otherwise
     * @throws SQLException if there is a database error
     */
    boolean delete(T entity) throws SQLException;

    /**
     * Selects an entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return an Optional containing the entity if found, or an empty Optional if not
     * @throws SQLException if there is a database error
     */
    Optional<T> selectById(Long id) throws SQLException;

    /**
     * Selects all entities from the database.
     *
     * @return a list of all entities
     * @throws SQLException if there is a database error
     */
    List<T> selectAll() throws SQLException;

    /**
     * Maps a result set to an entity.
     *
     * @param resultSet the result set to map
     * @return the entity represented by the result set
     * @throws SQLException if there is a database error
     */
    T mapToObject(ResultSet resultSet) throws SQLException;

}
