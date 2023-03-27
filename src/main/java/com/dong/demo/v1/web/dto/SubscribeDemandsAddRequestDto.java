package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscribeDemandsAddRequestDto {

    private final String folderCP;
    private final String accountPublicKey;
    private final byte[] byteSign;

    @Builder
    public SubscribeDemandsAddRequestDto(String folderCP, String accountPublicKey, byte[] byteSign) {
        this.folderCP = folderCP;
        this.accountPublicKey = accountPublicKey;
        this.byteSign = byteSign;
    }
}
