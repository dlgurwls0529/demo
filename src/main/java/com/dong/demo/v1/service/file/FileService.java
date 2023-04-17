package com.dong.demo.v1.service.file;

import com.dong.demo.v1.domain.file.File;
import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.exception.*;
import com.dong.demo.v1.util.*;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesGetResponseDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    @Transactional
    public String modifyFile(String folderPublicKey, String fileId, FilesModifyRequestDto dto) {
        if (!RSAVerifier.verify(
                dto.getByteSign(),
                CipherUtil.getPublicKeyFromBase58String(folderPublicKey)
        )) {
            throw new VerifyFailedException();
        }
        else {
            String folderCP = KeyCompressor.compress(folderPublicKey);

            if (!fileRepository.exist(folderCP, fileId)) {
                throw new NoSuchFileException();
            }
            else {
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
    }

    @Transactional(readOnly = true)
    public List<FilesGetResponseDto> getFileByFolderCP(String folderCP) {
        List<FilesGetResponseDto> res = new ArrayList<>();
        List<File> files = fileRepository.findByFolderCP(folderCP);

        files.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return -o1.getLastChangedDate().compareTo(o2.getLastChangedDate());
            }
        });

        for (File file : files) {
            res.add(FilesGetResponseDto.builder()
                    .folderCP(file.getFolderCP())
                    .fileId(file.getFileId())
                    .subheadEWS(file.getSubheadEWS())
                    .lastChangedDate(file.getLastChangedDate())
                    .build());
        }

        return res;
    }
}
