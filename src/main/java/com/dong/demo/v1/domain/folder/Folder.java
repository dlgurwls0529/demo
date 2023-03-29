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

    @Override
    public boolean equals(Object obj) {
        boolean folderCPEquality = this.getFolderCP().equals(((Folder)obj).getFolderCP());
        System.out.println("folderCPEquality = " + folderCPEquality);

        boolean isTitleOpenEquality = this.getIsTitleOpen() == ((Folder)obj).getIsTitleOpen();
        System.out.println("isTitleOpenEquality = " + isTitleOpenEquality);

        boolean titleEquality = this.getTitle().equals(((Folder)obj).getTitle());
        System.out.println("titleEquality = " + titleEquality);

        boolean symEquality = this.getSymmetricKeyEWF().equals(((Folder)obj).getSymmetricKeyEWF());
        System.out.println("symEquality = " + symEquality);

        boolean lastEquality = this.getLastChangedDate().equals(((Folder)obj).getLastChangedDate());
        System.out.println("lastEquality = " + lastEquality);

        return folderCPEquality && isTitleOpenEquality && titleEquality && symEquality && lastEquality;
    }
}
