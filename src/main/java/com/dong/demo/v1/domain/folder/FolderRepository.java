package com.dong.demo.v1.domain.folder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

public interface FolderRepository {
    //todo:명세서 보고 레포지토리 완성. 무결성 제약 조건 주의
    public void save(Folder folder) throws SQLIntegrityConstraintViolationException;
    public List<Folder> findByFolderCP(String folderCP);
    public List<Folder> findAll();
    public void updateLastChangedDate(String folderCP, LocalDateTime lastChangedDate);
}
