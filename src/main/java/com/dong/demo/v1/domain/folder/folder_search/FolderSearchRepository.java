package com.dong.demo.v1.domain.folder.folder_search;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.service.folder.FolderService;

import java.time.LocalDateTime;
import java.util.List;

public interface FolderSearchRepository {
    public void save(FolderSearch folderSearch);
    public FolderSearch find(String folderCP);
    public List<FolderSearch> findAll();
    public void deleteAll();
}
