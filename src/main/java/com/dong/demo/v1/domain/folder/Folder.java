package com.dong.demo.v1.domain.folder;


import lombok.Builder;

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
}
