package com.dong.demo.v1.domain.readAuth;

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
public class JdbcReadAuthRepository implements ReadAuthRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(ReadAuth readAuth) throws SQLIntegrityConstraintViolationException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO ReadAuthority VALUES(?, ?, ?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, readAuth.getAccountCP());
            preparedStatement.setString(2, readAuth.getFolderCP());
            preparedStatement.setString(3, readAuth.getSymmetricKeyEWA());
            preparedStatement.execute();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getErrorCode() == ICsViolationCode.ENTITY) {
                throw new DuplicatePrimaryKeyException();
            }
            else if (e.getErrorCode() == ICsViolationCode.REFERENTIAL) {
                throw new NoMatchParentRowException();
            }
            else {
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<ReadAuth> findByAccountCP(String accountCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT * FROM ReadAuthority WHERE accountCP=?;";
        ResultSet resultSet = null;
        List<ReadAuth> readAuths = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, accountCP);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                readAuths.add(ReadAuth.builder()
                        .accountCP(resultSet.getString("accountCP"))
                        .folderCP(resultSet.getString("folderCP"))
                        .symmetricKeyEWA(resultSet.getString("symmetricKeyEWA"))
                        .build());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return readAuths;
    }

    @Override
    public void deleteAll() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "DELETE FROM ReadAuthority;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
