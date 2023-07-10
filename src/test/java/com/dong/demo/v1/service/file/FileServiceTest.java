package com.dong.demo.v1.service.file;

import com.dong.demo.v1.domain.file.File;
import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.*;
import com.dong.demo.v1.service.folder.FolderService;
import com.dong.demo.v1.util.*;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesGetResponseDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test-db")
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FileRepository fileRepository;

    @AfterEach
    @BeforeEach
    public void cleanup() {
        fileRepository.deleteAll();
        folderRepository.deleteAll();
    }

    // 트랜잭션 메소드 자체에서 throw 할 때, 언체크드 예외이기만 하면 반드시 롤백된다.
    @Test
    public void generateFile_verify_success_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair = null;

        try {
            keyPair = CipherUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(keyPair);

        // 키들
        PublicKey publicKey = keyPair.getPublic();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        PrivateKey privateKey = keyPair.getPrivate();

        // input : base64 output : base58 compressed
        // 폴더 씨피 얻기
        String folderCP = KeyCompressor.compress(rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent());

        // dto 얻기
        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .symmetricKeyEWF("sym_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .build();

        // 넣기
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(RSAVerifier.SIGN_MESSAGE);

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .subhead("sub_TEST")
                .byteSign(sign)
                .build();

        // when
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                fileService.generateFile(folderPublicKey, filesGenerateRequestDto);
            }
        });

        // then
        List<File> file = fileRepository.findByFolderCP(folderCP);
        Assertions.assertEquals(1, file.size());
        Assertions.assertEquals(filesGenerateRequestDto.getSubhead(), file.get(0).getSubheadEWS());

        Folder folder = folderRepository.find(folderCP);
        Assertions.assertEquals(file.get(0).getLastChangedDate(), folder.getLastChangedDate());

    }

    @Test
    public void generateFile_verify_fail_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair = null;

        try {
            keyPair = CipherUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(keyPair);

        // 키들
        PublicKey publicKey = keyPair.getPublic();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        PrivateKey privateKey = keyPair.getPrivate();
        PrivateKey falsePrivateKey = CipherUtil.genRSAKeyPair().getPrivate();

        // input : base64 output : base58 compressed
        // 폴더 씨피 얻기
        String folderCP = KeyCompressor.compress(rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent());

        // dto 얻기
        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .symmetricKeyEWF("sym_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .build();

        // 넣기
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(falsePrivateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .subhead("sub_TEST")
                .byteSign(sign)
                .build();

        // when
        Assertions.assertThrows(VerifyFailedException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                fileService.generateFile(folderPublicKey, filesGenerateRequestDto);
            }
        });

        Assertions.assertEquals(0, fileRepository.findAllOrderByLastChangedDate().size());
    }

    @Test
    public void file_save_no_match_folder_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //given
        KeyPair keyPair = null;

        try {
            keyPair = CipherUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(keyPair);

        // 키들
        PublicKey publicKey = keyPair.getPublic();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        PrivateKey privateKey = keyPair.getPrivate();

        /*
        // input : base64 output : base58 compressed
        // 폴더 씨피 얻기
        String folderCP = KeyCompressor.compress(Base58.encode(publicKey.getEncoded()));

        // dto 얻기
        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .symmetricKeyEWF("sym_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .build();

        // 넣기
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);*/

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(RSAVerifier.SIGN_MESSAGE);

        byte[] sign = new byte[0];
        try {
            sign = signature.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .subhead("sub_TEST")
                .byteSign(sign)
                .build();

        // when
        Assertions.assertThrows(NoMatchParentRowException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                fileService.generateFile(folderPublicKey, filesGenerateRequestDto);
            }
        });

        Assertions.assertEquals(0, fileRepository.findAllOrderByLastChangedDate().size());

    }

    @Test
    public void file_modify_fail_no_file_test() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        // given
        KeyPair keyPair = null;

        try {
            keyPair = CipherUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(keyPair);

        PublicKey publicKey = keyPair.getPublic();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        PrivateKey privateKey = keyPair.getPrivate();

        // input : base64 output : base58 compressed
        // 폴더 씨피 얻기
        // String folderCP = KeyCompressor.compress(Base58.encode(publicKey.getEncoded()));

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(RSAVerifier.SIGN_MESSAGE);

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent();
        String fileId_false = "tefdsafsafsad";

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .byteSign(sign)
                .contents("contents_test")
                .subhead("sub_TEST")
                .build();

        //
        Assertions.assertThrows(NoSuchFileException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                fileService.modifyFile(folderPublicKey, fileId_false, filesModifyRequestDto);
            }
        });
    }

    @Test
    public void file_modify_verify_fail_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair = null;

        try {
            keyPair = CipherUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(keyPair);

        // 키들
        PublicKey publicKey = keyPair.getPublic();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        PrivateKey truePrivateKey = keyPair.getPrivate();
        PrivateKey falsePrivateKey = CipherUtil.genRSAKeyPair().getPrivate();

        // input : base64 output : base58 compressed
        // 폴더 씨피 얻기
        String folderCP = KeyCompressor.compress(rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent());

        // dto 얻기
        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .symmetricKeyEWF("sym_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .build();

        // 넣기
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature trueSignature = Signature.getInstance("SHA256withRSA");
        trueSignature.initSign(truePrivateKey);
        trueSignature.update(RSAVerifier.SIGN_MESSAGE);

        byte[] trueSign = new byte[0];
        try {
            trueSign = trueSignature.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        Signature falseSignature = Signature.getInstance("SHA256withRSA");
        falseSignature.initSign(falsePrivateKey);
        falseSignature.update(publicKey.getEncoded());

        byte[] falseSign = new byte[0];
        try {
            falseSign = falseSignature.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .subhead("sub_TEST")
                .byteSign(trueSign)
                .build();

        String fileId = fileService.generateFile(folderPublicKey, filesGenerateRequestDto);

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .subhead("sub_TEST")
                .contents("con_TEST")
                .byteSign(falseSign)
                .build();

        // when
        Assertions.assertThrows(VerifyFailedException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                fileService.modifyFile(folderPublicKey, fileId, filesModifyRequestDto);
            }
        });

        Assertions.assertEquals(filesGenerateRequestDto.getSubhead(), fileRepository.findAllOrderByLastChangedDate().get(0).getSubheadEWS());
        Assertions.assertEquals("", fileRepository.findAllOrderByLastChangedDate().get(0).getContentsEWS());
        Assertions.assertEquals(fileRepository.findByFolderCP(folderCP).get(0).getLastChangedDate(), folderRepository.find(folderCP).getLastChangedDate());
    }

    @Test
    public void file_modify_success_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair = null;

        try {
            keyPair = CipherUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(keyPair);

        // 키들
        PublicKey publicKey = keyPair.getPublic();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        PrivateKey privateKey = keyPair.getPrivate();

        // input : base64 output : base58 compressed
        // 폴더 씨피 얻기
        String folderCP = KeyCompressor.compress(rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent());

        // dto 얻기
        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .symmetricKeyEWF("sym_TEST")
                .isTitleOpen(true)
                .title("title_TEST")
                .build();

        // 넣기
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(RSAVerifier.SIGN_MESSAGE);

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .subhead("sub_TEST")
                .byteSign(sign)
                .build();

        String fileId = fileService.generateFile(folderPublicKey, filesGenerateRequestDto);

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .subhead("sub_modify")
                .contents("con_modify")
                .byteSign(sign)
                .build();

        // when
        fileService.modifyFile(folderPublicKey, fileId, filesModifyRequestDto);

        // then
        List<File> file = fileRepository.findByFolderCP(folderCP);
        Assertions.assertEquals(1, file.size());
        Assertions.assertEquals(filesModifyRequestDto.getSubhead(), file.get(0).getSubheadEWS());
        Assertions.assertEquals(filesModifyRequestDto.getContents(), file.get(0).getContentsEWS());

        Folder folder = folderRepository.find(folderCP);
        Assertions.assertEquals(file.get(0).getLastChangedDate(), folder.getLastChangedDate());

    }

    // file 없으면 예외 안터지고 그냥 빈 리스트 리턴
    @Test
    public void getFileByFolderCP_no_file_handling_test() {
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                List<FilesGetResponseDto> dtoList = fileService.getFileByFolderCP("a");
                Assertions.assertEquals(0, dtoList.size());
            }
        });
    }
    
    @Test
    public void getFileByFolderCP_success_test() throws InterruptedException {
        // given
        Folder folderA = Folder.builder()
                .folderCP("folderCP_TEST_A")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        File file1_folderA = File.builder()
                .folderCP(folderA.getFolderCP())
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_1A")
                .contentsEWS("con_TEST_1A")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Thread.sleep(200);

        File file2_folderA = File.builder()
                .folderCP(folderA.getFolderCP())
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_2A")
                .contentsEWS("con_TEST_2A")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Folder folderB = Folder.builder()
                .folderCP("folderCP_TEST_B")
                .isTitleOpen(true)
                .title("title_TEST")
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        Thread.sleep(200);

        File file1_folderB = File.builder()
                .folderCP(folderB.getFolderCP())
                .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                .subheadEWS("sub_TEST_1B")
                .contentsEWS("con_TEST_1B")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        folderRepository.save(folderA);
        folderRepository.save(folderB);
        fileRepository.save(file1_folderA);
        fileRepository.save(file2_folderA);
        fileRepository.save(file1_folderB);

        // when
        List<FilesGetResponseDto> dtoListA = fileService.getFileByFolderCP(folderA.getFolderCP());
        List<FilesGetResponseDto> dtoListB = fileService.getFileByFolderCP(folderB.getFolderCP());
        
        // then
        // !!! 폴더와 파일 간 lastChangedDate 에서 저장소에 직접 접근했기에, 일관성이 깨지나, 테스트라서 그냥 허용한다. !!!
        Assertions.assertEquals(2, dtoListA.size());
        Assertions.assertTrue(dtoListA.get(0).getLastChangedDate().isAfter(dtoListA.get(1).getLastChangedDate()));
        Assertions.assertEquals(folderA.getFolderCP(), dtoListA.get(0).getFolderCP());
        Assertions.assertEquals(folderA.getFolderCP(), dtoListA.get(1).getFolderCP());

        Assertions.assertEquals(1, dtoListB.size());
        Assertions.assertEquals(folderB.getFolderCP(), dtoListB.get(0).getFolderCP());
    }
    
    @Test
    public void getContentsByFileIdAndFolderCP_null_test() {
        Assertions.assertThrows(NoSuchFileException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                String res = fileService.getContentsByFileIdAndFolderCP("test", "test");
            }
        });
    }

    @Test
    public void getContentsByFileIdAndFolderCP_success_test() {
        // given
        folderRepository.save(Folder.builder()
                .folderCP("folderCP_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        String uuid = UUIDGenerator.createUUIDWithoutHyphen();

        fileRepository.save(File.builder()
                .folderCP("folderCP_TEST")
                .fileId(uuid)
                .contentsEWS("content_TEST")
                .subheadEWS("subhead_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        // when
        String content = fileService.getContentsByFileIdAndFolderCP(
                "folderCP_TEST", uuid
        );

        Assertions.assertNotNull(content);
        Assertions.assertEquals("content_TEST", content);
    }
}