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
import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

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

    // 의도적인 write skew 발생
    @Test
    public void concurrentTest() {
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        Connection connection = DataSourceUtils.getConnection(dataSource);
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                connection.prepareStatement("CREATE TABLE CC_TEST(a INTEGER);").execute();
                connection.prepareStatement("INSERT INTO CC_TEST VALUES(1000);").execute();
                connection.setAutoCommit(false);
                System.out.println("flag 0");
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("flag A start");
                    semaphore1.acquire();
                    connection.prepareStatement("update CC_TEST SET a = 10;").execute(); // 2
                    connection.commit();
                    semaphore2.release();
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("flag B start");
                    Integer a = null;
                    ResultSet resultSet = connection.prepareStatement("SELECT a FROM CC_TEST;").executeQuery(); // 1
                    if (resultSet.next()) {
                        a = resultSet.getInt(1);
                        resultSet.close();
                    }
                    semaphore1.release();
                    semaphore2.acquire();

                    // 3
                    if (a == null) {
                        throw new NullPointerException();
                    }

                    if (a == 10) {
                        connection.prepareStatement("update CC_TEST SET a = 20;").execute();
                    }
                    else {
                        connection.prepareStatement("update CC_TEST SET a = 30;").execute();
                    }
                } catch (SQLException | InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                latch.await();
            }
        });

        final Integer[] queryRes = {null};

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                ResultSet resultSet = connection.prepareStatement("SELECT * FROM CC_TEST;").executeQuery();

                if (resultSet.next()) {
                    queryRes[0] = resultSet.getInt(1);
                }

                resultSet.close();
            }
        });

        Assertions.assertNotNull(queryRes[0]);
        // if write skew occur, result is 30.
        Assertions.assertEquals(30, queryRes[0]);

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                connection.setAutoCommit(true);
                connection.commit();
                connection.prepareStatement("DROP TABLE CC_TEST").execute();
            }
        });
        DataSourceUtils.releaseConnection(connection, dataSource);
    }
}