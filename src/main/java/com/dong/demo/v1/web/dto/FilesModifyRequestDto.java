package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FilesModifyRequestDto {

    private final byte[] byteSign;
    private final String subhead;
    private final String contents;

    @Builder
    public FilesModifyRequestDto(byte[] byteSign, String subhead, String contents) {
        this.byteSign = byteSign;
        this.subhead = subhead;
        this.contents = contents;
    }
}
