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
import java.sql.SQLException;

@SpringBootTest
@ActiveProfiles("dev")
public class JDBCTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void connectionTest() throws SQLException {
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                Connection connection = dataSource.getConnection();
            }
        });

        Connection connection = dataSource.getConnection();
        Connection connection1 = dataSource.getConnection();

        Assertions.assertEquals(connection, connection1);

    }
}
