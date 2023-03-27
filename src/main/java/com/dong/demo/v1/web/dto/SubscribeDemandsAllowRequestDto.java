package com.dong.demo.v1.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscribeDemandsAllowRequestDto {

    private final String folderPublicKey;
    private final byte[] byteSign;
    private final String accountCP;
    private final String symmetricKeyEWA;

    @Builder
    public SubscribeDemandsAllowRequestDto(String folderPublicKey, byte[] byteSign, String accountCP, String symmetricKeyEWA) {
        this.folderPublicKey = folderPublicKey;
        this.byteSign = byteSign;
        this.accountCP = accountCP;
        this.symmetricKeyEWA = symmetricKeyEWA;
    }
}
