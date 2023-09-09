package com.dong.demo.v1.domain.folder.folderFindBatchBuilder;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test-db")
class FolderFindBatchBuilderTest {

    @Autowired
    private ObjectProvider<FolderFindBatchBuilder> provider;

    @Autowired
    private FolderRepository folderRepository;

    @AfterEach
    @BeforeEach
    public void cleanup() {
        folderRepository.deleteAll();
    }

    @Test
    public void batch_find_normal_test() {
        // given
        List<Folder> oracle_list = new ArrayList<>();
        int size = 10;

        for (int i = 1; i <= size; i++) {
            Folder oracle = Folder.builder()
                    .folderCP("folderCP_TEST_" + i)
                    .isTitleOpen(true)
                    .title("title_TEST_" + i)
                    .symmetricKeyEWF("sym_TEST")
                    .lastChangedDate(LocalDateTime6Digit.now())
                    .build();

            oracle_list.add(oracle);
            folderRepository.save(oracle);
        }

        // when
        FolderFindBatchBuilder builder = provider.getObject();

        for (Folder folder : oracle_list) {
            builder.append(folder.getFolderCP());
        }

        List<Folder> actual_list = builder.execute();

        oracle_list.sort(new Comparator<Folder>() {
            @Override
            public int compare(Folder o1, Folder o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        actual_list.sort(new Comparator<Folder>() {
            @Override
            public int compare(Folder o1, Folder o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        // then
        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(oracle_list.get(i), actual_list.get(i));
        }
    }

    @Test
    public void batch_find_empty_test() {
        // given

        // when
        FolderFindBatchBuilder builder = provider.getObject();
        List<Folder> actual_list = builder.execute();

        // then
        Assertions.assertEquals(0, actual_list.size());
    }

    @Test
    public void batch_prototype_scope_construct_test() {
        FolderFindBatchBuilder builder1 = provider.getObject();
        FolderFindBatchBuilder builder2 = provider.getObject();

        Assertions.assertNotSame(builder1, builder2);
    }
}