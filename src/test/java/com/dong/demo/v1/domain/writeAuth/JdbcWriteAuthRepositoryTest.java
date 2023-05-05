package com.dong.demo.v1.domain.writeAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.subDemand.SubDemand;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.ICsViolationCode;
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

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("real-db")
class JdbcWriteAuthRepositoryTest {

    @Autowired
    private WriteAuthRepository writeAuthRepository;

    @Autowired
    private FolderRepository folderRepository;

    @AfterEach
    @BeforeEach
    public void cleanUp() {
        writeAuthRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    public void save_success_test() {
        // given
        WriteAuth expected_writeAuth = WriteAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .folderPublicKey("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .folderPrivateKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
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
                writeAuthRepository.save(expected_writeAuth);
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
                List<WriteAuth> writeAuths = writeAuthRepository.findByAccountCP(accountCP);
            }
        };

        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    public void deleteAll_success_test() {
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                writeAuthRepository.deleteAll();
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

        WriteAuth expected_writeAuth_1 = WriteAuth.builder()
                .accountCP("fsanjflhas35215fdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .folderPublicKey("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia3fjdsklfjskdlfjskdfsdfsdfsdfsdgsfd=3r232=2/32")
                .folderPrivateKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        WriteAuth expected_writeAuth_2 = WriteAuth.builder()
                .accountCP("fsanjflhewdf51125sawe/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .folderPublicKey("mdsjlkafnuwilaw3fadfewfdf32f3fjdsklfjskdlfjskdfsdfsdfsdfsdgsfd=3r232=2/32")
                .folderPrivateKeyEWA("mdsjlkafnuwilafdsgsdfsge3wga8243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        WriteAuth expected_writeAuth_3 = WriteAuth.builder()
                .accountCP("fsanjflhsdf5331111111anjaslhfauwefga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .folderPublicKey("mdsjlkafnuwilaw3fadfsdfsdfsdfewa3323aaa3332jskdfsdfsdfsdfsdgsfd=3r232=2/32")
                .folderPrivateKeyEWA("mdsjlkegafwewfwefwfw43j4nfdsfsfdsafdfsafd23h4832jn[32=r2=3r232=2/32")
                .build();

        try {
            folderRepository.save(expected_folder);
            writeAuthRepository.save(expected_writeAuth_1);
            writeAuthRepository.save(expected_writeAuth_2);
            writeAuthRepository.save(expected_writeAuth_3);

        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        List<WriteAuth> writeAuths = writeAuthRepository.findByAccountCP(expected_writeAuth_1.getAccountCP());

        Assertions.assertEquals(1, writeAuths.size());
        Assertions.assertEquals(expected_writeAuth_1, writeAuths.get(0));

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

        WriteAuth expected_writeAuth= WriteAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .folderPublicKey("2h3ri233r93h932yqr9fhoy29")
                .folderPrivateKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        try {
            folderRepository.save(expected_folder);
            writeAuthRepository.save(expected_writeAuth);

        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(1,
                writeAuthRepository.findByAccountCP(expected_writeAuth.getAccountCP()).size());

        writeAuthRepository.deleteAll();

        Assertions.assertEquals(0,
                writeAuthRepository.findByAccountCP(expected_writeAuth.getAccountCP()).size());
    }

    @Test
    public void duplicate_exception_handle_test() {
        // given
        Folder folder = Folder.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .isTitleOpen(true)
                .title("hello")
                .symmetricKeyEWF("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        WriteAuth writeAuth = WriteAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .folderPublicKey("2h3ri233r93h932yqr9fhoy29")
                .folderPrivateKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                folderRepository.save(folder);
                writeAuthRepository.save(writeAuth);
            }
        });

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        writeAuthRepository.save(writeAuth);
                    }
                });
    }

    @Test
    public void non_folder_exception_handle_test() {
        // given
        WriteAuth writeAuth = WriteAuth.builder()
                .accountCP("fsanjflhasfdj=/fsaf/s=sdg=/ga=/fsaf")
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                .folderPublicKey("2h3ri233r93h932yqr9fhoy29")
                .folderPrivateKeyEWA("mdsjlkafnuwilaw3fah9829hudshfl7awlfhusia32blh3u48243j4nj423j4h32j4hj32h4k23h4832jn[32=r2=3r232=2/32")
                .build();

        // when
        Assertions.assertThrows(NoMatchParentRowException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        writeAuthRepository.save(writeAuth);
                    }
                });
    }

    @Test
    public void addWriteAuth_referential_throw_TEST() {
        // given

        // when
        // then
    }

    @Test
    public void addWriteAuth_entity_throw_TEST() {

    }

    @Test
    public void addWriteAuth_success_TEST() {

    }
}