package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FoldersGenerateRequestDto {

    private final boolean isTitleOpen;
    private final String symmetricKeyEWF;
    private final String title;

    @Builder
    public FoldersGenerateRequestDto(boolean isTitleOpen, String symmetricKeyEWF, String title) {
        this.isTitleOpen = isTitleOpen;
        this.symmetricKeyEWF = symmetricKeyEWF;
        this.title = title;
    }
}
