package com.dong.demo.v1.web.dto;


import com.dong.demo.v1.web.validate.ValidRSAPublicKeyEncodingFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WriteAuthsAddRequestDto {

    @NotBlank
    @Size(max = 60)
    private final String accountCP;

    @NotBlank
    @Size(max = 60)
    private final String folderCP;

    @NotBlank
    // @ValidRSAPublicKeyEncodingFormat
    private final String folderPublicKey;

    @NotBlank
    private final String folderPrivateKeyEWA;

    @Builder
    public WriteAuthsAddRequestDto(String accountCP, String folderCP, String folderPublicKey, String folderPrivateKeyEWA) {
        this.accountCP = accountCP;
        this.folderCP = folderCP;
        this.folderPublicKey = folderPublicKey;
        this.folderPrivateKeyEWA = folderPrivateKeyEWA;
    }
}
