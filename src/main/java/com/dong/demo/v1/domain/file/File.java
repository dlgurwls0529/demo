package com.dong.demo.v1.domain.file;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class File {

    private final String folderCP;
    private final String fileId;
    private final String subheadEWS;
    private final LocalDateTime lastChangedDate;
    private final String contentEWS;

}
