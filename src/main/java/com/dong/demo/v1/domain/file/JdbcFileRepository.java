package com.dong.demo.v1.domain.file;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, folderCP);
            preparedStatement.setString(2, fileId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                if (resultSet.next()) {
                    exists = resultSet.getBoolean("success");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return exists;
    }

    @Override
    public void save(File file) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        INSERT INTO File VALUES(?,
                                UNHEX(REPLACE(?, '-', '')),
                                ?, ?, ?);
                    """;

        // String uuid 를 헥스코드 풀어서 Binary(16)으로 바꿔서 저장.

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, file.getFolderCP());
            preparedStatement.setString(2, file.getFileId());
            preparedStatement.setString(3, file.getSubheadEWS());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(file.getLastChangedDate()));
            preparedStatement.setString(5, file.getContentsEWS());
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
    public void update(File file) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        UPDATE File
                            SET
                                subheadEWS=?,
                                lastChangedDate=?,
                                contentsEWS=?
                            WHERE
                                folderCP=? AND
                                fileId=UNHEX(REPLACE(?, '-', ''));
                    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, file.getSubheadEWS());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(file.getLastChangedDate()));
            preparedStatement.setString(3, file.getContentsEWS());
            preparedStatement.setString(4, file.getFolderCP());
            preparedStatement.setString(5, file.getFileId());
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void updateLastChangedDate(String folderCP, String fileId, LocalDateTime dateTime) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        UPDATE File
                            SET
                                lastChangedDate=?
                            WHERE
                                folderCP=? AND
                                fileId=UNHEX(REPLACE(?, '-', ''));
                    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTime));
            preparedStatement.setString(2, folderCP);
            preparedStatement.setString(3, fileId);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public List<File> findAllOrderByLastChangedDate() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        SELECT
                            folderCP,
                            LOWER(HEX(fileId)) AS fileId_hex,
                            subheadEWS,
                            lastChangedDate,
                            contentsEWS
                        FROM File
                        ORDER BY lastChangedDate DESC;
                    """;

        List<File> files = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();) {

            while (resultSet.next()) {
                files.add(File.builder()
                        .folderCP(resultSet.getString("folderCP"))
                        .fileId(resultSet.getString("fileId_hex"))
                        .subheadEWS(resultSet.getString("subheadEWS"))
                        .lastChangedDate(resultSet.getTimestamp("lastChangedDate").toLocalDateTime())
                        .contentsEWS(resultSet.getString("contentsEWS"))
                        .build()
                );
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return files;
    }

    @Override
    public List<File> findByFolderCP(String folderCP) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        SELECT
                            folderCP,
                            LOWER(HEX(fileId)) AS fileId_hex,
                            subheadEWS,
                            lastChangedDate,
                            contentsEWS
                        FROM File
                        WHERE folderCP=?;
                    """;

        List<File> files = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, folderCP);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                while (resultSet.next()) {
                    files.add(File.builder()
                            .folderCP(resultSet.getString("folderCP"))
                            .fileId(resultSet.getString("fileId_hex"))
                            .subheadEWS(resultSet.getString("subheadEWS"))
                            .lastChangedDate(resultSet.getTimestamp("lastChangedDate").toLocalDateTime())
                            .contentsEWS(resultSet.getString("contentsEWS"))
                            .build());
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return files;
    }

    @Override
    public String findContentsByFolderCPAndFileId(String folderCP, String fileId) {
        // Optional 쓸까 말까
        Connection connection = DataSourceUtils.getConnection(dataSource);
        String sql = """
                        SELECT contentsEWS
                        FROM File
                        WHERE
                            folderCP=? AND
                            fileId=UNHEX(REPLACE(?, '-', ''));
                    """;
        String result = "";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, folderCP);
            preparedStatement.setString(2, fileId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                if (resultSet.next()) {
                    result = resultSet.getString("contentsEWS");
                }
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
        String sql = "DELETE FROM File;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
