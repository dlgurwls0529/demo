package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesGetResponseDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilesApiController {

    @PostMapping("/api/v1/folders/{folderPublicKey}/files")
    public ResponseEntity<String> generateFile(
            @PathVariable String folderPublicKey,
            @RequestBody FilesGenerateRequestDto requestDto) {
        String uuid = "uuid_TEST";

        return new ResponseEntity<>(uuid, HttpStatus.OK);
    }

    @PutMapping("api/v1/folders/{folderPublicKey}/files/{fileId}")
    public ResponseEntity<String> modifyFile(
            @PathVariable String folderPublicKey,
            @PathVariable String fileId,
            @RequestBody FilesModifyRequestDto requestDto
            ) {
        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }

    @GetMapping("api/v1/folders/{folderCP}/files")
    public ResponseEntity<List<FilesGetResponseDto>> getFileByFolderCP(
            @PathVariable String folderCP
    ) {
        FilesGetResponseDto dto = FilesGetResponseDto.builder()
                .folderCP(folderCP)
                .fileId("folderId_TEST")
                .lastChangedDate(LocalDateTime.now())
                .subheadEWS("subheadEWS_TEST")
                .contentsEWS("contentsEWS_TEST")
                .build();

        List<FilesGetResponseDto> list = new ArrayList<>();
        list.add(dto);
        list.add(dto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
