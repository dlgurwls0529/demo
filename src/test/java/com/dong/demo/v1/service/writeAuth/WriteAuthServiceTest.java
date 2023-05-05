package com.dong.demo.v1.service.writeAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.domain.writeAuth.WriteAuth;
import com.dong.demo.v1.domain.writeAuth.WriteAuthRepository;
import com.dong.demo.v1.service.readAuth.ReadAuthService;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.ReadAuthsGetResponseDto;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsGetResponseDto;
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
@ActiveProfiles("real-db")
class WriteAuthServiceTest {

    @Autowired
    private WriteAuthService writeAuthService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private WriteAuthRepository writeAuthRepository;

    @AfterEach
    @BeforeEach
    public void cleanup() {
        writeAuthRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    public void getWriteAuthByAccountCP_success_test() {
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

        WriteAuth writeAuth_A_TRUE = WriteAuth.builder()
                .accountCP("accountCP_TRUE")
                .folderCP(folderA.getFolderCP())
                .folderPublicKey("folderPublicKey_TRUE")
                .folderPrivateKeyEWA("EWA_TRUE")
                .build();

        WriteAuth writeAuth_B_TRUE = WriteAuth.builder()
                .accountCP("accountCP_TRUE")
                .folderCP(folderB.getFolderCP())
                .folderPublicKey("folderPublicKey_TRUE")
                .folderPrivateKeyEWA("EWA_TRUE")
                .build();

        WriteAuth writeAuth_A_FALSE = WriteAuth.builder()
                .accountCP("accountCP_FALSE")
                .folderCP(folderA.getFolderCP())
                .folderPublicKey("folderPublicKey_FALSE")
                .folderPrivateKeyEWA("EWA_FALSE")
                .build();

        folderRepository.save(folderA);
        folderRepository.save(folderB);
        writeAuthRepository.save(writeAuth_A_FALSE);
        writeAuthRepository.save(writeAuth_B_TRUE);
        writeAuthRepository.save(writeAuth_A_TRUE);

        // when
        List<WriteAuthsGetResponseDto> dtoList
                = writeAuthService.getWriteAuthByAccountCP("accountCP_TRUE");

        // then
        Assertions.assertEquals(2, dtoList.size());

        Assertions.assertEquals("EWA_TRUE", dtoList.get(0).getFolderPrivateKeyEWA());
        Assertions.assertEquals("EWA_TRUE", dtoList.get(1).getFolderPrivateKeyEWA());
        Assertions.assertNotEquals(dtoList.get(0).getFolderCP(), dtoList.get(1).getFolderCP());
    }

    @Test
    public void getWriteAuthByAccountCP_no_auth_test() {
        // given

        // when
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                List<WriteAuthsGetResponseDto> dtoList = writeAuthService.getWriteAuthByAccountCP("test");

                Assertions.assertEquals(0, dtoList.size());
            }
        });
    }


}