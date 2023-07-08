package com.dong.demo.v1.web.dto;

import com.dong.demo.v1.web.validate.ValidBase58RSAPublicKeyFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SubscribeDemandsAllowRequestDto {

    @NotBlank
    // @ValidBase58RSAPublicKeyFormat
    private final String folderPublicKey;

    @NotEmpty
    private final byte[] byteSign;

    @NotBlank
    @Size(max = 60)
    private final String accountCP;

    @NotBlank
    private final String symmetricKeyEWA;

    @Builder
    public SubscribeDemandsAllowRequestDto(String folderPublicKey, byte[] byteSign, String accountCP, String symmetricKeyEWA) {
        this.folderPublicKey = folderPublicKey;
        this.byteSign = byteSign;
        this.accountCP = accountCP;
        this.symmetricKeyEWA = symmetricKeyEWA;
    }
}
