package com.dong.demo.v1.domain.readAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.NoMatchParentRowException;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test-db")
class ReadAuthRepositoryTest {

    @Autowired
    private ReadAuthRepository readAuthRepository;

    @Autowired
    private FolderRepository folderRepository;

    @AfterEach
    @BeforeEach
    public void cleanUp() {
        readAuthRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    public void save_success_test() {
        // given
        ReadAuth expected_readAuth = ReadAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .symmetricKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        Folder expected_folder = Folder.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        // when, then
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(expected_folder);
                readAuthRepository.save(expected_readAuth);
            }
        };

        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    public void findByAccountCP_success_test() {
        // given
        String accountCP = "fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf";

        // when, then
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                List<ReadAuth> readAuths = readAuthRepository.findByAccountCP(accountCP);
            }
        };

        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    public void deleteAll_success_test() {
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                readAuthRepository.deleteAll();
            }
        };

        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    public void save_3_find_test() {
        Folder expected_folder = Folder.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        ReadAuth expected_readAuth_1 = ReadAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .symmetricKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        ReadAuth expected_readAuth_2 = ReadAuth.builder()
                .accountCP("fsanjflhewdfsawe/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .symmetricKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        ReadAuth expected_readAuth_3 = ReadAuth.builder()
                .accountCP("fsanjflhsdfanjaslhfauwefga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .symmetricKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        try {
            folderRepository.save(expected_folder);
            readAuthRepository.save(expected_readAuth_1);
            readAuthRepository.save(expected_readAuth_2);
            readAuthRepository.save(expected_readAuth_3);

        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        List<ReadAuth> readAuths = readAuthRepository.findByAccountCP(expected_readAuth_1.getAccountCP());

        Assertions.assertEquals(1, readAuths.size());
        Assertions.assertEquals(expected_readAuth_1, readAuths.get(0));

    }

    @Test
    public void save_delete_test() {
        Folder expected_folder = Folder.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        ReadAuth expected_readAuth = ReadAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .symmetricKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        try {
            folderRepository.save(expected_folder);
            readAuthRepository.save(expected_readAuth);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(1,
                readAuthRepository.findByAccountCP(expected_readAuth.getAccountCP()).size());

        readAuthRepository.deleteAll();

        Assertions.assertEquals(0,
                readAuthRepository.findByAccountCP(expected_readAuth.getAccountCP()).size());
    }

    @Test
    public void duplicate_exception_handle_test() {
        // given
        Folder expected_folder = Folder.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        ReadAuth expected_readAuth = ReadAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .symmetricKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(expected_folder);
                readAuthRepository.save(expected_readAuth);
            }
        });

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        readAuthRepository.save(expected_readAuth);
                    }
                });
    }

    @Test
    public void non_folder_exception_handle_test() {
        // given
        ReadAuth readAuth = ReadAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .symmetricKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        // when
        Assertions.assertThrows(NoMatchParentRowException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        readAuthRepository.save(readAuth);
                    }
                });
    }

}