package com.dong.demo.dbTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

@SpringBootTest
public class JDBCTest {

    @Autowired
    private DataSource dataSource;

    // 어차피 테스트는 h2 환경에서 돌아가서(ec2 이더라도) insert 된게 막 보이지는 않는다.
    // 한번 닫히면 다 초기화됨.

    @Test
    public void emptyFindTest() {
        Connection connection = DataSourceUtils.getConnection(dataSource);

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                String sql = "select * from Folder;";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
            }
        });

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateNullTargetTest() {
        Connection connection = DataSourceUtils.getConnection(dataSource);

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                connection.prepareStatement("update Folder set title='1';")
                        .execute();
            }
        });

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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