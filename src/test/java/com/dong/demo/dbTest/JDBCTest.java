package com.dong.demo.dbTest;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;

@SpringBootTest
// @ActiveProfiles("dev")
public class JDBCTest {

    @Test
    public void connectionTest() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/database-1",
                "dong_demo",
                "mpqe8754"
        );

        Assertions.assertNotNull(connection);
    }
}
