package com.dong.demo.v1.domain.subDemand;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubDemand {

    private final String accountCP;
    private final String folderCP;
    private final String accountPublicKey;

    @Override
    public boolean equals(Object obj) {
        SubDemand target = (SubDemand) obj;

        boolean accountCPEquality = this.accountCP.equals(target.accountCP);
        boolean folderCPEquality = this.folderCP.equals(target.folderCP);
        boolean accountPublicKeyEquality = this.accountPublicKey.equals(target.getAccountPublicKey());

        return accountCPEquality && folderCPEquality && accountPublicKeyEquality;
    }
}
