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
@ActiveProfiles("dev")
public class JDBCTest {

    @Autowired
    TestJdbcRepository testJdbcRepository;

    @Test
    public void connectionTest() {
        String sql = "select * from TEST_TABLE;";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = testJdbcRepository.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            String res0 = rs.getString(0); rs.next();
            System.out.println("res0 = " + res0);
            Assertions.assertEquals("a", res0);

            String res1 = rs.getString(1); rs.next();
            System.out.println("res1 = " + res1);
            Assertions.assertEquals("b", res1);

            String res2 = rs.getString(2); rs.next();
            System.out.println("res2 = " + res2);
            Assertions.assertEquals("c", res2);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
