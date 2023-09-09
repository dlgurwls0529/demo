package com.dong.demo.v1.domain.folder.folder_search;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test-db")
class FolderSearchRepositoryTest {

    @Autowired
    private FolderSearchRepository folderSearchRepository;

    @Autowired
    private FolderRepository folderRepository;

    @AfterEach
    @BeforeEach
    public void cleanUp() {
        folderSearchRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    void save() {
        // given
        Folder folder_expected = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        // when
        folderSearchRepository.save(folder_expected.getFolderSearch());

        // then
        FolderSearch folderSearch_actual = folderSearchRepository.find(folder_expected.getFolderCP());
        Assertions.assertEquals(folder_expected.getFolderSearch(), folderSearch_actual);
    }

    @Test
    void find() {
        // given
        Folder folder_expected = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        folderSearchRepository.save(folder_expected.getFolderSearch());

        // when
        FolderSearch folderSearch_actual = folderSearchRepository.find(folder_expected.getFolderCP());

        // then
        Assertions.assertEquals(folder_expected.getFolderSearch(), folderSearch_actual);
    }

    @Test
    void findAll() {
        // given
        Folder folder_expected_1 = Folder.builder()
                .folderCP("folderCP_TEST_1")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder folder_expected_2 = Folder.builder()
                .folderCP("folderCP_TEST_2")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        folderSearchRepository.save(folder_expected_1.getFolderSearch());
        folderSearchRepository.save(folder_expected_2.getFolderSearch());

        // when
        List<FolderSearch> folderSearch_actual_list = folderSearchRepository.findAll();

        // then
        Assertions.assertEquals(folder_expected_1.getFolderSearch(), folderSearch_actual_list.get(0));
        Assertions.assertEquals(folder_expected_2.getFolderSearch(), folderSearch_actual_list.get(1));
    }

    @Test
    void deleteAll() {
        // given
        Folder folder_expected_1 = Folder.builder()
                .folderCP("folderCP_TEST_1")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder folder_expected_2 = Folder.builder()
                .folderCP("folderCP_TEST_2")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        folderSearchRepository.save(folder_expected_1.getFolderSearch());
        folderSearchRepository.save(folder_expected_2.getFolderSearch());

        // when
        folderSearchRepository.deleteAll();

        // then
        List<FolderSearch> folderSearch_actual_list = folderSearchRepository.findAll();
        Assertions.assertEquals(0, folderSearch_actual_list.size());
    }
}