package com.dong.demo.v1.domain.folder;

import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.domain.subDemand.SubDemandRepository;
import com.dong.demo.v1.domain.writeAuth.WriteAuthRepository;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.ICsViolationCode;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("real-db")
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
                .lastChangedDate(LocalDateTime6Digit.now())
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
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(expected);
                // 무결성 예외 때문에 얘는 예외 처리 해줘야 함
            }
        });

        Folder actual = folderRepository.find(expected.getFolderCP());

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findFolderCPAndTitleTest() {
        Folder expected1 = Folder.builder()
                .folderCP("fdsafdgasdsg7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder expected2 = Folder.builder()
                .folderCP("ljhiwrnjkudsjNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder expected3 = Folder.builder()
                .folderCP("pewjrwpfeipfnNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        List<Folder> expectedList = new ArrayList<>();
        expectedList.add(expected1);
        expectedList.add(expected2);
        expectedList.add(expected3);

        try {
            folderRepository.save(expected1);
            folderRepository.save(expected2);
            folderRepository.save(expected3);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        List<String[]> actualList = folderRepository.findAllFolderCPAndTitle();

        Assertions.assertEquals(3, actualList.size());

        for(int i = 0 ; i < expectedList.size(); i++) {
            Assertions.assertNotNull(actualList.get(i));
            Assertions.assertEquals(expectedList.get(i).getFolderCP(), actualList.get(i)[0]);
            Assertions.assertEquals(expectedList.get(i).getTitle(), actualList.get(i)[1]);
        }
    }

    @Test
    public void updateLastChangedDateTest() {
        // given
        Folder expected = Folder.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LocalDateTime localDateTime = LocalDateTime6Digit.now();

        try {
            folderRepository.save(expected);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        // when
        folderRepository.updateLastChangedDate(expected.getFolderCP(), localDateTime);

        // then
        Folder actual = folderRepository.find(expected.getFolderCP());
        Assertions.assertNotEquals(expected.getLastChangedDate(), actual.getLastChangedDate());
        Assertions.assertEquals(localDateTime, actual.getLastChangedDate());

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

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(folder);
            }
        });

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        folderRepository.save(folder);
                    }
                });
    }
}