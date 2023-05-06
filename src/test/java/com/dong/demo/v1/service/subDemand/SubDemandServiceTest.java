package com.dong.demo.v1.service.subDemand;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.domain.subDemand.SubDemand;
import com.dong.demo.v1.domain.subDemand.SubDemandRepository;
import com.dong.demo.v1.exception.VerifyFailedException;
import com.dong.demo.v1.service.readAuth.ReadAuthService;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.KeyGenerator;
import java.security.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("real-db")
class SubDemandServiceTest {

    @Autowired
    private SubDemandService subDemandService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private ReadAuthRepository readAuthRepository;

    @Autowired
    private SubDemandRepository subDemandRepository;

    @AfterEach
    @BeforeEach
    public void cleanUp() {
        readAuthRepository.deleteAll();
        subDemandRepository.deleteAll();
        folderRepository.deleteAll();
    }

    @Test
    public void getSubscribeDemand_test() {
        // given
        Folder folder = Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        SubDemand subDemand = SubDemand.builder()
                .folderCP("folderCP_TEST")
                .accountCP("accountCP_TEST")
                .accountPublicKey("accountPK_TEST")
                .build();

        folderRepository.save(folder);
        subDemandRepository.save(subDemand);

        // when
        List<String> subDemands = subDemandService.getSubscribeDemand(folder.getFolderCP());

        // then
        Assertions.assertEquals(1, subDemands.size());
        Assertions.assertEquals("accountPK_TEST", subDemands.get(0));
    }

    @Test
    public void addSubscribeDemand_verify_fail_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        KeyPair keyPair_false = CipherUtil.genRSAKeyPair();
        PublicKey publicKey_false = keyPair_false.getPublic();
        PrivateKey privateKey_false = keyPair_false.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(Base58.encode(publicKey_false.getEncoded()))
                .byteSign(sign)
                .folderCP("folderCP_TEST")
                .build();

        // 폴더 insert, referential integrity 를 위함.
        folderRepository.save(Folder.builder()
                .folderCP("folderCP_TEST")
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        // when
        Assertions.assertThrows(VerifyFailedException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.addSubscribeDemand(dto);
            }
        });
    }

    @Test
    public void addSubscribeDemand_success_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        // verify sign 준비
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(Base58.encode(publicKey.getEncoded()))
                .byteSign(sign)
                .folderCP("folderCP_TEST")
                .build();

        // 폴더 insert, referential integrity 를 위함.
        folderRepository.save(Folder.builder()
                .folderCP("folderCP_TEST")
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        // when
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.addSubscribeDemand(dto);
            }
        });

        // then
        String actual = subDemandRepository.findAccountPublicKeyByFolderCP("folderCP_TEST").get(0);
        Assertions.assertEquals(actual, dto.getAccountPublicKey());
    }
}