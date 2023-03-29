package com.dong.demo.v1.domain.folder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JdbcFolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @AfterEach
    @BeforeEach
    public void cleanup() {
        folderRepository.deleteAll();
    }

    @Test
    public void saveSuccessTest() {
        // given
        Folder folder = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime.now())
                .build();

        // when
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(folder);
            }
        };

        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    public void findSuccessTest() {
        // given
        String folderCP = "folderCP_TEST";

        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                Folder folder = folderRepository.find(folderCP);
            }
        };

        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    public void save_find_test() {
        Folder expected = Folder.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime.now())
                .build();

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(expected);
                // 무결성 예외 때문에 얘는 예외 처리 해줘야 함
            }
        });

        Folder actual = folderRepository.find(expected.getFolderCP());

        // todo : 이거 해쉬코드로 비교 되니까 도메인 객체에다가 isEquals 오버라이딩해서 구현해야 할 듯

        Assertions.assertNotNull(actual);


    }
}