package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FoldersSearchResponseDto {

    private final String title;
    private final String folderCP;

    @Builder
    public FoldersSearchResponseDto(String title, String folderCP) {
        this.title = title;
        this.folderCP = folderCP;
    }
}
