package com.dong.demo.v1.service.folder;

import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("real-db")
class FolderServiceTest {

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @AfterEach
    @BeforeEach
    public void cleanup() {
        folderRepository.deleteAll();
    }

    @Test
    public void generateFolder_success_test() {
        // given
        String folderCP = "folderCP_TEST";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .title("title_test")
                .symmetricKeyEWF("sym_TEST")
                .build();

        // when
        Assertions.assertEquals(0, folderRepository.findAllFolderCPAndTitle().size());

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderService.generateFolder(folderCP, dto);
            }
        });

        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
    }

    @Test
    public void generateFolder_failure_test() {
        // given
        String folderCP = "folderCP_TEST";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .title("title_test")
                .symmetricKeyEWF("sym_TEST")
                .build();

        // when
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderService.generateFolder(folderCP, dto);
            }
        });

        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());

        Assertions.assertThrows(DuplicatePrimaryKeyException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        folderService.generateFolder(folderCP, dto);
                    }
                });

        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
    }
}