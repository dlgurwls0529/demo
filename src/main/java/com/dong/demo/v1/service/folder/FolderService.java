package com.dong.demo.v1.service.folder;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class FolderService {

    private final FolderRepository folderRepository;

    @Transactional
    public String generateFolder(String folderCP, FoldersGenerateRequestDto dto) throws SQLIntegrityConstraintViolationException {
        Folder folder = Folder.builder()
                .folderCP(folderCP)
                .isTitleOpen(dto.getIsTitleOpen())
                .title(dto.getTitle())
                .symmetricKeyEWF(dto.getSymmetricKeyEWF())
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        folderRepository.save(folder);

        return folderCP;
    }
}
