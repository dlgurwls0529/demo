package com.dong.demo.v1.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class FoldersGenerateRequestDto {

    @NotNull
    private final boolean isTitleOpen;

    // 빈 값을 막으면 안되고, null 만 해도 DB 단까지는 안 갈 듯.
    @NotNull
    private final String symmetricKeyEWF;

    @NotBlank
    private final String title;

    @Builder
    public FoldersGenerateRequestDto(boolean isTitleOpen, String symmetricKeyEWF, String title) {
        this.isTitleOpen = isTitleOpen;
        this.symmetricKeyEWF = symmetricKeyEWF;
        this.title = title;
    }

    public boolean getIsTitleOpen() {
        return isTitleOpen;
    }

    public String getSymmetricKeyEWF() {
        return symmetricKeyEWF;
    }

    public String getTitle() {
        return title;
    }
}
