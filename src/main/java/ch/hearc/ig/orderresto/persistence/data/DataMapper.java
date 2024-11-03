package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.persistence.filter.Filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DataMapper<T> {
    boolean insert(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean delete(T entity);

    Optional<T> selectById(Long id) throws SQLException;

    List<T> selectAll() throws SQLException;

    List<T> selectWhere(Filter filter);

    T mapToObject(ResultSet resultSet) throws SQLException;

}
