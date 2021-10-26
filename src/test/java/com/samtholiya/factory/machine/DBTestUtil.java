package com.samtholiya.factory.machine;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBTestUtil {

    private DBTestUtil() {
    }

    public static void resetAutoIncrementColumns(ApplicationContext applicationContext)
            throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection dbConnection = dataSource.getConnection()) {
            try (Statement statement = dbConnection.createStatement()) {
                String resetSql = "UPDATE hibernate_sequence set next_val = 1;";
                statement.execute(resetSql);
            }
        }
    }
}
