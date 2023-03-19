package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FilesGetResponseDto {

    private final String folderCP;
    private final Long fileId;
    private final LocalDateTime lastChagedDate;
    private final String subheadEWS;
    private final String contentsEWS;

    @Builder
    public FilesGetResponseDto(String folderCP, Long fileId, LocalDateTime lastChagedDate, String subheadEWS, String contentsEWS) {
        this.folderCP = folderCP;
        this.fileId = fileId;
        this.lastChagedDate = lastChagedDate;
        this.subheadEWS = subheadEWS;
        this.contentsEWS = contentsEWS;
    }
}
