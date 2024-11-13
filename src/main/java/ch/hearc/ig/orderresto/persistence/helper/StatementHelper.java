package ch.hearc.ig.orderresto.persistence.helper;

import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Helper class for binding parameters to SQL prepared statements.
 * This class handles various data types and their specific binding to SQL queries.
 */
public class StatementHelper {

    /**
     * Binds the given parameters to a prepared statement.
     * This method dynamically binds multiple parameters of various types
     * to a prepared statement in the correct format.
     *
     * @param statement the prepared statement to bind parameters to
     * @param sqlParameters the parameters to bind, in the order they appear in the SQL query
     */
    public static void bindStatementParameters(PreparedStatement statement, Object... sqlParameters) {
        for (int i = 0; i < sqlParameters.length; i++) {
            Object parameter = sqlParameters[i];
            try {
                if (parameter instanceof String) {
                    statement.setString(i + 1, (String) parameter);
                } else if (parameter instanceof Long) {
                    statement.setLong(i + 1, (Long) parameter);
                } else if (parameter instanceof BigDecimal) {
                    statement.setBigDecimal(i + 1, (BigDecimal) parameter);
                } else if (parameter instanceof Boolean) {
                    // Oracle doesn't handle native boolean type, in database it's stored as 'O' for True and 'N' for False.
                    statement.setString(i + 1, (Boolean) parameter ? "O" : "N");
                } else {
                    statement.setObject(i + 1, parameter);
                }
            } catch (SQLException e) {
                SimpleLogger.error("Error while binding statement: " + e.getMessage());
            }
        }
    }
}
