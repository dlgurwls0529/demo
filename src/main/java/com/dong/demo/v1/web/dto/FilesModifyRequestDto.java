package com.dong.demo.v1.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FilesModifyRequestDto {

    @NotEmpty
    private final byte[] byteSign;

    @NotBlank
    private final String subhead;

    @NotNull
    private final String contents;

    @Builder
    public FilesModifyRequestDto(byte[] byteSign, String subhead, String contents) {
        this.byteSign = byteSign;
        this.subhead = subhead;
        this.contents = contents;
    }
}
