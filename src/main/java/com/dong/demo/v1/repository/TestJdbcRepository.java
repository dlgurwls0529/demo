package com.dong.demo.v1.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;

@Repository
@RequiredArgsConstructor
public class TestJdbcRepository {

    private final DataSource dataSource;

    public void test() {
        String sql = "insert into TEST_TABLE values(?);";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(dataSource);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "e");
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

}
