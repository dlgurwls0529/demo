package com.dong.demo.v1.domain.file;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

public interface FileRepository {
    public boolean exist(String folderCP, String fileId);
    public void save(File file); // ref, ent
    public void update(File file);
    public void updateLastChangedDate(String folderCP, String fileId, LocalDateTime dateTime);
    public List<File> findAllOrderByLastChangedDate();
    public List<File> findByFolderCP(String folderCP);
    public String findContentsByFolderCPAndFileId(String folderCP, String fileId);
    public void deleteAll();
}
