package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.dong.demo.v1.web.dto.FoldersSearchResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FoldersApiController {

    @PostMapping("/api/v1/folders/{folderCP}")
    public ResponseEntity<String> generateFolder(
            @PathVariable String folderCP,
            @RequestBody FoldersGenerateRequestDto dto) throws JsonProcessingException {
        return new ResponseEntity<>(folderCP, HttpStatus.OK);
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
