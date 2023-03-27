package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FilesGenerateRequestDto {

    private final byte[] byteSign;
    private final String subhead;

    @Builder
    public FilesGenerateRequestDto(byte[] byteSign, String subhead) {
        this.byteSign = byteSign;
        this.subhead = subhead;
    }
}
