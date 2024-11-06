package ch.hearc.ig.orderresto.persistence.data;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.connection.DatabaseConnection;
import ch.hearc.ig.orderresto.persistence.filter.Filter;
import ch.hearc.ig.orderresto.persistence.helper.StatementHelper;
import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductDataMapper implements DataMapper<Product> {

    // Insert a Product to the database.
    @Override
    public boolean insert(Product product) {

        String sql = "INSERT INTO PRODUIT (FK_RESTO, PRIX_UNITAIRE, NOM, DESCRIPTION) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"NUMERO"});

            StatementHelper.bindStatementParameters(
                    statement,
                    product.getRestaurant().getId(),
                    product.getUnitPrice(),
                    product.getName(),
                    product.getDescription()
            );

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {

                // Set the new generated ID to the product object
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    product.setId(Long.valueOf(generatedId));
                    SimpleLogger.info("[INSERTED] PRODUCT WITH ID: " + product.getId());

                    return true;
                }
            }

        } catch (SQLException e) {
            SimpleLogger.error("Error while inserting product: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean update(Product entity) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Product entity) {
        return false;
    }

    @Override
    public Optional<Product> selectById(Long id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<Product> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public List<Product> selectWhere(Filter filter) {
        return List.of();
    }

    @Override
    public Product mapToObject(ResultSet resultSet) throws SQLException {
        return null;
    }

}
