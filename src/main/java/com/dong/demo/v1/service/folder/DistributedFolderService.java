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

import java.util.*;


@RequiredArgsConstructor
@Primary
@Qualifier("distributed")
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

        // 이렇게 해도, 검색할 때 folder 를 folderCP 로 먼저 검색하니까, 검색은 가능.
        if (folder.getIsTitleOpen()) {
            folderSearchRepository.save(folder.getFolderSearch());
        }

        return folderCP;
    }

    @Transactional(readOnly = true)
    public List<FolderSearch> search(String keyword) {
        // 비어있으면 n-gram 계산 시 문제 생길 수 있음.
        if (keyword.length() == 0) {return new ArrayList<>();}

        List<FolderSearch> fdrSearches = new ArrayList<>();

        Folder folder = folderRepository.find(keyword);

        if (folder != null) {
            fdrSearches.add(folder.getFolderSearch());
        }
        else {
            fdrSearches = folderSearchRepository.findAll();

            if (fdrSearches.size() != 0) {
                Map<String, Integer> simHash = new HashMap<>();

                for (FolderSearch fdrSearch : fdrSearches) {
                    if (!simHash.containsKey(fdrSearch.getTitle())) {
                        simHash.put(
                                fdrSearch.getTitle(),
                                (int) searchEngine.similarity(keyword, fdrSearch.getTitle())
                        );
                    }
                }

                fdrSearches.sort(new Comparator<FolderSearch>() {
                    @Override
                    public int compare(FolderSearch o1, FolderSearch o2) {
                        return -Integer.compare(
                                simHash.get(o1.getTitle()),
                                simHash.get(o2.getTitle())
                        );
                    }
                });
            }
        }

        return fdrSearches;
    }
}
