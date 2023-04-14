package com.dong.demo.v1.domain.folder;

import com.dong.demo.v1.domain.folder.Folder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

public interface FolderRepository {
    public void save(Folder folder); // ent
    public void updateLastChangedDate(String folderCP, LocalDateTime dateTime);
    public Folder find(String folderCP);
    public List<String[]> findAllFolderCPAndTitle();
    public void deleteAll();
}
