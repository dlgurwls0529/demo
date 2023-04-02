package com.dong.demo.v1.domain.file;

import com.dong.demo.v1.domain.folder.Folder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class File {

    private final String folderCP;
    private final String fileId;
    private final String subheadEWS;
    private final LocalDateTime lastChangedDate;
    private final String contentsEWS;

    @Override
    public boolean equals(Object obj) {
        File target = (File) obj;
        boolean folderCPEquality = this.getFolderCP().equals(target.getFolderCP());
        boolean fileIdEquality = this.getFileId().equals(target.getFileId());
        boolean subheadEquality = this.getSubheadEWS().equals(target.getSubheadEWS());
        boolean dateEquality = this.getLastChangedDate().equals(target.getLastChangedDate());
        boolean contentEquality = this.getContentsEWS().equals(target.getContentsEWS());

        return folderCPEquality && fileIdEquality && subheadEquality && dateEquality && contentEquality;
    }
}
