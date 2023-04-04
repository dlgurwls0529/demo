package com.dong.demo.v1.domain.file;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.ICsViolationCode;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.util.UUIDGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
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
    public void update_test() {
        // given
        Folder folder = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file_before = File.builder()
                .folderCP("folderCP_TEST")
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_before")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST_before")
                .build();

        File file_after = File.builder()
                .folderCP(file_before.getFolderCP())
                .fileId(file_before.getFileId())
                .subheadEWS("sub_TEST_after")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST_after")
                .build();

        try {
            folderRepository.save(folder);
            fileRepository.save(file_before);

        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        // when
        fileRepository.update(file_after);

        // then
        List<File> files = fileRepository.findByFolderCP(file_before.getFolderCP());

        Assertions.assertEquals(1, files.size());
        Assertions.assertEquals(file_after, files.get(0));
        Assertions.assertNotEquals(file_before, files.get(0));
    }

    @Test
    public void updateLastChangedDate() {
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
                .subheadEWS("sub_TEST_before")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST_before")
                .build();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LocalDateTime after = LocalDateTime6Digit.now();

        try {
            folderRepository.save(folder);
            fileRepository.save(file);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        // when
        fileRepository.updateLastChangedDate(file.getFolderCP(), file.getFileId(), after);

        // then
        List<File> files = fileRepository.findByFolderCP(file.getFolderCP());

        Assertions.assertEquals(1, files.size());
        Assertions.assertEquals(after, files.get(0).getLastChangedDate());
        Assertions.assertNotEquals(file, files.get(0));
    }

    @Test
    public void findAllOrderByLastChangedDate_test() {
        // given
        Folder folder = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file1 = File.builder()
                .folderCP("folderCP_TEST")
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_1")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST_1")
                .build();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File file2 = File.builder()
                .folderCP("folderCP_TEST")
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_2")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST_2")
                .build();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File file3 = File.builder()
                .folderCP("folderCP_TEST")
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_3")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST_3")
                .build();

        try {
            folderRepository.save(folder);
            fileRepository.save(file1);
            fileRepository.save(file2);
            fileRepository.save(file3);

        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        // when
        List<File> files = fileRepository.findAllOrderByLastChangedDate();

        // then
        Assertions.assertEquals(file3, files.get(0));
        Assertions.assertEquals(file2, files.get(1));
        Assertions.assertEquals(file1, files.get(2));
    }

    @Test
    public void findByFolderCP_test() {
        // given
        Folder folderA = Folder.builder()
                .folderCP("folderCP_TEST_A")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file1_folderA = File.builder()
                .folderCP(folderA.getFolderCP())
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_1A")
                .contentsEWS("con_TEST_1A")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file2_folderA = File.builder()
                .folderCP(folderA.getFolderCP())
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_2A")
                .contentsEWS("con_TEST_2A")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder folderB = Folder.builder()
                .folderCP("folderCP_TEST_B")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file1_folderB = File.builder()
                .folderCP(folderB.getFolderCP())
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_1B")
                .contentsEWS("con_TEST_1B")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        try {
            folderRepository.save(folderA);
            folderRepository.save(folderB);
            fileRepository.save(file1_folderA);
            fileRepository.save(file2_folderA);
            fileRepository.save(file1_folderB);

        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        // when
        List<File> list_A = fileRepository.findByFolderCP(folderA.getFolderCP());
        List<File> list_B = fileRepository.findByFolderCP(folderB.getFolderCP());

        // then
        Assertions.assertEquals(2, list_A.size());
        Assertions.assertEquals(folderA.getFolderCP(), list_A.get(0).getFolderCP());
        Assertions.assertEquals(folderA.getFolderCP(), list_A.get(1).getFolderCP());
        Assertions.assertEquals(1, list_B.size());
        Assertions.assertEquals(folderB.getFolderCP(), list_B.get(0).getFolderCP());
    }

    @Test
    public void findContentsByFolderCPAndFileId_test() {
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

        try {
            folderRepository.save(folder);
            fileRepository.save(file);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        // when
        String contents = fileRepository.findContentsByFolderCPAndFileId(file.getFolderCP(), file.getFileId());

        // then
        Assertions.assertEquals(file.getContentsEWS(), contents);
    }

    @Test
    public void deleteAll_test() {
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

        try {
            folderRepository.save(folder);
            fileRepository.save(file);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        // when
        int before = fileRepository.findAllOrderByLastChangedDate().size();
        fileRepository.deleteAll();
        int after = fileRepository.findAllOrderByLastChangedDate().size();

        // then
        Assertions.assertEquals(1, before);
        Assertions.assertEquals(0, after);
    }

    @Test
    public void duplicate_exception_handle_test() {
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

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(folder);
                fileRepository.save(file);
            }
        });

        // when, then
        Integer code = null;

        try {
            fileRepository.save(file);
        } catch (SQLIntegrityConstraintViolationException e) {
            code = e.getErrorCode();
        }

        Assertions.assertNotNull(code);
        Assertions.assertEquals(ICsViolationCode.ENTITY, code);
    }

    @Test
    public void non_folder_exception_handle_test() {
        File file = File.builder()
                .folderCP("folderCP_TEST")
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .contentsEWS("contents_TEST")
                .build();

        // when
        Integer code = null;

        try {
            fileRepository.save(file);
        } catch (SQLIntegrityConstraintViolationException e) {
            code = e.getErrorCode();
        }

        Assertions.assertNotNull(code);
        Assertions.assertEquals(ICsViolationCode.REFERENTIAL, code);

    }
}