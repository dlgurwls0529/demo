package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FoldersApiController {

    @PostMapping("/api/v1/folders/{folderCP}")
    public ResponseEntity<String> generateFolder(
            @PathVariable String folderCP,
            @RequestBody FoldersGenerateRequestDto dto) {
        return new ResponseEntity<>(folderCP, HttpStatus.OK);
    }
}
