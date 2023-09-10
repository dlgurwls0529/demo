package com.dong.demo.v1.service.folder;

import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearch;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test-db")
class SimpleFolderServiceTest {

    @Autowired
    @Qualifier("simple")
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
                Assertions.assertEquals(folderCP, folderService.generateFolder(folderCP, dto));
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

    public void search_ngram_test_1() {
        // given
        List<FoldersGenerateRequestDto> folderToSave = new ArrayList<>();
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i so hungrr")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("hungri...")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("where is bob")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i did not eat dinner")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("give me coffeee")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("hungry hungry hungry hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );

        for(int i = 0; i < folderToSave.size(); i++) {
            try {
                folderService.generateFolder("folderCP"+i, folderToSave.get(i));
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
        }

        // when
        List<FolderSearch> list_sorted = folderService.search("hungryy");

        for (FolderSearch folderSearch : list_sorted) {
            System.out.println("folderCP : " + folderSearch.getFolderCP());
            System.out.println("title : " + folderSearch.getTitle());
            System.out.println();
        }
    }

    public void search_ngram_test_2() {
        // given
        List<FoldersGenerateRequestDto> folderToSave = new ArrayList<>();
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i so hungrr")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("hungri...")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("where is bob")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i did not eat dinner")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("give me coffeee")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("hungry hungry hungry hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );

        for(int i = 0; i < folderToSave.size(); i++) {
            try {
                folderService.generateFolder("folderCP"+i, folderToSave.get(i));
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
        }

        // when
        List<FolderSearch> list_sorted = folderService.search("foldeCP2");

        for (FolderSearch folderSearch : list_sorted) {
            System.out.println("folderCP : " + folderSearch.getFolderCP());
            System.out.println("title : " + folderSearch.getTitle());
            System.out.println();
        }
    }

    public void search_ngram_test_3() {
        // given
        List<FoldersGenerateRequestDto> folderToSave = new ArrayList<>();
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hugry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );

        for(int i = 0; i < folderToSave.size(); i++) {
            try {
                folderService.generateFolder("folderCP"+i, folderToSave.get(i));
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
        }

        // when
        List<FolderSearch> list_sorted = folderService.search("hungry");

        for (FolderSearch folderSearch : list_sorted) {
            System.out.println("folderCP : " + folderSearch.getFolderCP());
            System.out.println("title : " + folderSearch.getTitle());
            System.out.println();
        }
    }

    public void search_ngram_test_4() {
        // given
        List<FoldersGenerateRequestDto> folderToSave = new ArrayList<>();
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );
        folderToSave.add(
                FoldersGenerateRequestDto.builder()
                        .isTitleOpen(true)
                        .title("i am so hungry")
                        .symmetricKeyEWF("sym_TEST")
                        .build()
        );

        for(int i = 0; i < folderToSave.size(); i++) {
            try {
                folderService.generateFolder("folderCP"+i, folderToSave.get(i));
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
        }

        // when
        List<FolderSearch> list_sorted = folderService.search("hungry");

        for (FolderSearch folderSearch : list_sorted) {
            System.out.println("folderCP : " + folderSearch.getFolderCP());
            System.out.println("title : " + folderSearch.getTitle());
            System.out.println();
        }
    }

    @Test
    public void search_ngram_test_5() {
        // given

        // when
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                List<FolderSearch> list_sorted = folderService.search("hungry");

                for (FolderSearch folderSearch : list_sorted) {
                    System.out.print(" ");
                }
            }
        });
    }
}
