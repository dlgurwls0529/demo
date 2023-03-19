package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscribeDemandsAddRequestDto {

    private final String folderCP;
    private final String accountPublicKey;
    private final byte[] sign;

    @Builder
    public SubscribeDemandsAddRequestDto(String folderCP, String accountPublicKey, byte[] sign) {
        this.folderCP = folderCP;
        this.accountPublicKey = accountPublicKey;
        this.sign = sign;
    }
}
