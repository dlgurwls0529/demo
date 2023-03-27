package com.dong.demo.dbTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

@SpringBootTest
public class JDBCTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void insertionTest() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "insert into Folder values('test', true, 'test', 'test', ?);";

        try {
            connection.setAutoCommit(true);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "select * from Folder;";
        ResultSet resultSet = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery(sql);
            String test = resultSet.getString("folderCP");
            Assertions.assertEquals("test", test);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void duplicateTest() {
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String driving_sql = "insert into Folder values('test', true, 'test', 'test', ?);";
        String driven_sql = "insert into SubscribeDemand values('test', 'test', 'test');";

        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        PreparedStatement pstmt = connection.prepareStatement(driving_sql);
                        pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                        pstmt.execute();
                        pstmt = connection.prepareStatement(driven_sql);
                        pstmt.execute();
                        pstmt.execute();
                    }
                });

        try {
            connection.rollback();
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*try {
            pstmt = connection.prepareStatement(driving_sql);
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.execute();
            pstmt = connection.prepareStatement(driven_sql);
            pstmt.execute();
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
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