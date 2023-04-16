package com.dong.demo.v1.service.file;

import com.dong.demo.v1.domain.file.File;
import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.*;
import com.dong.demo.v1.util.*;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;

    @Transactional
    public String generateFile(String folderPublicKey, FilesGenerateRequestDto dto) {
        if (!RSAVerifier.verify(
                dto.getByteSign(),
                CipherUtil.getPublicKeyFromBase58String(folderPublicKey)
        )) {
            throw new VerifyFailedException();
        }
        else {
            String folderCP = KeyCompressor.compress(folderPublicKey);
            LocalDateTime now = LocalDateTime6Digit.now();

            File file = File.builder()
                    .folderCP(folderCP)
                    .fileId(UUIDGenerator.createUUIDWithoutHyphen())
                    .contentsEWS("")
                    .subheadEWS(dto.getSubhead())
                    .lastChangedDate(now)
                    .build();

            fileRepository.save(file);
            folderRepository.updateLastChangedDate(folderCP, now);

            return file.getFileId();
        }
    }

    // todo : 테스트 작성!
    @Transactional
    public String modifyFile(String folderPublicKey, String fileId, FilesModifyRequestDto dto) {
        if (!RSAVerifier.verify(
                dto.getByteSign(),
                CipherUtil.getPublicKeyFromBase58String(folderPublicKey)
        )) {
            throw new VerifyFailedException();
        }

        String folderCP = KeyCompressor.compress(folderPublicKey);

        if (!fileRepository.exist(folderCP, fileId)) {
           throw new FileDoesNotExistException();
        }

        LocalDateTime lastChangedDate = LocalDateTime6Digit.now();

        fileRepository.update(File.builder()
                .folderCP(folderCP)
                .fileId(fileId)
                .subheadEWS(dto.getSubhead())
                .contentsEWS(dto.getContents())
                .lastChangedDate(lastChangedDate)
                .build());

        folderRepository.updateLastChangedDate(folderCP, lastChangedDate);

        return fileId;
    }
}
