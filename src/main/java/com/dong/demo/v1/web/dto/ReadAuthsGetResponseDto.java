package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReadAuthsGetResponseDto {

    private final String folderCP;
    private final boolean isTitleOpen;
    private final String title;
    private final String symmetricKeyEWA;
    private final LocalDateTime lastChangedDate;

    @Builder
    public ReadAuthsGetResponseDto(String folderCP, boolean isTitleOpen, String title, String symmetricKeyEWA, LocalDateTime lastChangedDate) {
        this.folderCP = folderCP;
        this.isTitleOpen = isTitleOpen;
        this.title = title;
        this.symmetricKeyEWA = symmetricKeyEWA;
        this.lastChangedDate = lastChangedDate;
    }
}
