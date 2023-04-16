package com.dong.demo.v1.service.file;

import com.dong.demo.v1.domain.file.File;
import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.NoMatchParentRowException;
import com.dong.demo.v1.exception.VerifyFailedException;
import com.dong.demo.v1.service.folder.FolderService;
import com.dong.demo.v1.util.*;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
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
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}