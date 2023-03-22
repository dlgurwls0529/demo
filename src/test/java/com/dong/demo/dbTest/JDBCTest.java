package com.dong.demo.dbTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.sql.*;

// 안띄우고(띄우기 전에) 주입받은 데이터소스는 프로필 아직 없어서 디폴트인 h2db
@SpringBootTest
@PropertySource("classpath:src/main/resources/application-real.properties")
public class JDBCTest {

    @Value("${real.test}")
    private String s;

    // @Value("${spring.datasource.url}")
    private String url;

    // @Value("${spring.datasource.username}")
    private String username;

    // @Value("${spring.datasource.password}")
    private String password;

    // @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Test
    public void propertyTest() {
        Assertions.assertEquals("test", s);
    }

    public void connectionTest() {
        Assertions.assertEquals("test", s);

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connection = " + connection);

            ResultSet resultSet = connection.prepareStatement("select * from TEST_TABLE").executeQuery();

            while(resultSet.next()) {
                System.out.println("res : " + resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
