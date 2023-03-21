package com.dong.demo.dbTest;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
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
@ActiveProfiles("dev")
public class JDBCTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void connectionTest() {
        Assertions.assertNotNull(dataSource);
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                Connection connection = dataSource.getConnection();
                Assertions.assertEquals("HikariProxyConnection@635096154 wrapping conn0: url=jdbc:h2:mem:testdb user=SA", connection.toString());
            }
        });
    }
}
