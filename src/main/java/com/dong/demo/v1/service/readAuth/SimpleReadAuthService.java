package com.dong.demo.v1.service.readAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.folder.folderFindBatchBuilder.FolderFindBatchBuilder;
import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.web.dto.ReadAuthsGetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SimpleReadAuthService implements ReadAuthService {

    private final ReadAuthRepository readAuthRepository;
    private final FolderRepository folderRepository;

    @Transactional(readOnly = true)
    public List<ReadAuthsGetResponseDto> getReadAuthByAccountCP(String accountCP) {
        List<ReadAuth> readAuths = readAuthRepository.findByAccountCP(accountCP);
        List<ReadAuthsGetResponseDto> dtoList = new ArrayList<>();

        for (ReadAuth readAuth : readAuths) {
            Folder targetFolder = folderRepository.find(readAuth.getFolderCP());
            dtoList.add(ReadAuthsGetResponseDto.builder()
                    .folderCP(targetFolder.getFolderCP())
                    .isTitleOpen(targetFolder.getIsTitleOpen())
                    .title(targetFolder.getTitle())
                    .symmetricKeyEWA(readAuth.getSymmetricKeyEWA())
                    .lastChangedDate(targetFolder.getLastChangedDate())
                    .build());
        }

        return dtoList;
    }
}
