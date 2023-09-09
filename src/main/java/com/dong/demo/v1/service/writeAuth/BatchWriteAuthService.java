package com.dong.demo.v1.service.writeAuth;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.folderFindBatchBuilder.FolderFindBatchBuilder;
import com.dong.demo.v1.domain.writeAuth.WriteAuth;
import com.dong.demo.v1.domain.writeAuth.WriteAuthRepository;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsGetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BatchWriteAuthService implements WriteAuthService {

    private final ObjectProvider<FolderFindBatchBuilder> folderFindBatchBuilderObjectProvider;
    private final WriteAuthRepository writeAuthRepository;

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

    @Transactional(readOnly = true)
    public List<WriteAuthsGetResponseDto> getWriteAuthByAccountCP(String accountCP) {
        // prepare
        List<WriteAuth> writeAuths = writeAuthRepository.findByAccountCP(accountCP);
        FolderFindBatchBuilder builder = folderFindBatchBuilderObjectProvider.getObject();
        List<WriteAuthsGetResponseDto> dtoList = new ArrayList<>();

        // execute
        for (WriteAuth writeAuth : writeAuths) {
            builder.append(writeAuth.getFolderCP());
        }

        List<Folder> folders = builder.execute();

        // exception
        boolean isReversed = false;

        for (int i = 0; i < writeAuths.size(); i++) {
            if (!writeAuths.get(i).getFolderCP()
                    .equals(folders.get(i).getFolderCP())) {
                isReversed = true; break;
            }
        }

        if (!isReversed) {
            for (int i = 0; i < writeAuths.size(); i++) {
                dtoList.add(WriteAuthsGetResponseDto.builder()
                        .folderCP(writeAuths.get(i).getFolderCP())
                        .folderPublicKey(writeAuths.get(i).getFolderPublicKey())
                        .folderPrivateKeyEWA(writeAuths.get(i).getFolderPrivateKeyEWA())
                        .isTitleOpen(folders.get(i).getIsTitleOpen())
                        .title(folders.get(i).getTitle())
                        .symmetricKeyEWF(folders.get(i).getSymmetricKeyEWF())
                        .lastChangedDate(folders.get(i).getLastChangedDate())
                        .build());
            }
        }
        else {
            Map<String, WriteAuth> hashMap = new HashMap<>();

            for (WriteAuth w : writeAuths) {
                hashMap.put(w.getFolderCP(), w);
            }

            for (int i = 0; i < writeAuths.size(); i++) {
                Folder cur_folder = folders.get(i);
                WriteAuth cur_writeAuth = hashMap.get(cur_folder.getFolderCP());

                dtoList.add(WriteAuthsGetResponseDto.builder()
                        .folderCP(cur_writeAuth.getFolderCP())
                        .folderPublicKey(cur_writeAuth.getFolderPublicKey())
                        .folderPrivateKeyEWA(cur_writeAuth.getFolderPrivateKeyEWA())
                        .isTitleOpen(cur_folder.getIsTitleOpen())
                        .title(cur_folder.getTitle())
                        .symmetricKeyEWF(cur_folder.getSymmetricKeyEWF())
                        .lastChangedDate(cur_folder.getLastChangedDate())
                        .build());
            }
        }

        return dtoList;

    }
}
