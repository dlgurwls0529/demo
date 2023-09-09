package com.dong.demo.v1.domain.folder.folderFindBatchBuilder;

import com.dong.demo.v1.domain.folder.Folder;

import java.util.List;

public interface FolderFindBatchBuilder {
    public FolderFindBatchBuilder append(String folderCP);
    public List<Folder> execute();
}
