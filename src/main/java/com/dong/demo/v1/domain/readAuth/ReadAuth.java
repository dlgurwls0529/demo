package com.dong.demo.v1.domain.readAuth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReadAuth {

    private final String accountCP;
    private final String folderCP;
    private final String symmetricKeyEWA;
}
