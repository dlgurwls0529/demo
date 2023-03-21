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
// @ActiveProfiles("dev")
public class JDBCTest {

    @Autowired
    StringEncryptor encryptor;

    @Test
    public void connectionTest() {
        String text = "O+V+gjJF4nOhoOXYn45gZgKGbz1lKcQT";

        Assertions.assertEquals("test_string", encryptor.decrypt(text));

    }

    public String jasyptEncoding(String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(System.getenv("JASYPT_PASSWORD"));
        return pbeEnc.encrypt(value);
    }
}
