package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.exception.DataAccessException;
import com.dong.demo.v1.exception.DuplicatePrimaryKeyException;
import com.dong.demo.v1.service.folder.FolderService;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.dong.demo.v1.web.dto.FoldersSearchResponseDto;
import com.dong.demo.v1.web.validate.InValidInputMessageWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FoldersApiController {

    private final FolderService folderService;
    private final InValidInputMessageWriter inputMessageWriter;

    // file 에서는 selection 작업이었기 때문에, 데이터가 무결할 필요가 없었다.
    // 하지만 여기는 좀 중요하다. DB 에서도 세밀하게 체크한 건 아니라, 여기서 안하면 핸들링하기 복잡해진다.
    // DB 단에서 중복, 참조 무결성 예외 말고도 몇개 더 rethrow 하면 좋을 듯.
    // ex) folderCP 길이 검사, 타입 변환 예외, NULL 예외 (거의 안뜨긴 한다.) -> 다 안해도 되김 함.
    @PostMapping("/api/v1/folders/{folderCP}")
    public ResponseEntity<String> generateFolder(
            @PathVariable String folderCP,
            @Valid @RequestBody FoldersGenerateRequestDto dto,
            BindingResult bindingResult
    ) {

        // /api/v1/folders/{folderPublicKey}/files
        // 여기에서 folderPublicKey 가 비어있는 경우 folders url 로 인식된다.
        // /api/v1/folders/files 이런 느낌으로. 문제는 없지만 정책 상 안되니까 CONFLICT 로 처리.

        // -> Valid 를 하면 dto 타입이 달라서 모든 dto 에 null 값이 세팅(이름이 매칭되는 필드가 JSON 에 없으니까)
        // 그래서 자동으로 Validation 에 실패하게 된다.
        // 다시 말하면, ~/folders/files 이렇게 하면 Validation 에서 걸린다는 뜻.
        // 그래서 아래 로직을 고친다.

        /* if (folderCP.equals("files")) {
            return new ResponseEntity<String>(
                    "Maybe you requested api/v1/folders/~/files." +
                    "folderPublicKey is empty. ",
                    HttpStatus.CONFLICT);
        } */

        // validate folderCP and validation complete.
        if (folderCP.equals("null") || folderCP.replaceAll(" ", "").length() == 0) {
            bindingResult.addError(new FieldError(
                    "dto",
                    "folderCP",
                    "must not be blank")
            );
        }
        else if (folderCP.length() > 60) {
            bindingResult.addError(new FieldError(
                    "dto",
                    "folderCP",
                    "must not longer than 60")
            );
        }

        // conflict handling
        if (folderCP.equals("files") &&
            !dto.getIsTitleOpen() &&
            dto.getTitle() == null &&
            dto.getSymmetricKeyEWF() == null) {
            return new ResponseEntity<String>(
                    "Maybe you requested api/v1/folders/~/files." +
                            "folderPublicKey is empty. ",
                    HttpStatus.CONFLICT);
        }
        // not conflict.
        else {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<String>(
                    inputMessageWriter.write(bindingResult.getFieldErrors()),
                    HttpStatus.BAD_REQUEST);
            }
            else {
                try {
                    folderService.generateFolder(folderCP, dto);
                    return new ResponseEntity<String>(folderCP, HttpStatus.OK);
                } catch (DuplicatePrimaryKeyException e) {
                    return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
                } catch (DataAccessException e) {
                    return new ResponseEntity<String>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    // 이거는 딱히 상관 없다.
    @GetMapping("/api/v1/folders")
    public ResponseEntity<List<FoldersSearchResponseDto>> search(@RequestParam("keyword") String keyword) {
        try {
            List<String[]> resultFolderCPAndTitleList = folderService.search(keyword);
            List<FoldersSearchResponseDto> responseDtoList = new ArrayList<>();

            for (String[] strings : resultFolderCPAndTitleList) {
                responseDtoList.add(FoldersSearchResponseDto.builder()
                        .folderCP(strings[0])
                        .title(strings[1])
                        .build()
                );
            }

            return new ResponseEntity<List<FoldersSearchResponseDto>>(responseDtoList, HttpStatus.OK);
        }
        catch (DataAccessException e) {
            return new ResponseEntity<List<FoldersSearchResponseDto>>((List<FoldersSearchResponseDto>) null, HttpStatus.BAD_REQUEST);
        }
    }
}
