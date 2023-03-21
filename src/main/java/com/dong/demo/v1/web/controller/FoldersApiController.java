package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.dong.demo.v1.web.dto.FoldersSearchResponseDto;
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
            @RequestBody FoldersGenerateRequestDto dto) {
        return new ResponseEntity<>(folderCP, HttpStatus.OK);
    }

    @GetMapping("/api/v1/folders")
    public ResponseEntity<List<FoldersSearchResponseDto>> search(@RequestParam("keyword") String keyword) {
        FoldersSearchResponseDto dto = FoldersSearchResponseDto.builder()
                .title("title_TEST")
                .folderCP("folderCP_TEST")
                .build();

        List<FoldersSearchResponseDto> list = new ArrayList<>();
        list.add(dto);
        list.add(dto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
