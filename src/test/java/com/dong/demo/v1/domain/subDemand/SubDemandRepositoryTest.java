package com.dong.demo.v1.domain.subDemand;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.NoMatchParentRowException;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test-db")
class SubDemandRepositoryTest {

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

        boolean exists_before = subDemandRepository.exist(subDemand.getFolderCP(), subDemand.getAccountCP());

        try {
            folderRepository.save(folder);
            subDemandRepository.save(subDemand);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }


        // when
        boolean exists_after = subDemandRepository.exist(subDemand.getFolderCP(), subDemand.getAccountCP());

        // then
        Assertions.assertFalse(exists_before);
        Assertions.assertTrue(exists_after);

    }

    @Test
    public void save_test() {
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

        boolean exists_before = subDemandRepository.exist(subDemand.getFolderCP(), subDemand.getAccountCP());

        try {
            folderRepository.save(folder);
            subDemandRepository.save(subDemand);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }


        // when
        boolean exists_after = subDemandRepository.exist(subDemand.getFolderCP(), subDemand.getAccountCP());

        // then
        Assertions.assertFalse(exists_before);
        Assertions.assertTrue(exists_after);
    }

    @Test
    public void delete_test() {
        // given
        Folder folder = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        SubDemand subDemand1 = SubDemand.builder()
                .accountCP("accountCP_TEST_1")
                .folderCP(folder.getFolderCP())
                .accountPublicKey("accountPub_TEST")
                .build();

        SubDemand subDemand2 = SubDemand.builder()
                .accountCP("accountCP_TEST_2")
                .folderCP(folder.getFolderCP())
                .accountPublicKey("accountPub_TEST")
                .build();

        try {
            folderRepository.save(folder);
            subDemandRepository.save(subDemand1);
            subDemandRepository.save(subDemand2);

        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        // when
        subDemandRepository.delete(subDemand1.getFolderCP(), subDemand1.getAccountCP());

        // then
        Assertions.assertFalse(subDemandRepository.exist(subDemand1.getFolderCP(), subDemand1.getAccountCP()));
        Assertions.assertTrue(subDemandRepository.exist(subDemand2.getFolderCP(), subDemand2.getAccountCP()));
    }

    @Test
    public void findAccountPublicKeyByFolderCP_test() {
        // given
        Folder folderA = Folder.builder()
                .folderCP("folderCP_TEST_A")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder folderB = Folder.builder()
                .folderCP("folderCP_TEST_B")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        SubDemand subDemand1 = SubDemand.builder()
                .accountCP("accountCP_TEST_1")
                .folderCP(folderA.getFolderCP())
                .accountPublicKey("accountPub_TEST_1")
                .build();

        SubDemand subDemand2 = SubDemand.builder()
                .accountCP("accountCP_TEST_2")
                .folderCP(folderA.getFolderCP())
                .accountPublicKey("accountPub_TEST_2")
                .build();

        SubDemand subDemand3 = SubDemand.builder()
                .accountCP("accountCP_TEST_3")
                .folderCP(folderB.getFolderCP())
                .accountPublicKey("accountPub_TEST_3")
                .build();

        try {
            folderRepository.save(folderA);
            folderRepository.save(folderB);
            subDemandRepository.save(subDemand1);
            subDemandRepository.save(subDemand2);
            subDemandRepository.save(subDemand3);

        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        // when
        List<String> listA = subDemandRepository.findAccountPublicKeyByFolderCP(folderA.getFolderCP());
        List<String> listB = subDemandRepository.findAccountPublicKeyByFolderCP(folderB.getFolderCP());

        // then
        Assertions.assertEquals(listA.get(0), subDemand1.getAccountPublicKey());
        Assertions.assertEquals(listA.get(1), subDemand2.getAccountPublicKey());
        Assertions.assertEquals(listB.get(0), subDemand3.getAccountPublicKey());
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
        } catch (DataAccessException e) {
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

        SubDemand subDemand = SubDemand.builder()
                .accountCP("accountCP_TEST")
                .folderCP(folder.getFolderCP())
                .accountPublicKey("accountPub_TEST")
                .build();

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(folder);
                subDemandRepository.save(subDemand);
            }
        });

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        subDemandRepository.save(subDemand);
                    }
                });
    }

    @Test
    public void non_folder_exception_handle_test() {
        // given
        SubDemand subDemand = SubDemand.builder()
                .accountCP("accountCP_TEST")
                .folderCP("folderCP_TEST")
                .accountPublicKey("accountPub_TEST")
                .build();

        // when
        Assertions.assertThrows(NoMatchParentRowException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        subDemandRepository.save(subDemand);
                    }
                });
    }
}