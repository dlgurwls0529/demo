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

    @Builder
    public FilesGetResponseDto(String folderCP, String fileId, LocalDateTime lastChangedDate, String subheadEWS) {
        this.folderCP = folderCP;
        this.fileId = fileId;
        this.lastChangedDate = lastChangedDate;
        this.subheadEWS = subheadEWS;
    }
}
