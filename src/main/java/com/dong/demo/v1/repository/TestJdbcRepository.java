package com.dong.demo.v1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class TestJdbcRepository {

    private final DataSource dataSource;

    @Autowired
    public TestJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String test() throws SQLException {
        Connection connection = dataSource.getConnection();
        ResultSet resultSet = connection.prepareStatement("select * from TEST_TABLE;").executeQuery();

        return resultSet.getString(0);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
