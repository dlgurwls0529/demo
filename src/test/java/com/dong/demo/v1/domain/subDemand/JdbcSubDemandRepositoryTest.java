package com.dong.demo.v1.domain.subDemand;

import com.dong.demo.v1.domain.file.File;
import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("real-db")
class JdbcSubDemandRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private SubDemandRepository subDemandRepository;

    @BeforeEach
    @AfterEach
    public void setUpAndTearDown() {
        subDemandRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    public void exist_test() {

    }

    @Test
    public void save_test() {
    }

    @Test
    public void delete_test() {
    }

    @Test
    public void findAccountPublicKeyByFolderCP_test() {
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

        SubDemand subDemand = SubDemand.builder()
                .accountCP("accountCP_TEST")
                .folderCP(folder.getFolderCP())
                .accountPublicKey("accountPub_TEST")
                .build();

        try {
            folderRepository.save(folder);
            subDemandRepository.save(subDemand);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        boolean exists_before = subDemandRepository.exist(subDemand.getFolderCP(), subDemand.getAccountCP());

        // when
        subDemandRepository.deleteAll();
        boolean exists_after = subDemandRepository.exist(subDemand.getFolderCP(), subDemand.getAccountCP());

        // then
        Assertions.assertTrue(exists_before);
        Assertions.assertFalse(exists_after);
    }
}