package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.exception.*;
import com.dong.demo.v1.service.file.FileService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FilesApiController {

    // 이거 테스트. validation 실패 테스트는 이미 끝났으니, 밑단에서 올라온 예외 핸들링만 테스트.

    private final Base58RSAPublicKeyFormatValidator base58RSAPublicKeyFormatValidator;
    private final UUIDFormatValidator uuidFormatValidator;
    private final InValidInputMessageWriter messageWriter;
    private final FileService fileService;

    // [generateFile]
    // 참조 무결성 (부모 없이 넣고, BAD REQUEST 및 안들어가있는지)
    // 잘못된 byteSign 형식 (validation 안함. BAD REQUEST 및 DB에 안들어가있는지)
    // verify 실패 (키 byteSign 형식은 맞지만 다르게. UNAUTHORIZED 뜨는지, DB에 안들어가있는지)

    @PostMapping("/api/v1/folders/{folderPublicKey}/files")
    public ResponseEntity<String> generateFile(
            @PathVariable String folderPublicKey,
            @Valid @RequestBody FilesGenerateRequestDto requestDto,
            BindingResult bindingResult) {

        /* 막아야 하는 것들
            ** http://localhost:65468/api/v1/folders//files <- 얘는 folders 단에서 막는다.

            http://localhost:65468/api/v1/folders/null/files
            http://localhost:65468/api/v1/folders/    /files <- 얜 아마 base58에서 막힐 듯?
            http://localhost:65468/api/v1/folders/"invalid publicKey"/files
        */

        // 퍼블릭키로 "" 이게 들어오면 폴더 포스트 URL 으로 인식된다. 사실 이게 맞으니까, 여기에선 ""에 대해서는 테스트 하면 안되는 것이다.
        // if (!base58FormatValidator.validate(folderPublicKey) || !rsaFormatValidator.validatePublicKey(Base58.decode(folderPublicKey))) {
        if (!base58RSAPublicKeyFormatValidator.isValid(folderPublicKey, null)) {
            bindingResult.addError(new FieldError(
                    "dto",
                    "folderPublicKey",
                    "is invalid. It may be violation Base58 or RSAPublicKey Format or blank"
            ));
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>(
                    messageWriter.write(bindingResult.getFieldErrors()),
                    HttpStatus.BAD_REQUEST
            );
        }
        else {
            try {
                String uuid = fileService.generateFile(folderPublicKey, requestDto);
                return new ResponseEntity<String>(uuid, HttpStatus.OK);
            }
            catch (DuplicatePrimaryKeyException | NoMatchParentRowException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (VerifyInvalidInputException e) {
                return new ResponseEntity<String>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (VerifyFailedException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            /* 얘는 처리하지 말고 그냥 서버 죽인다.
            catch (CompressAlgorithmDeprecatedException e) {
                return new ResponseEntity<String>(e.getMessage(), )
            }*/
        }
    }

    // [modifyFile]
    // 파일 없는데 수정하려하면? (No Content 확인)
    // 잘못된 byteSign 형식 (validation 안함. BAD REQUEST 및 DB에 안들어가있는지)
    // verify 실패 (키 byteSign 형식은 맞지만 다르게. UNAUTHORIZED 뜨는지, DB에 안들어가있는지)

    @PutMapping("api/v1/folders/{folderPublicKey}/files/{fileId}")
    public ResponseEntity<String> modifyFile(
            @PathVariable String folderPublicKey,
            @PathVariable String fileId,
            @Valid @RequestBody FilesModifyRequestDto requestDto,
            BindingResult bindingResult
            ) {

        // folderPublicKey 가 null, "", "   "

        // if (!base58FormatValidator.validate(folderPublicKey) || !rsaFormatValidator.validatePublicKey(Base58.decode(folderPublicKey))) {
        if (!base58RSAPublicKeyFormatValidator.isValid(folderPublicKey, null)) {
            bindingResult.addError(new FieldError(
                    "dto",
                    "folderPublicKey",
                    "is invalid. It may be violation Base58 or RSAPublicKey Format or blank"
            ));
        }
        if (!uuidFormatValidator.validate(fileId)) {
            bindingResult.addError(new FieldError(
                    "dto",
                    "fileId",
                    "is invalid. check it please."
            ));
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>(
                    messageWriter.write(bindingResult.getFieldErrors()),
                    HttpStatus.BAD_REQUEST
            );
        }
        else {
            try {
                String uuid = fileService.modifyFile(folderPublicKey, fileId, requestDto);
                return new ResponseEntity<String>(uuid, HttpStatus.OK);
            }
            catch (NoSuchFileException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NO_CONTENT);
            }
            catch (VerifyInvalidInputException e) {
                return new ResponseEntity<String>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (VerifyFailedException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @GetMapping("api/v1/folders/{folderCP}/files")
    public ResponseEntity<?> getFileByFolderCP(
            @PathVariable String folderCP
    ) {
        // 얘는 뭐 할게 없다. invalid 하면 어차피 검색 안되니까.
        List<FilesGetResponseDto> dtoList = fileService.getFileByFolderCP(folderCP);
        return new ResponseEntity<List<FilesGetResponseDto>>(dtoList, HttpStatus.OK);
    }

    @GetMapping("api/v1/folders/{folderCP}/files/{fileId}")
    public ResponseEntity<String> getContentsByFileIdAndFolderCP(
            @PathVariable String folderCP,
            @PathVariable String fileId
    ) {
        // 얘도 마찬가지 뭐 할게 없다. invalid 하면 어차피 검색 안되니까.
        String content = fileService.getContentsByFileIdAndFolderCP(folderCP, fileId);
        return new ResponseEntity<String>(content, HttpStatus.OK);
    }
}
