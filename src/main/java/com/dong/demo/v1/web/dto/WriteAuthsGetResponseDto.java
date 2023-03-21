package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class WriteAuthsGetResponseDto {

    private final String folderCP;
    private final String folderPublicKey;
    private final String folderPrivateKeyEWA;
    private final boolean isTitleOpen;
    private final String title;
    private final String symmetricKeyEWF;
    private final LocalDateTime lastChangedDate;

    @Builder
    public WriteAuthsGetResponseDto(String folderCP, String folderPublicKey, String folderPrivateKeyEWA, boolean isTitleOpen, String title, String symmetricKeyEWF, LocalDateTime lastChangedDate) {
        this.folderCP = folderCP;
        this.folderPublicKey = folderPublicKey;
        this.folderPrivateKeyEWA = folderPrivateKeyEWA;
        this.isTitleOpen = isTitleOpen;
        this.title = title;
        this.symmetricKeyEWF = symmetricKeyEWF;
        this.lastChangedDate = lastChangedDate;
    }
}
