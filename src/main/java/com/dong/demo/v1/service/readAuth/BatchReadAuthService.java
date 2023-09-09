package com.dong.demo.v1.service.readAuth;

import com.dong.demo.v1.domain.folder.Folder;
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
@Service
public class BatchReadAuthService implements ReadAuthService {

    private final ObjectProvider<FolderFindBatchBuilder> folderFindBatchBuilderObjectProvider;
    private final ReadAuthRepository readAuthRepository;

    @Transactional(readOnly = true)
    public List<ReadAuthsGetResponseDto> getReadAuthByAccountCP(String accountCP) {
        // prepare
        List<ReadAuth> readAuths = readAuthRepository.findByAccountCP(accountCP);
        FolderFindBatchBuilder builder = folderFindBatchBuilderObjectProvider.getObject();
        List<ReadAuthsGetResponseDto> dtoList = new ArrayList<>();

        // execute
        for (ReadAuth readAuth : readAuths) {
            builder.append(readAuth.getFolderCP());
        }

        List<Folder> folders = builder.execute();

        // exception
        boolean isReversed = false;

        for (int i = 0; i < readAuths.size(); i++) {
            if (!readAuths.get(i).getFolderCP()
                    .equals(folders.get(i).getFolderCP())) {
                isReversed = true; break;
            }
        }

        if (!isReversed) {
            for (int i = 0; i < readAuths.size(); i++) {
                dtoList.add(ReadAuthsGetResponseDto.builder()
                                .folderCP(folders.get(i).getFolderCP())
                                .isTitleOpen(folders.get(i).getIsTitleOpen())
                                .title(folders.get(i).getTitle())
                                .symmetricKeyEWA(readAuths.get(i).getSymmetricKeyEWA())
                                .lastChangedDate(folders.get(i).getLastChangedDate())
                                .build());
            }
        }
        else {
            Map<String, ReadAuth> hashMap = new HashMap<>();

            for (ReadAuth r : readAuths) {
                hashMap.put(r.getFolderCP(), r);
            }

            for (int i = 0; i < readAuths.size(); i++) {
                Folder cur_folder = folders.get(i);
                ReadAuth cur_readAuth = hashMap.get(cur_folder.getFolderCP());

                dtoList.add(ReadAuthsGetResponseDto.builder()
                                .folderCP(cur_folder.getFolderCP())
                                .isTitleOpen(cur_folder.getIsTitleOpen())
                                .title(cur_folder.getTitle())
                                .symmetricKeyEWA(cur_readAuth.getSymmetricKeyEWA())
                                .lastChangedDate(cur_folder.getLastChangedDate())
                                .build());
            }
        }

        return dtoList;

    }
}
