package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.exception.NoMatchParentRowException;
import com.dong.demo.v1.service.readAuth.ReadAuthService;
import com.dong.demo.v1.service.writeAuth.WriteAuthService;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.*;
import com.dong.demo.v1.web.validate.InValidInputMessageWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthsApiController {

    private final WriteAuthService writeAuthService;
    private final ReadAuthService readAuthService;
    private final InValidInputMessageWriter writer;

    // 서칭 기능이라 validation 필요 없다.
    @GetMapping("/api/v1/write-auths/{accountCP}/folders")
    public ResponseEntity<List<WriteAuthsGetResponseDto>> getWriteAuthByAccountCP(
            @PathVariable String accountCP
    ) {
        List<WriteAuthsGetResponseDto> dtoList = writeAuthService.getWriteAuthByAccountCP(accountCP);
        return new ResponseEntity<List<WriteAuthsGetResponseDto>>(dtoList, HttpStatus.OK);
    }

    // 얘도 그렇다.
    @GetMapping("/api/v1/read-auths/{accountCP}/folders")
    public ResponseEntity<List<ReadAuthsGetResponseDto>> getReadAuthByAccountCP(
            @PathVariable String accountCP) {
        List<ReadAuthsGetResponseDto> dtoList = readAuthService.getReadAuthByAccountCP(accountCP);
        return new ResponseEntity<List<ReadAuthsGetResponseDto>>(dtoList, HttpStatus.OK);
    }

    // 실패 테스트
    // folderCP 와 folderPublicKey 모두 유효한 형식이나, match 가 안되는 경우(folderCP 와 accountPublicKey ... -> BAD REQ
    // 무결성 예외. duplicate 랑 referential integrity. -> BAD REQ

    // 얘는 Invalid 해도 상관 없지만, 혹시 모르니 Valid 를 한다.
    // 만약 publicKey compress 했는데, CP 안나오면 Bad Request.
    @PostMapping("/api/v1/write-auths")
    public ResponseEntity<String> addWriteAuthority(
            @Valid @RequestBody WriteAuthsAddRequestDto dto,
            BindingResult bindingResult
    ) {

        if (bindingResult.getFieldErrors("folderCP").size() == 0 &&
                bindingResult.getFieldErrors("folderPublicKey").size() == 0 &&
                !KeyCompressor.compress(dto.getFolderPublicKey()).equals(dto.getFolderCP())) {
            bindingResult.addError(new FieldError(
                    "dto",
                    "folderCP",
                    "must match with compressed publicKey"
            ));
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>(writer.write(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);
        } else {
            try {
                writeAuthService.addWriteAuthority(dto);
                return new ResponseEntity<String>(HttpStatus.OK);
            } catch (DuplicatePrimaryKeyException | NoMatchParentRowException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } // catch (DataAccessException e) {
                // 밑에 에러 있었는데, 고침.
                // Referential integrity constraint violation: "CONSTRAINT_AE: PUBLIC.WRITEAUTHORITY FOREIGN ~~
                // INSERT INTO WriteAuthority VALUES(?, ?, ?, ?) [23506-214]
                // h2 환경에서 실행할 때, 원래 있던 ICs의 mariaDB 기준 참조 무결성 에러 코드가
                // h2 의 참조 무결성 에러 코드랑 다르니까 예외 전환을 잘 못한 듯 하다.
                // 아무튼 그거 추가해서 NoMatch~ 로 잘 전환 된다.
              //  return new ResponseEntity<String>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
            // }
        }
    }
}
