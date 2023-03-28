package com.dong.demo.v1.domain.folder;

import com.dong.demo.v1.domain.folder.Folder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

public interface FolderRepository {
    public boolean exist(String folderCP);
    public void save(Folder folder); // key 중복 에러 rethrow
    void updateLastChangedDate(String folderCP, LocalDateTime dateTime); // 해당되는 폴더 없으면 throw
    List<String[]> findAllFolderCPAndTitle(); // 없으면 예외 아니고 그냥 null
}
