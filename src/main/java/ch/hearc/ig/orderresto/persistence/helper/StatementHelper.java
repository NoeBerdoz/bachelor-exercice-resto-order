package ch.hearc.ig.orderresto.persistence.helper;

import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementHelper {

    // Bind parameters to a prepared statement.
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
