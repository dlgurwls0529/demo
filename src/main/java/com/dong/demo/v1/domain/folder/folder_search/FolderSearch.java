package com.dong.demo.v1.domain.folder.folder_search;

import com.dong.demo.v1.domain.folder.Folder;
import lombok.Builder;

@Builder
public class FolderSearch {

    private final String folderCP;
    private final String title;

    public String getFolderCP() {
        return folderCP;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        Folder obj_folder = (Folder) obj;
        boolean eqFolderCP = folderCP.equals(obj_folder.getFolderCP());
        boolean eqTitle = title.equals(obj_folder.getTitle());
        return eqFolderCP && eqTitle;
    }
}
