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
import java.sql.ResultSet;
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
        Connection connection = null;

        Assertions.assertNotNull(dataSource);

        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //
        ResultSet resultSet = null;

        try {
            resultSet = connection.prepareStatement("select * from TEST_TABLE;").executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Assertions.assertEquals("a", resultSet.getString(0));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
