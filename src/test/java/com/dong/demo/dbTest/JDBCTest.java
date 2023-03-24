package com.dong.demo.dbTest;

import com.dong.demo.v1.repository.TestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.*;

@SpringBootTest
public class JDBCTest {
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRepository testRepository;

    @Test
    public void repositoryInjectionTest() {
        // System.out.println("repo : " + testRepository.getClass());
    }

    @Test
    public void connectionTest() {
        Connection connection = null;
        
        try {
            connection = dataSource.getConnection();
            System.out.println("connection = " + connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
