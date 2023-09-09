package com.dong.demo.v1.service.folder;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearch;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearchRepository;
import com.dong.demo.v1.service.folder.search.SearchEngine;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
@Qualifier("distribute")
// todo : 여러 테스트가 SimpleFolderService 에 의존하고 있다. 결합을 인터페이스로 전환하고, 얘를 프라이머리 빈으로 등록한다.
// todo : 다만, SimpleFolderService 에서는 해당되는 구상클래스 빈을 주입받을 수 있도록 한다.
@Service
public class DistributedFolderService implements FolderService {

    private final FolderRepository folderRepository;
    private final FolderSearchRepository folderSearchRepository;
    private final SearchEngine searchEngine;

    // already tested.
    @Transactional
    public String generateFolder(String folderCP, FoldersGenerateRequestDto dto) {
        Folder folder = Folder.builder()
                .folderCP(folderCP)
                .isTitleOpen(dto.getIsTitleOpen())
                .title(dto.getTitle())
                .symmetricKeyEWF(dto.getSymmetricKeyEWF())
                .lastChangedDate(LocalDateTime6Digit.now())
                .build();

        folderRepository.save(folder);
        folderSearchRepository.save(folder.getFolderSearch());

        return folderCP;
    }

    // todo : 이거 테스트 해야 함. 명세 바뀐거 대로.
    @Transactional(readOnly = true)
    public List<FolderSearch> search(String keyword) {
        return null;
    }

    private float getMinMaxScaled(int input, int max, int min) {
        // max min 같은 경우를 따져봤는데 그냥 0 리턴해도 될 듯
        if (max == min) {
            return 0f;
        }
        else {  
            return ((float)input - (float)min) / ((float)max - (float)min);
        }
    }
}
