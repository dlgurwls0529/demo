package com.dong.demo.v1.domain.subDemand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcSubDemandRepository implements SubDemandRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public boolean exist(String folderCP, String accountCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        SELECT EXISTS (
                            SELECT folderCP, accountCP
                            FROM SubscribeDemand
                            WHERE folderCP=? AND accountCP=?
                            LIMIT 1
                        ) AS success;
                    """;

        boolean exists = false;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, folderCP);
            preparedStatement.setString(2, accountCP);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                exists = resultSet.getBoolean("success");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return exists;
    }

    @Override
    public void save(SubDemand demand) throws SQLIntegrityConstraintViolationException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO SubscribeDemand VALUES(?, ?, ?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, demand.getAccountCP());
            preparedStatement.setString(2, demand.getFolderCP());
            preparedStatement.setString(3, demand.getAccountPublicKey());
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(String folderCP, String accountCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "DELETE FROM SubscribeDemand WHERE folderCP=? AND accountCP=?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, folderCP);
            preparedStatement.setString(2, accountCP);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> findAccountPublicKeyByFolderCP(String folderCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        SELECT accountPublicKey
                        FROM SubscribeDemand
                        WHERE folderCP=?;
                    """;

        List<String> accountPubs = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, folderCP);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                accountPubs.add(resultSet.getString("accountPublicKey"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return accountPubs;
    }

    @Override
    public void deleteAll() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "DELETE FROM SubscribeDemand;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
