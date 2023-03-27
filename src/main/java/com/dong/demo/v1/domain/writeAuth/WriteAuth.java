package com.dong.demo.v1.domain.writeAuth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WriteAuth {

    private final String accountCP;
    private final String folderCP;
    private final String folderPublicKey;
    private final String folderPrivateKeyEWA;
}
