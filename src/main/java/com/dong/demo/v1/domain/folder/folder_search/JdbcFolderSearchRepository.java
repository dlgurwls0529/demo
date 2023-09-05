package com.dong.demo.v1.domain.folder.folder_search;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.ICsViolationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcFolderSearchRepository implements FolderSearchRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(FolderSearch folderSearch) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO Folder_SEARCH VALUES(?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, folderSearch.getFolderCP());
            preparedStatement.setString(2, folderSearch.getTitle());
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
    public FolderSearch find(String folderCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT * FROM Folder_SEARCH WHERE folderCP=?;";
        FolderSearch resultFolderSearch = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, folderCP);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                if (resultSet.next()) {
                    resultFolderSearch = FolderSearch.builder()
                            .folderCP(resultSet.getString("folderCP"))
                            .title(resultSet.getString("title"))
                            .build();
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return resultFolderSearch;
    }

    @Override
    public List<FolderSearch> findAll() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT folderCP, title FROM Folder_SEARCH";
        List<FolderSearch> result = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();) {

            while(resultSet.next()) {
                result.add(FolderSearch.builder()
                        .folderCP(resultSet.getString("folderCP"))
                        .title(resultSet.getString("title"))
                        .build()
                );
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
        String sql = "DELETE FROM Folder_SEARCH;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
