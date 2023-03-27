package com.dong.demo.v1.domain.subDemand;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubDemand {

    private final String accountCP;
    private final String folderCP;
    private final String accountPublicKey;
}
