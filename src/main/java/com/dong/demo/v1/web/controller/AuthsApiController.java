package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.*;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthsApiController {

    @GetMapping("/api/v1/write-auths/{accountCP}/folders")
    public ResponseEntity<List<WriteAuthsGetResponseDto>> getWriteAuthByAccountCP(
            @PathVariable String accountCP
    ) {
        WriteAuthsGetResponseDto dto =
                WriteAuthsGetResponseDto.builder()
                        .folderCP("folderCP_TEST")
                        .folderPublicKey("folderPun_TEST")
                        .folderPrivateKeyEWA("folderPri_TEST")
                        .isTitleOpen(true)
                        .title("title_TEST")
                        .symmetricKeyEWF("sym_TEST")
                        .lastChangedDate(LocalDateTime.now())
                        .build();

        List<WriteAuthsGetResponseDto> list = new ArrayList<>();
        list.add(dto);
        list.add(dto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/api/v1/read-auths/{accountCP}/folders")
    public ResponseEntity<List<ReadAuthsGetResponseDto>> getReadAuthByAccountCP(
            @PathVariable String accountCP) {
        ReadAuthsGetResponseDto dto =
                ReadAuthsGetResponseDto.builder()
                        .folderCP("folderCP_TEST")
                        .isTitleOpen(true)
                        .title("title_TEST")
                        .symmetricKeyEWA("symmetricKeyEWA_TEST")
                        .lastChangedDate(LocalDateTime.now())
                        .build();

        List<ReadAuthsGetResponseDto> list = new ArrayList<>();
        list.add(dto);
        list.add(dto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/api/v1/write-auths")
    public ResponseEntity<Void> addWriteAuthority(@RequestBody WriteAuthsAddRequestDto dto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
