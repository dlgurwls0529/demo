package com.dong.demo.v1.service.file;

import com.dong.demo.v1.domain.file.File;
import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.*;
import com.dong.demo.v1.service.folder.FolderService;
import com.dong.demo.v1.util.*;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.*;
import java.util.List;

@SpringBootTest
@ActiveProfiles("real-db")
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
        PrivateKey privateKey = keyPair.getPrivate();

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
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = Base58.encode(publicKey.getEncoded());

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
        PrivateKey privateKey = keyPair.getPrivate();
        PrivateKey falsePrivateKey = CipherUtil.genRSAKeyPair().getPrivate();

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
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(falsePrivateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = Base58.encode(publicKey.getEncoded());

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
        signature.update(publicKey.getEncoded());

        byte[] sign = new byte[0];
        try {
            sign = signature.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = Base58.encode(publicKey.getEncoded());

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

    }

    // todo : 해당 파일 없거나, verify 실패하거나
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
        PrivateKey privateKey = keyPair.getPrivate();

        // input : base64 output : base58 compressed
        // 폴더 씨피 얻기
        // String folderCP = KeyCompressor.compress(Base58.encode(publicKey.getEncoded()));

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = Base58.encode(publicKey.getEncoded());
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
        PrivateKey truePrivateKey = keyPair.getPrivate();
        PrivateKey falsePrivateKey = CipherUtil.genRSAKeyPair().getPrivate();

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
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature trueSignature = Signature.getInstance("SHA256withRSA");
        trueSignature.initSign(truePrivateKey);
        trueSignature.update(publicKey.getEncoded());

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
        String folderPublicKey = Base58.encode(publicKey.getEncoded());

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .subhead("sub_TEST")
                .byteSign(trueSign)
                .build();

        fileService.generateFile(folderPublicKey, filesGenerateRequestDto);

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .subhead("sub_TEST")
                .contents("con_TEST")
                .byteSign(falseSign)
                .build();

        String fileId = fileService.generateFile(folderPublicKey, filesGenerateRequestDto);

        // when
        Assertions.assertThrows(VerifyFailedException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                fileService.modifyFile(folderPublicKey, fileId, filesModifyRequestDto);
            }
        });
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
        PrivateKey privateKey = keyPair.getPrivate();

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
        folderService.generateFolder(folderCP, foldersGenerateRequestDto);

        // verify sign 준비
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());

        byte[] sign = signature.sign();

        // sign 이랑 해서 file 생성 준비
        String folderPublicKey = Base58.encode(publicKey.getEncoded());

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
}