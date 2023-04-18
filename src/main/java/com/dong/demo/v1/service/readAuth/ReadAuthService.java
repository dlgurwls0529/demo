package com.dong.demo.v1.service.readAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.web.dto.ReadAuthsGetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReadAuthService {

    private final FolderRepository folderRepository;
    private final ReadAuthRepository readAuthRepository;

    @Transactional(readOnly = true)
    // todo : 이거 테스트
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
