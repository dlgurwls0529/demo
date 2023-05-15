package com.dong.demo.v1.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FilesGenerateRequestDto {

    // 얘는 서비스에서 verify 해봐야 할 수 있음.
    // 배열이 null 이거나 다 비어있는거만 막자.
    @NotEmpty
    private final byte[] byteSign;

    // 제목은 비어있을 수 없지. 띄어쓰기도 막는다.
    @NotBlank
    private final String subhead;

    @Builder
    public FilesGenerateRequestDto(byte[] byteSign, String subhead) {
        this.byteSign = byteSign;
        this.subhead = subhead;
    }
}
