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

    @Override
    public boolean equals(Object obj) {
        WriteAuth target = (WriteAuth)obj;
        boolean accountCPEquality = this.accountCP.equals(target.accountCP);
        boolean folderCPEquality = this.folderCP.equals(target.folderCP);
        boolean folderPublicKeyEquality = this.folderPublicKey.equals(target.getFolderPublicKey());
        boolean folderPrivateKeyEWAEquality = this.folderPrivateKeyEWA.equals(target.getFolderPrivateKeyEWA());

        return accountCPEquality && folderCPEquality && folderPublicKeyEquality && folderPrivateKeyEWAEquality;
    }
}
