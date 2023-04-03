package com.dong.demo.v1.domain.readAuth;

import com.dong.demo.v1.domain.folder.Folder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReadAuth {

    private final String accountCP;
    private final String folderCP;
    private final String symmetricKeyEWA;

    @Override
    public boolean equals(Object obj) {
        boolean accountCPEquality = this.accountCP.equals(((ReadAuth)obj).accountCP);
        boolean folderCPEquality = this.folderCP.equals(((ReadAuth)obj).folderCP);
        boolean symEquality = this.symmetricKeyEWA.equals(((ReadAuth)obj).symmetricKeyEWA);

        return accountCPEquality && folderCPEquality && symEquality;
    }
}
