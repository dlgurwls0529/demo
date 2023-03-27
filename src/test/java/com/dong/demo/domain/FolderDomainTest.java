package com.dong.demo.domain;

import com.dong.demo.v1.domain.folder.Folder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class FolderDomainTest {

    @Test
    public void folderInitTest() {

        String expectedFolderCP = "folderCP_TEST";
        boolean expectedTitleOpen = true;
        LocalDateTime expectedLocalDateTime = LocalDateTime.now();
        String expectedSym = "sym_TEST";
        String expectedTitle = "title_TEST";

        Folder folder = Folder.builder()
                .folderCP(expectedFolderCP)
                .isTitleOpen(expectedTitleOpen)
                .lastChangedDate(expectedLocalDateTime)
                .symmetricKeyEWF(expectedSym)
                .title(expectedTitle)
                .build();

        Assertions.assertEquals(expectedFolderCP, folder.getFolderCP());
        Assertions.assertEquals(expectedTitleOpen, folder.getIsTitleOpen());
        Assertions.assertEquals(expectedLocalDateTime, folder.getLastChangedDate());
        Assertions.assertEquals(expectedSym, folder.getSymmetricKeyEWF());
        Assertions.assertEquals(expectedTitle, folder.getTitle());

    }
}
