package com.dong.demo.v1.domain.writeAuth;

import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.ICsViolationCode;
import com.dong.demo.v1.exception.NoMatchParentRowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcWriteAuthRepository implements WriteAuthRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(WriteAuth writeAuth) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO WriteAuthority VALUES(?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, writeAuth.getAccountCP());
            preparedStatement.setString(2, writeAuth.getFolderCP());
            preparedStatement.setString(3, writeAuth.getFolderPublicKey());
            preparedStatement.setString(4, writeAuth.getFolderPrivateKeyEWA());
            preparedStatement.execute();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == ICsViolationCode.ENTITY) {
                throw new DuplicatePrimaryKeyException();
            }
            else if (e.getErrorCode() == ICsViolationCode.REFERENTIAL) {
                throw new NoMatchParentRowException();
            }
            else {
                throw new DataAccessException();
            }
        } catch (SQLException e) {
            throw new DataAccessException();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<WriteAuth> findByAccountCP(String accountCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT * FROM WriteAuthority WHERE accountCP=?;";
        ResultSet resultSet = null;
        List<WriteAuth> writeAuths = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, accountCP);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                writeAuths.add(WriteAuth.builder()
                        .accountCP(resultSet.getString("accountCP"))
                        .folderCP(resultSet.getString("folderCP"))
                        .folderPublicKey(resultSet.getString("folderPublicKey"))
                        .folderPrivateKeyEWA(resultSet.getString("folderPrivateKeyEWA"))
                        .build()
                );
            }

        } catch (SQLException e) {
            throw new DataAccessException();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return writeAuths;
    }

    @Override
    public void deleteAll() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "DELETE FROM WriteAuthority;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
