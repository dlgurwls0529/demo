package com.dong.demo.v1.domain.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcFileRepository implements FileRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public boolean exist(String folderCP, String fileId) {
        // fileId 변환
        // * 안하는 이유는 커버링 인덱스 때메 리프 노드 접근 안해도 됨.
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        SELECT EXISTS (
                            SELECT folderCP, fileId
                            FROM File
                            WHERE folderCP=? AND fileId=UNHEX(REPLACE(?, '-', ''))
                            LIMIT 1
                        ) AS success;
                    """;

        boolean exists = false;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, folderCP);
            preparedStatement.setString(2, fileId);
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
    public void save(File file) throws SQLIntegrityConstraintViolationException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        INSERT INTO File VALUES(?,
                                UNHEX(REPLACE(?, '-', '')),
                                ?, ?, ?);
                    """;

        // String uuid 를 헥스코드 풀어서 Binary(16)으로 바꿔서 저장.

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, file.getFolderCP());
            preparedStatement.setString(2, file.getFileId());
            preparedStatement.setString(3, file.getSubheadEWS());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(file.getLastChangedDate()));
            preparedStatement.setString(5, file.getContentEWS());
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
    public void update(File file) {

    }

    @Override
    public void updateLastChangedDate(String folderCP, String fileId, LocalDateTime dateTime) {

    }

    @Override
    public List<File> findAllOrderByLastChangedDate() {
        return null;
    }

    @Override
    public List<File> findByFolderCP(String folderCP) {
        return null;
    }

    @Override
    public String findContentsByFolderCPAndFileId(String folderCP, String fileId) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
