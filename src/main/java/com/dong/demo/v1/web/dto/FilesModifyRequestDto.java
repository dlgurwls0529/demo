package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FilesModifyRequestDto {

    private final byte[] signByte;
    private final String subhead;
    private final String contents;

    @Builder
    public FilesModifyRequestDto(byte[] signByte, String subhead, String contents) {
        this.signByte = signByte;
        this.subhead = subhead;
        this.contents = contents;
    }
}
