package com.dong.demo.v1.service.subDemand;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.domain.subDemand.SubDemand;
import com.dong.demo.v1.domain.subDemand.SubDemandRepository;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.NoMatchParentRowException;
import com.dong.demo.v1.exception.NoSuchSubscribeDemandException;
import com.dong.demo.v1.exception.VerifyFailedException;
import com.dong.demo.v1.service.readAuth.ReadAuthService;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.KeyGenerator;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test-db")
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
        String encodedPublicKey =
                ((RSAPublicKey)publicKey).getModulus() + "and" + ((RSAPublicKey)publicKey).getPublicExponent();
        PrivateKey privateKey = keyPair.getPrivate();

        KeyPair keyPair_false = CipherUtil.genRSAKeyPair();
        PublicKey publicKey_false = keyPair_false.getPublic();
        String encodedPublicKey_false =
                ((RSAPublicKey)publicKey_false).getModulus() + "and" + ((RSAPublicKey)publicKey_false).getPublicExponent();
        PrivateKey privateKey_false = keyPair_false.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(encodedPublicKey_false)
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

        Assertions.assertEquals(0, subDemandRepository.findAccountPublicKeyByFolderCP("folderCP_TEST").size());
    }

    @Test
    public void addSubscribeDemand_success_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        // verify sign 준비
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        String encodedPublicKey =
                ((RSAPublicKey)publicKey).getModulus() + "and" + ((RSAPublicKey)publicKey).getPublicExponent();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(encodedPublicKey)
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

    @Test
    public void addSubscribeDemand_ref_violation_fail_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        // verify sign 준비
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        String encodedPublicKey =
                ((RSAPublicKey)publicKey).getModulus() + "and" + ((RSAPublicKey)publicKey).getPublicExponent();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(encodedPublicKey)
                .byteSign(sign)
                .folderCP("folderCP_TEST")
                .build();

        // when, mariadb 로 환경 바뀌면 돌아간다, h2랑 무결성 에외 달라서 그럼

        Assertions.assertThrows(NoMatchParentRowException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.addSubscribeDemand(dto);
            }
        });

        // then
        Assertions.assertEquals(0, subDemandRepository.findAccountPublicKeyByFolderCP("folderCP_TEST").size());
    }

    @Test
    public void addSubscribeDemand_ent_violation_fail_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        // verify sign 준비
        KeyPair keyPair = CipherUtil.genRSAKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        String encodedPublicKey = rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent();

        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(encodedPublicKey)
                .byteSign(sign)
                .folderCP("folderCP_TEST")
                .build();

        folderRepository.save(Folder.builder()
                .folderCP("folderCP_TEST")
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        subDemandService.addSubscribeDemand(dto);

        // when, mariadb 로 환경 바뀌면 돌아간다, h2랑 무결성 에외 달라서 그럼

        Assertions.assertThrows(DuplicatePrimaryKeyException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.addSubscribeDemand(dto);
            }
        });

        // then
        Assertions.assertEquals(1, subDemandRepository.findAccountPublicKeyByFolderCP("folderCP_TEST").size());
    }

    @Test
    public void allowSubscribe_success_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey folderPublicKey = folderKeyPair.getPublic();
        RSAPublicKey rsaFolderPublicKey = (RSAPublicKey) folderPublicKey;
        String encodedFolderPublicKey = rsaFolderPublicKey.getModulus() + "and" + rsaFolderPublicKey.getPublicExponent();
        PrivateKey folderPrivateKey = folderKeyPair.getPrivate();

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey accountPublicKey = accountKeyPair.getPublic();
        RSAPublicKey rsaAccountPublicKey = (RSAPublicKey) accountPublicKey;
        String encodedAccountPublicKey = rsaAccountPublicKey.getModulus() + "and" + rsaAccountPublicKey.getPublicExponent();
        PrivateKey accountPrivateKey = accountKeyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderPrivateKey);
        signature.update(folderPublicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                .folderPublicKey(encodedFolderPublicKey)
                .byteSign(sign)
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .symmetricKeyEWA("EWA_TEST")
                .build();

        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        SubDemand expectedSub = SubDemand.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .accountPublicKey(encodedAccountPublicKey)
                .build();

        subDemandRepository.save(expectedSub);

        Assertions.assertTrue(subDemandRepository.exist(expectedSub.getFolderCP(), expectedSub.getAccountCP()));

        // when
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.allowSubscribe(dto);
            }
        });

        // then
        Assertions.assertFalse(subDemandRepository.exist(expectedSub.getFolderCP(), expectedSub.getAccountCP()));
        Assertions.assertEquals(1, readAuthRepository.findByAccountCP(expectedSub.getAccountCP()).size());
        Assertions.assertEquals(
                    ReadAuth.builder()
                        .accountCP(expectedSub.getAccountCP())
                        .folderCP(expectedSub.getFolderCP())
                        .symmetricKeyEWA("EWA_TEST")
                        .build(),
                    readAuthRepository.findByAccountCP(expectedSub.getAccountCP()).get(0)
        );
    }

    // fail 뜨면 트랜잭션 반영 x
    @Test
    public void allowSubscribe_verify_fail_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair_true = CipherUtil.genRSAKeyPair();
        PublicKey publicKey_true = keyPair_true.getPublic();
        String publicKey_true_encoded =
                ((RSAPublicKey)publicKey_true).getModulus() + "and" + ((RSAPublicKey)publicKey_true).getPublicExponent();

        KeyPair keyPair_false = CipherUtil.genRSAKeyPair();
        PrivateKey privateKey_false = keyPair_false.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey_false);
        signature.update(publicKey_true.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                .folderPublicKey(publicKey_true_encoded)
                .byteSign(sign)
                .accountCP("accountCP_TEST")
                .symmetricKeyEWA("EWA_TEST")
                .build();

        // when
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.allowSubscribe(dto);
            }
        };

        // then
        Assertions.assertThrows(VerifyFailedException.class, executable);
    }

    // verify 통과해도 subDemand 없으면 뜨면 트랜잭션 반영 x
    @Test
    public void allowSubscribe_subscribe_if_not_exist_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey folderPublicKey = folderKeyPair.getPublic();
        RSAPublicKey rsaFolderPublicKey = (RSAPublicKey) folderPublicKey;
        String encodedFolderPublicKey = rsaFolderPublicKey.getModulus() + "and" + rsaFolderPublicKey.getPublicExponent();
        PrivateKey folderPrivateKey = folderKeyPair.getPrivate();

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey accountPublicKey = accountKeyPair.getPublic();
        RSAPublicKey rsaAccountPublicKey = (RSAPublicKey) accountPublicKey;
        String encodedAccountPublicKey = rsaAccountPublicKey.getModulus() + "and" + rsaAccountPublicKey.getPublicExponent();
        PrivateKey accountPrivateKey = accountKeyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderPrivateKey);
        signature.update(folderPublicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                .folderPublicKey(encodedFolderPublicKey)
                .byteSign(sign)
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .symmetricKeyEWA("EWA_TEST")
                .build();

        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        SubDemand expectedSub = SubDemand.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .accountPublicKey(encodedAccountPublicKey)
                .build();

        Assertions.assertFalse(subDemandRepository.exist(expectedSub.getFolderCP(), expectedSub.getAccountCP()));

        // when
        Assertions.assertThrows(NoSuchSubscribeDemandException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.allowSubscribe(dto);
            }
        });

        // then
        Assertions.assertFalse(subDemandRepository.exist(expectedSub.getFolderCP(), expectedSub.getAccountCP()));
        Assertions.assertEquals(0, readAuthRepository.findByAccountCP(dto.getAccountCP()).size());
    }

    // 밑에 두 개는 안해도 될 것 같지만
    // 병행수행 될 경우 위반 될 가능성이 있어서 테스트를 한다.
    // 중간에 예외 터졌을 때 롤백 되는지 확인한다.
    // crash 를 중간에 내서 DataAccessException 에 대해서도 롤백이 되는지 확인해보면 좋을 거 같기도 하고

    @Test
    public void allowSubscribe_read_auth_ent_violation_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey folderPublicKey = folderKeyPair.getPublic();
        RSAPublicKey rsaFolderPublicKey = (RSAPublicKey) folderPublicKey;
        String encodedFolderPublicKey = rsaFolderPublicKey.getModulus() + "and" + rsaFolderPublicKey.getPublicExponent();
        PrivateKey folderPrivateKey = folderKeyPair.getPrivate();

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey accountPublicKey = accountKeyPair.getPublic();
        RSAPublicKey rsaAccountPublicKey = (RSAPublicKey) accountPublicKey;
        String encodedAccountPublicKey = rsaAccountPublicKey.getModulus() + "and" + rsaAccountPublicKey.getPublicExponent();
        PrivateKey accountPrivateKey = accountKeyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderPrivateKey);
        signature.update(folderPublicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                .folderPublicKey(encodedFolderPublicKey)
                .byteSign(sign)
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .symmetricKeyEWA("EWA_TEST")
                .build();

        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        SubDemand expectedSub = SubDemand.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .accountPublicKey(encodedAccountPublicKey)
                .build();

        subDemandRepository.save(expectedSub);
        readAuthRepository.save(ReadAuth.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .accountCP(dto.getAccountCP())
                .symmetricKeyEWA(dto.getSymmetricKeyEWA())
                .build());

        // when
        Assertions.assertThrows(DuplicatePrimaryKeyException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                subDemandService.allowSubscribe(dto);
            }
        });

        // then, 삭제가 롤백되어 subDemand 에 하나, readAuth 에도 하나가 있어야 한다.
        // 롤백 안되면 subDemand 에 아무것도 없고, readAuth 에 하나가 있을 것
        // 물론 이렇게 롤백 되면 안된다. 다시 알아보자.
        Assertions.assertEquals(1, subDemandRepository.findAccountPublicKeyByFolderCP(expectedSub.getFolderCP()).size());
        Assertions.assertEquals(1, readAuthRepository.findByAccountCP(expectedSub.getAccountCP()).size());
        Assertions.assertEquals(
                ReadAuth.builder()
                        .accountCP(expectedSub.getAccountCP())
                        .folderCP(expectedSub.getFolderCP())
                        .symmetricKeyEWA("EWA_TEST")
                        .build(),
                readAuthRepository.findByAccountCP(expectedSub.getAccountCP()).get(0)
        );
    }

    // 생각해보니까 이거는 일어날 수가 없다. 중간에 folder 삭제해야 하는데, 참조 무결성 때문에 불가능하다. 테스트 할 수가 없다.
    // 멀티스레딩 써도 동시성 제어 때문에 안될 듯.
    public void allowSubscribe_read_auth_ref_violation_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

    }

    @RepeatedTest(10)
    public void allowSubscribe_concurrent_test() throws InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey folderPublicKey = folderKeyPair.getPublic();
        RSAPublicKey rsaFolderPublicKey = (RSAPublicKey) folderPublicKey;
        String encodedFolderPublicKey = rsaFolderPublicKey.getModulus() + "and" + rsaFolderPublicKey.getPublicExponent();
        PrivateKey folderPrivateKey = folderKeyPair.getPrivate();

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        PublicKey accountPublicKey = accountKeyPair.getPublic();
        RSAPublicKey rsaAccountPublicKey = (RSAPublicKey) accountPublicKey;
        String encodedAccountPublicKey = rsaAccountPublicKey.getModulus() + "and" + rsaAccountPublicKey.getPublicExponent();
        PrivateKey accountPrivateKey = accountKeyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderPrivateKey);
        signature.update(folderPublicKey.getEncoded());

        byte[] sign = signature.sign();

        SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                .folderPublicKey(encodedFolderPublicKey)
                .byteSign(sign)
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .symmetricKeyEWA("EWA_TEST")
                .build();

        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        SubDemand expectedSub = SubDemand.builder()
                .folderCP(KeyCompressor.compress(dto.getFolderPublicKey()))
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .accountPublicKey(encodedAccountPublicKey)
                .build();

        subDemandRepository.save(expectedSub);

        Assertions.assertTrue(subDemandRepository.exist(expectedSub.getFolderCP(), expectedSub.getAccountCP()));

        // multithreading, concurrent environment.
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // for waiting
        CountDownLatch latch = new CountDownLatch(2);

        // when
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    subDemandService.allowSubscribe(dto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    latch.countDown();
                }
            }
        });

        Random random = new Random();
        int minDelay = 0; // 최소 딜레이(ms)
        int maxDelay = 100; // 최대 딜레이(ms)

        try {
            int delay = random.nextInt(maxDelay - minDelay + 1) + minDelay;
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    subDemandService.allowSubscribe(dto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    latch.countDown();
                }
            }
        });

        latch.await();

        // then
        Assertions.assertFalse(subDemandRepository.exist(expectedSub.getFolderCP(), expectedSub.getAccountCP()));
        Assertions.assertEquals(1, readAuthRepository.findByAccountCP(expectedSub.getAccountCP()).size());
        Assertions.assertEquals(
                ReadAuth.builder()
                        .accountCP(expectedSub.getAccountCP())
                        .folderCP(expectedSub.getFolderCP())
                        .symmetricKeyEWA("EWA_TEST")
                        .build(),
                readAuthRepository.findByAccountCP(expectedSub.getAccountCP()).get(0)
        );
    }
}