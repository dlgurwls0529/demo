package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FilesGetResponseDto {

    private final String folderCP;
    private final String fileId;
    private final LocalDateTime lastChangedDate;
    private final String subheadEWS;
    private final String contentsEWS;

    @Builder
    public FilesGetResponseDto(String folderCP, String fileId, LocalDateTime lastChangedDate, String subheadEWS, String contentsEWS) {
        this.folderCP = folderCP;
        this.fileId = fileId;
        this.lastChangedDate = lastChangedDate;
        this.subheadEWS = subheadEWS;
        this.contentsEWS = contentsEWS;
    }
}
