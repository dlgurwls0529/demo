package com.dong.demo.dbTest;

import com.dong.demo.v1.repository.TestJdbcRepository;
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
import java.sql.*;

import org.springframework.jdbc.datasource.DataSourceUtils;
import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;

@SpringBootTest
// @ActiveProfiles("dev")
public class JDBCTest {

    @Autowired
    TestJdbcRepository testJdbcRepository;

    @Test
    public void connectionTest() {
        try {
            Assertions.assertEquals("a", testJdbcRepository.test());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
