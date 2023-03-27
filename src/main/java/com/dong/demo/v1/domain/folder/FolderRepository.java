package com.dong.demo.v1.domain.folder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

public interface FolderRepository {
    public void save(Folder folder) throws SQLIntegrityConstraintViolationException;
    public List<String[]> findAllFolderCPAndTitle();
    public void updateLastChangedDate(String folderCP, LocalDateTime lastChangedDate);
}
