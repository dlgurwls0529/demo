package com.dong.demo.v1;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class TestEnvDDLExecutor {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void executeDDL() {
        Connection connection = null;
        connection = DataSourceUtils.getConnection(dataSource);

        String ddl1 = "CREATE TABLE IF NOT EXISTS Folder (\n" +
                "  folderCP CHAR(60) PRIMARY KEY NOT NULL,\n" +
                "  isTitleOpen BOOL NOT NULL,\n" +
                "  title TEXT NOT NULL,\n" +
                "  symmetricKeyEWF TEXT NOT NULL,\n" +
                "  lastChangedDate TIMESTAMP(6) NOT NULL\n" +
                ");";

        String ddl2 = "CREATE TABLE IF NOT EXISTS WriteAuthority (\n" +
                "  accountCP CHAR(60) NOT NULL,\n" +
                "  folderCP CHAR(60) NOT NULL,\n" +
                "  folderPublicKey TEXT NOT NULL,\n" +
                "  folderPrivateKeyEWA TEXT NOT NULL,\n" +
                "  PRIMARY KEY (accountCP, folderCP)\n" +
                ");";

        String ddl3 = "CREATE TABLE IF NOT EXISTS ReadAuthority (\n" +
                "  accountCP CHAR(60) NOT NULL,\n" +
                "  folderCP CHAR(60) NOT NULL,\n" +
                "  symmetricKeyEWA TEXT NOT NULL,\n" +
                "  PRIMARY KEY (accountCP, folderCP)\n" +
                ");";

        String ddl4 = "CREATE TABLE IF NOT EXISTS SubscribeDemand (\n" +
                "  accountCP CHAR(60) NOT NULL,\n" +
                "  folderCP CHAR(60) NOT NULL,\n" +
                "  accountPublicKey TEXT NOT NULL,\n" +
                "  PRIMARY KEY (accountCP, folderCP)\n" +
                ");";

        String ddl5 = "CREATE TABLE IF NOT EXISTS File (\n" +
                "  folderCP CHAR(60) NOT NULL,\n" +
                "  fileId BINARY(16) NOT NULL,\n" +
                "  subheadEWS TEXT NOT NULL,\n" +
                "  lastChangedDate TIMESTAMP(6) NOT NULL,\n" +
                "  contentsEWS TEXT NOT NULL,\n" +
                "  PRIMARY KEY (folderCP, fileId)\n" +
                ");";

        String constraints =
                "ALTER TABLE WriteAuthority ADD FOREIGN KEY (folderCP) REFERENCES Folder (folderCP);\n" +
                "ALTER TABLE ReadAuthority ADD FOREIGN KEY (folderCP) REFERENCES Folder (folderCP);\n" +
                "ALTER TABLE SubscribeDemand ADD FOREIGN KEY (folderCP) REFERENCES Folder (folderCP);\n" +
                "ALTER TABLE File ADD FOREIGN KEY (folderCP) REFERENCES Folder (folderCP);";

        try {
            connection.prepareStatement(ddl1).execute();
            connection.prepareStatement(ddl2).execute();
            connection.prepareStatement(ddl3).execute();
            connection.prepareStatement(ddl4).execute();
            connection.prepareStatement(ddl5).execute();
            connection.prepareStatement(constraints).execute();

            PreparedStatement preparedStatement = connection.prepareStatement("insert into Folder values('tests', true, 'tests', 'tests', ?);");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
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
