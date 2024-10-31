package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.persistence.filter.Filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DataMapper<T> {
    void insert(T entity) throws SQLException;

    void update(T entity);

    boolean delete(T entity);

    T selectById(Long id) throws SQLException;

    List<T> selectAll() throws SQLException;

    List<T> selectWhere(Filter filter);

    T mapToObject(ResultSet resultSet) throws SQLException;

}
