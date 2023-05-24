package com.dong.demo.v1.domain.subDemand;

import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.ICsViolationCode;
import com.dong.demo.v1.exception.NoMatchParentRowException;
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
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return exists;
    }

    @Override
    public void save(SubDemand demand) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO SubscribeDemand VALUES(?, ?, ?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, demand.getAccountCP());
            preparedStatement.setString(2, demand.getFolderCP());
            preparedStatement.setString(3, demand.getAccountPublicKey());
            preparedStatement.execute();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (ICsViolationCode.isEntityIntegrityViolation(e.getErrorCode())) {
                throw new DuplicatePrimaryKeyException();
            }
            else if (ICsViolationCode.isReferentialIntegrityViolation(e.getErrorCode())) {
                throw new NoMatchParentRowException();
            }
            else {
                throw new DataAccessException(e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
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
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
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
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
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
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
