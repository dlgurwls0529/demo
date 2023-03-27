package com.dong.demo.v1.web.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class WriteAuthsAddRequestDto {

    private final String accountCP;
    private final String folderCP;
    private final String folderPublicKey;
    private final String folderPrivateKeyEWA;

    @Builder
    public WriteAuthsAddRequestDto(String accountCP, String folderCP, String folderPublicKey, String folderPrivateKeyEWA) {
        this.accountCP = accountCP;
        this.folderCP = folderCP;
        this.folderPublicKey = folderPublicKey;
        this.folderPrivateKeyEWA = folderPrivateKeyEWA;
    }
}
