package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesGetResponseDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import com.dong.demo.v1.web.validate.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FilesApiController {

    /*private final Base58FormatValidator base58FormatValidator;
    private final RSAFormatValidator rsaFormatValidator;*/
    private final Base58RSAPublicKeyFormatValidator base58RSAPublicKeyFormatValidator;
    private final UUIDFormatValidator uuidFormatValidator;

    @PostMapping("/api/v1/folders/{folderPublicKey}/files")
    public ResponseEntity<String> generateFile(
            @PathVariable String folderPublicKey,
            @Valid @RequestBody FilesGenerateRequestDto requestDto) {

        /* 막아야 하는 것들
            ** http://localhost:65468/api/v1/folders//files <- 얘는 folders 단에서 막는다.

            http://localhost:65468/api/v1/folders/null/files
            http://localhost:65468/api/v1/folders/    /files <- 얜 아마 base58에서 막힐 듯?
            http://localhost:65468/api/v1/folders/"invalid publicKey"/files
        */

        // 퍼블릭키로 "" 이게 들어오면 폴더 포스트 URL 으로 인식된다. 사실 이게 맞으니까, 여기에선 ""에 대해서는 테스트 하면 안되는 것이다.
        // if (!base58FormatValidator.validate(folderPublicKey) || !rsaFormatValidator.validatePublicKey(Base58.decode(folderPublicKey))) {
        if (!base58RSAPublicKeyFormatValidator.isValid(folderPublicKey, null)) {
            return new ResponseEntity<String>("folderPublicKey format is invalid." +
                            "It may be violation Base58 or RSAPublicKey Format or blank",
                    HttpStatus.BAD_REQUEST);
        }
        else {
            String uuid = "uuid_TEST";
            return new ResponseEntity<String>(uuid, HttpStatus.OK);
        }
    }

    @PutMapping("api/v1/folders/{folderPublicKey}/files/{fileId}")
    public ResponseEntity<String> modifyFile(
            @PathVariable String folderPublicKey,
            @PathVariable String fileId,
            @Valid @RequestBody FilesModifyRequestDto requestDto
            ) {

        // folderPublicKey 가 null, "", "   "

        // if (!base58FormatValidator.validate(folderPublicKey) || !rsaFormatValidator.validatePublicKey(Base58.decode(folderPublicKey))) {
        if (!base58RSAPublicKeyFormatValidator.isValid(folderPublicKey, null)) {
            return new ResponseEntity<String>("folderPublicKey format is invalid." +
                    "It may be violation Base58 or RSAPublicKey Format or blank",
                    HttpStatus.BAD_REQUEST);
        } else if (!uuidFormatValidator.validate(fileId)) {
            return new ResponseEntity<String>("fileId format is invalid. check it please.",
                    HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<String>(fileId, HttpStatus.OK);
        }
    }

    @GetMapping("api/v1/folders/{folderCP}/files")
    public ResponseEntity<List<FilesGetResponseDto>> getFileByFolderCP(
            @PathVariable String folderCP
    ) {
        // 얘는 뭐 할게 없다. invalid 하면 어차피 검색 안되니까.

        FilesGetResponseDto dto = FilesGetResponseDto.builder()
                .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK4so+Q=")
                .fileId(String.valueOf(UUID.randomUUID()))
                .lastChangedDate(LocalDateTime.now())
                .subheadEWS("what meal do I eat?")
                .build();

        List<FilesGetResponseDto> list = new ArrayList<>();
        list.add(dto);
        list.add(dto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("api/v1/folders/{folderCP}/files/{fileId}")
    public ResponseEntity<String> getContentsByFileIdAndFolderCP(
            @PathVariable String folderCP,
            @PathVariable String fileId
    ) {
        // 얘도 마찬가지 뭐 할게 없다. invalid 하면 어차피 검색 안되니까.

        return new ResponseEntity<>("배고파" + folderCP + fileId, HttpStatus.OK);
    }
}
