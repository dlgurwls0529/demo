package com.dong.demo.v1.web.dto;

import com.dong.demo.v1.web.validate.ValidBase58RSAPublicKeyFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscribeDemandsAddRequestDto {

    @NotBlank
    @Size(max = 60)
    private final String folderCP;

    @NotBlank
    // @ValidBase58RSAPublicKeyFormat
    private final String accountPublicKey;

    @NotEmpty
    private final byte[] byteSign;

    @Builder
    public SubscribeDemandsAddRequestDto(String folderCP, String accountPublicKey, byte[] byteSign) {
        this.folderCP = folderCP;
        this.accountPublicKey = accountPublicKey;
        this.byteSign = byteSign;
    }
}
