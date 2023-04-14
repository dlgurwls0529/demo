package com.dong.demo.v1.service.file;

import com.dong.demo.v1.domain.file.File;
import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.service.folder.FolderService;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.util.UUIDGenerator;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("real-db")
class FileServiceTest {

    @Autowired
    private FileService fileService;

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
    // todo : 파일 생성 예외 롤백 테스트 + lastChanged 같이 갱신되는지 + save 는 잘 되었는지..
    public void generateFile_test() {
        // 1048 : null 필드 입력
        try {
            folderRepository.save(Folder.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .lastChangedDate(LocalDateTime6Digit.now())
                    .folderCP("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
                    .build());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

    }
}