package com.dong.demo.dbTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.hibernate.cfg.AvailableSettings.URL;

public class JdbcConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void driverManager() throws Exception {
        Connection con1 = dataSource.getConnection();
        System.out.println("con1 = " + con1);

        Connection con2 = dataSource.getConnection();
        System.out.println("con2 = " + con2);
    }

}
