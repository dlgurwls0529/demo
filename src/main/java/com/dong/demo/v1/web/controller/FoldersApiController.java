package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.dong.demo.v1.web.dto.FoldersSearchResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLEngineResult;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FoldersApiController {

    @PostMapping("/api/v1/folders/{folderCP}")
    public ResponseEntity<String> generateFolder(
            @PathVariable String folderCP,
            @RequestBody FoldersGenerateRequestDto dto) throws JsonProcessingException {

        // /api/v1/folders/{folderPublicKey}/files
        // 여기에서 folderPublicKey 가 비어있는 경우 folders url 로 인식된다.
        // /api/v1/folders/files 이런 느낌으로. 문제는 없지만 정책 상 안되니까 CONFLICT 로 처리.
        if (folderCP.equals("files")) {
            return new ResponseEntity<String>(
                    "Maybe you requested api/v1/folders/~/files." +
                    "folderPublicKey is empty. ",
                    HttpStatus.CONFLICT);
        }
        else {
            return new ResponseEntity<String>(folderCP, HttpStatus.OK);
        }

    }

    @GetMapping("/api/v1/folders")
    public ResponseEntity<List<FoldersSearchResponseDto>> search(@RequestParam("keyword") String keyword) {
        FoldersSearchResponseDto dto = FoldersSearchResponseDto.builder()
                .title("title_TEST")
                .folderCP("folderCP_TEST")
                .build();

        int length = keyword.length();

        List<FoldersSearchResponseDto> list = new ArrayList<>();

        for(int i = 0; i < length; i++) {
            list.add(dto);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
