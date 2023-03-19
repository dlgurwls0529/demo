package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscribeDemandsAllowRequestDto {

    private final String folderPublicKey;
    private final byte[] sign;
    private final String accountCP;
    private final String symmetricEWA;

    @Builder
    public SubscribeDemandsAllowRequestDto(String folderPublicKey, byte[] sign, String accountCP, String symmetricEWA) {
        this.folderPublicKey = folderPublicKey;
        this.sign = sign;
        this.accountCP = accountCP;
        this.symmetricEWA = symmetricEWA;
    }
}
