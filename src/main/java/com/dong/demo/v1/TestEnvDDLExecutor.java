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

        String folderDDl = """
                CREATE TABLE IF NOT EXISTS Folder (
                  folderCP VARCHAR(60) NOT NULL,
                  isTitleOpen BOOL NOT NULL,
                  title TEXT NOT NULL,
                  symmetricKeyEWF TEXT NOT NULL,
                  lastChangedDate TIMESTAMP(6) NOT NULL,
                  PRIMARY KEY(folderCP)
                );""";

        String fileDDL = """
                CREATE TABLE IF NOT EXISTS File (
                  folderCP VARCHAR(60) NOT NULL,
                  fileId BINARY(16) NOT NULL,
                  subheadEWS TEXT NOT NULL,
                  lastChangedDate TIMESTAMP(6) NOT NULL,
                  contentsEWS TEXT NOT NULL,
                  PRIMARY KEY(folderCP, fileId),
                  FOREIGN KEY(folderCP) REFERENCES Folder(folderCP)
                    ON UPDATE RESTRICT
                    ON DELETE RESTRICT
                );""";

        String writeAuthDDL = """
                CREATE TABLE IF NOT EXISTS WriteAuthority (
                  accountCP VARCHAR(60) NOT NULL,
                  folderCP VARCHAR(60) NOT NULL,
                  folderPublicKey TEXT NOT NULL,
                  folderPrivateKeyEWA TEXT NOT NULL,
                  PRIMARY KEY(accountCP, folderCP),
                  FOREIGN KEY(folderCP) REFERENCES Folder(folderCP)
                    ON UPDATE RESTRICT
                    ON DELETE RESTRICT
                );""";

        String readAuthDDL = """
                CREATE TABLE IF NOT EXISTS ReadAuthority (
                  accountCP VARCHAR(60) NOT NULL,
                  folderCP VARCHAR(60) NOT NULL,
                  symmetricKeyEWA TEXT NOT NULL,
                  PRIMARY KEY(accountCP, folderCP),
                  FOREIGN KEY(folderCP) REFERENCES Folder(folderCP)
                    ON UPDATE RESTRICT
                    ON DELETE RESTRICT
                );""";

        String subDemandDDL = """
                CREATE TABLE IF NOT EXISTS SubscribeDemand (
                  accountCP VARCHAR(60) NOT NULL,
                  folderCP VARCHAR(60) NOT NULL,
                  accountPublicKey TEXT NOT NULL,
                  PRIMARY KEY(accountCP, folderCP),
                  FOREIGN KEY(folderCP) REFERENCES Folder(folderCP)
                    ON UPDATE RESTRICT
                    ON DELETE RESTRICT
                );""";

        try {
            connection.prepareStatement(folderDDl).execute();
            connection.prepareStatement(fileDDL).execute();
            connection.prepareStatement(writeAuthDDL).execute();
            connection.prepareStatement(readAuthDDL).execute();
            connection.prepareStatement(subDemandDDL).execute();

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
