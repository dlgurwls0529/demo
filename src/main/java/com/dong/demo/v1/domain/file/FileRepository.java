package com.dong.demo.v1.domain.file;

import java.util.List;

public interface FileRepository {
    public boolean exist(String folderCP, String fileId);
    public void save(File file); // 키 중복 에러 rethrow 해주기
    public void update(File file); // 해당되는 파일 없으면 throw
    public void updateLastChangedDate(String file, String folderCP, String fileId); // 해당 파일 없으면 throw
    public List<File> findAllOrderByLastChangedDate();
    public List<File> findByFolderCP(String folderCP);
    public String findContentsByFolderCPAndFileId(String folderCP, String fileId);
}
