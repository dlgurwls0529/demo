package com.dong.demo.v1.domain.writeAuth;

import com.dong.demo.v1.domain.readAuth.ReadAuth;
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
    public void save(WriteAuth writeAuth) throws SQLIntegrityConstraintViolationException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO WriteAuthority VALUES(?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, writeAuth.getAccountCP());
            preparedStatement.setString(2, writeAuth.getFolderCP());
            preparedStatement.setString(3, writeAuth.getFolderPublicKey());
            preparedStatement.setString(4, writeAuth.getFolderPrivateKeyEWA());
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
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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