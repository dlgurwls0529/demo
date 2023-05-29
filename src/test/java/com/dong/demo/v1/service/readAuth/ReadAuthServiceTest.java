package com.dong.demo.v1.service.readAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.service.folder.FolderService;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.ReadAuthsGetResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test-db")
class ReadAuthServiceTest {

    @Autowired
    private ReadAuthService readAuthService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private ReadAuthRepository readAuthRepository;

    @AfterEach
    @BeforeEach
    public void cleanup() {
        readAuthRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    public void getReadAuthByAccountCP_success_test() {
        // given
        Folder folderA = Folder.builder()
                .folderCP("folderCP_A")
                .title("title_A")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_A")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder folderB = Folder.builder()
                .folderCP("folderCP_B")
                .title("title_B")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_B")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        ReadAuth readAuth_A_TRUE = ReadAuth.builder()
                .accountCP("accountCP_TRUE")
                .folderCP(folderA.getFolderCP())
                .symmetricKeyEWA("sym_TRUE")
                .build();

        ReadAuth readAuth_B_TRUE = ReadAuth.builder()
                .accountCP("accountCP_TRUE")
                .folderCP(folderB.getFolderCP())
                .symmetricKeyEWA("sym_TRUE")
                .build();

        ReadAuth readAuth_A_FALSE = ReadAuth.builder()
                .accountCP("accountCP_FALSE")
                .folderCP(folderA.getFolderCP())
                .symmetricKeyEWA("sym_FALSE")
                .build();

        folderRepository.save(folderA);
        folderRepository.save(folderB);
        readAuthRepository.save(readAuth_A_FALSE);
        readAuthRepository.save(readAuth_B_TRUE);
        readAuthRepository.save(readAuth_A_TRUE);

        // when
        List<ReadAuthsGetResponseDto> dtoList
                 = readAuthService.getReadAuthByAccountCP("accountCP_TRUE");

        // then
        Assertions.assertEquals(2, dtoList.size());

        Assertions.assertEquals("sym_TRUE", dtoList.get(0).getSymmetricKeyEWA());
        Assertions.assertEquals("sym_TRUE", dtoList.get(1).getSymmetricKeyEWA());
        Assertions.assertNotEquals(dtoList.get(0).getFolderCP(), dtoList.get(1).getFolderCP());
    }

    @Test
    public void getReadAuthByAccountCP_no_auth_test() {
        // given

        // when
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                List<ReadAuthsGetResponseDto> dtoList = readAuthService.getReadAuthByAccountCP("test");

                Assertions.assertEquals(0, dtoList.size());
            }
        });
    }
}