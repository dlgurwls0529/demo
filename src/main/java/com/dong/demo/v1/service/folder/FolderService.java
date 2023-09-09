package com.dong.demo.v1.service.folder;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearch;
import com.dong.demo.v1.domain.folder.folder_search.FolderSearchRepository;
import com.dong.demo.v1.util.LocalDateTime6Digit;
import com.dong.demo.v1.service.folder.search.SearchEngine;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public interface FolderService {
    public String generateFolder(String folderCP, FoldersGenerateRequestDto dto);
    public List<FolderSearch> search(String keyword);
}
