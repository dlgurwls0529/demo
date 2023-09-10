package com.dong.demo.v1.service.folder;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearch;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearchRepository;
import com.dong.demo.v1.service.folder.search.SearchEngine;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
@Qualifier("simple")
@Service
public class SimpleFolderService implements FolderService {

    private final FolderRepository folderRepository;
    private final SearchEngine searchEngine;

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

        return folderCP;
    }

    @Transactional(readOnly = true)
    public List<FolderSearch> search(String keyword) {
        List<String[]> search_list = folderRepository.findAllFolderCPAndTitle();
        int length = search_list.size();

        if (length == 0 || keyword.length() == 0) {
            return new ArrayList<>();
        }

        int[] similarity_folderCP = new int[length];
        int[] similarity_title = new int[length];

        similarity_folderCP[0] = (int)searchEngine.similarity(keyword, search_list.get(0)[0]);
        similarity_title[0] = (int)searchEngine.similarity(keyword, search_list.get(0)[1]);

        int folderCP_sim_min = similarity_folderCP[0];
        int folderCP_sim_max = similarity_folderCP[0];

        int title_sim_min = similarity_title[0];
        int title_sim_max = similarity_title[0];

        for (int i = 1; i < length; i++) {
            similarity_folderCP[i] = (int)searchEngine.similarity(keyword, search_list.get(i)[0]);
            similarity_title[i] = (int)searchEngine.similarity(keyword, search_list.get(i)[1]);

            if (similarity_folderCP[i] < folderCP_sim_min) {
                folderCP_sim_min = similarity_folderCP[i];
            }
            if (similarity_folderCP[i] > folderCP_sim_max) {
                folderCP_sim_max = similarity_folderCP[i];
            }
            if (similarity_title[i] < title_sim_min) {
                title_sim_min = similarity_title[i];
            }
            if (similarity_title[i] > title_sim_max) {
                title_sim_max = similarity_title[i];
            }
        }

        for(int i = 0; i < length; i++) {
            float scaled_folderCP_sim = 0.5f * getMinMaxScaled(similarity_folderCP[i], folderCP_sim_max, folderCP_sim_min);
            float scaled_title_sim = 0.5f * getMinMaxScaled(similarity_title[i], title_sim_max, title_sim_min);
            float total_similarity = scaled_folderCP_sim + scaled_title_sim;

            String[] temp = {
                     search_list.get(i)[0],
                     search_list.get(i)[1],
                     String.valueOf(total_similarity)
             };
             search_list.set(i, temp);
        }

        search_list.sort(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return -Float.compare(Float.parseFloat(o1[2]), Float.parseFloat(o2[2]));
            }
        });

        List<FolderSearch> folderSearches = new ArrayList<>();
        for (String[] s : search_list) {
            folderSearches.add(FolderSearch.builder().folderCP(s[0]).title(s[1]).build());
        }

        return folderSearches;
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
