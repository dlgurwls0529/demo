package com.dong.demo.v1.service.writeAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.domain.readAuth.ReadAuthRepository;
import com.dong.demo.v1.domain.writeAuth.WriteAuth;
import com.dong.demo.v1.domain.writeAuth.WriteAuthRepository;
import com.dong.demo.v1.web.dto.ReadAuthsGetResponseDto;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsGetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WriteAuthService {
    private final FolderRepository folderRepository;
    private final WriteAuthRepository writeAuthRepository;

    @Transactional(readOnly = true)
    public List<WriteAuthsGetResponseDto> getWriteAuthByAccountCP(String accountCP) {
        List<WriteAuth> writeAuths = writeAuthRepository.findByAccountCP(accountCP);
        List<WriteAuthsGetResponseDto> dtoList = new ArrayList<>();

        for (WriteAuth writeAuth : writeAuths) {
            Folder targetFolder = folderRepository.find(writeAuth.getFolderCP());
            dtoList.add(WriteAuthsGetResponseDto.builder()
                    .folderCP(writeAuth.getFolderCP())
                    .folderPublicKey(writeAuth.getFolderPublicKey())
                    .folderPrivateKeyEWA(writeAuth.getFolderPrivateKeyEWA())
                    .isTitleOpen(targetFolder.getIsTitleOpen())
                    .title(targetFolder.getTitle())
                    .symmetricKeyEWF(targetFolder.getSymmetricKeyEWF())
                    .lastChangedDate(targetFolder.getLastChangedDate())
                    .build());
        }

        return dtoList;
    }

    // purpose : just note write auth information for user
    // this information can be invalid
    @Transactional
    public void addWriteAuthority(WriteAuthsAddRequestDto writeAuthsAddRequestDto) {
        writeAuthRepository.save(WriteAuth.builder()
                .accountCP(writeAuthsAddRequestDto.getAccountCP())
                .folderCP(writeAuthsAddRequestDto.getFolderCP())
                .folderPublicKey(writeAuthsAddRequestDto.getFolderPublicKey())
                .folderPrivateKeyEWA(writeAuthsAddRequestDto.getFolderPrivateKeyEWA())
                .build());
    }
}
