package com.dong.demo.v1.domain.folder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcFolderRepository implements FolderRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(Folder folder) throws SQLIntegrityConstraintViolationException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "INSERT INTO Folder VALUES(?, ?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, folder.getFolderCP());
            preparedStatement.setBoolean(2, folder.getIsTitleOpen());
            preparedStatement.setString(3, folder.getTitle());
            preparedStatement.setString(4, folder.getSymmetricKeyEWF());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(folder.getLastChangedDate()));
            preparedStatement.execute();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw e;
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
    public void updateLastChangedDate(String folderCP, LocalDateTime dateTime) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "UPDATE Folder SET lastChangedDate=? WHERE folderCP=?;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTime));
            preparedStatement.setString(2, folderCP);
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
    public Folder find(String folderCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT * FROM Folder WHERE folderCP=?;";
        ResultSet resultSet = null;
        Folder resultFolder = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, folderCP);
            resultSet = preparedStatement.executeQuery();

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

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return resultFolder;
    }

    @Override
    public List<String[]> findAllFolderCPAndTitle() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "SELECT folderCP, title FROM Folder";
        ResultSet resultSet = null;
        List<String[]> result = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                result.add(new String[]{
                        resultSet.getString("folderCP"),
                        resultSet.getString("title")
                });
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

        return result;
    }

    @Override
    public void deleteAll() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = "DELETE FROM Folder;";

        try {
            connection.prepareStatement(sql).execute();
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
