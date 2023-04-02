package com.dong.demo.v1.domain.file;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.util.UUIDGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("real-db")
class JdbcFileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @AfterEach
    @BeforeEach
    public void cleanUp() {
        fileRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    public void exist_test() {
        // given
        Folder folder = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file = File.builder()
                .folderCP("folderCP_TEST")
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST")
                .build();


        // when
        boolean exist_before = fileRepository.exist(file.getFolderCP(), file.getFileId());

        try {
            folderRepository.save(folder);
            fileRepository.save(file);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        boolean exist_after = fileRepository.exist(file.getFolderCP(), file.getFileId());

        // then
        Assertions.assertFalse(exist_before);
        Assertions.assertTrue(exist_after);
    }

    @Test
    public void save_success_test() {
        // given
        Folder folder = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file = File.builder()
                .folderCP("folderCP_TEST")
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST")
                .build();

        // when, then
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(folder);
                fileRepository.save(file);
            }
        });

    }

    @Test
    void update() {
    }

    @Test
    void updateLastChangedDate() {
    }

    @Test
    void findAllOrderByLastChangedDate() {
    }

    @Test
    void findByFolderCP() {
    }

    @Test
    void findContentsByFolderCPAndFileId() {
    }

    @Test
    void deleteAll() {
    }
}