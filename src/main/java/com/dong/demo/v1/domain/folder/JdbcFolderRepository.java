package com.dong.demo.v1.domain.folder;

import com.dong.demo.v1.domain.folder.folderFindBatchBuilder.FolderFindBatchBuilder;
import com.dong.demo.v1.domain.folder.folderFindBatchBuilder.JdbcFolderFindBatchBuilder;
import com.dong.demo.v1.exception.DataAccessException;
import com.sun.net.httpserver.Authenticator;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.ICsViolationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sun.net.httpserver.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcFolderRepository implements FolderRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(Folder folder) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO Folder VALUES(?, ?, ?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, folder.getFolderCP());
            preparedStatement.setBoolean(2, folder.getIsTitleOpen());
            preparedStatement.setString(3, folder.getTitle());
            preparedStatement.setString(4, folder.getSymmetricKeyEWF());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(folder.getLastChangedDate()));
            preparedStatement.execute();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (ICsViolationCode.isEntityIntegrityViolation(e.getErrorCode())) {
                throw new DuplicatePrimaryKeyException();
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
    public void updateLastChangedDate(String folderCP, LocalDateTime dateTime) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "UPDATE Folder SET lastChangedDate=? WHERE folderCP=?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTime));
            preparedStatement.setString(2, folderCP);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Folder find(String folderCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT * FROM Folder WHERE folderCP=?;";
        Folder resultFolder = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, folderCP);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                // && resultSet.isBeforeFirst() 이거 안하면 안된다. 결과 없는게 isBefore 뭐시기
                /*
                isBeforeFirst() 는 쿼리한 결과의 커서가,
                첫 로우 바로 앞이면 true,
                첫 로우 바로 앞이 아니거나, 결과 로우가 없으면 false.
            */
                if (resultSet.next()) {
                    resultFolder = Folder.builder()
                            .folderCP(resultSet.getString("folderCP"))
                            .isTitleOpen(resultSet.getBoolean("isTitleOpen"))
                            .title(resultSet.getString("title"))
                            .symmetricKeyEWF(resultSet.getString("symmetricKeyEWF"))
                            .lastChangedDate(resultSet.getTimestamp("lastChangedDate").toLocalDateTime())
                            .build();
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return resultFolder;
    }

    @Override
    public List<String[]> findAllFolderCPAndTitle() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT folderCP, title FROM Folder";
        List<String[]> result = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();) {
            while(resultSet.next()) {
                result.add(new String[]{
                        resultSet.getString("folderCP"),
                        resultSet.getString("title")
                });
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return result;
    }

    @Override
    public void deleteAll() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "DELETE FROM Folder;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
