package com.dong.demo.v1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TestJdbcRepository {

    private final DataSource dataSource;

    @Autowired
    public TestJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> test() {
        try {
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection.prepareStatement("select * from TEST_TABLE;").executeQuery();

            List<String> list = new ArrayList<>();
            list.add(resultSet.getString(0));
            resultSet.next();
            list.add(resultSet.getString(1));
            resultSet.next();
            list.add(resultSet.getString(2));
            resultSet.next();

            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
