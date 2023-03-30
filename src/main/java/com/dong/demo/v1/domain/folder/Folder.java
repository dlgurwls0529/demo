package com.dong.demo.v1.domain.folder;


import lombok.Builder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
public class Folder {

    private final String folderCP;
    private final boolean isTitleOpen;
    private final String title;
    private final String symmetricKeyEWF;
    private final LocalDateTime lastChangedDate;

    public String getFolderCP() {
        return folderCP;
    }

    public boolean getIsTitleOpen() {
        return isTitleOpen;
    }

    public String getTitle() {
        return title;
    }

    public String getSymmetricKeyEWF() {
        return symmetricKeyEWF;
    }

    public LocalDateTime getLastChangedDate() {
        return lastChangedDate;
    }

    @Override
    public boolean equals(Object obj) {
        boolean folderCPEquality = this.folderCP.equals(((Folder)obj).folderCP);
        boolean isTitleOpenEquality = this.isTitleOpen == ((Folder)obj).isTitleOpen;
        boolean titleEquality = this.title.equals(((Folder)obj).title);
        boolean symEquality = this.symmetricKeyEWF.equals(((Folder)obj).symmetricKeyEWF);
        boolean lastEquality = this.lastChangedDate.equals(((Folder)obj).lastChangedDate);

        return folderCPEquality && isTitleOpenEquality && titleEquality && symEquality && lastEquality;
    }
}
