package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.exception.*;
import com.dong.demo.v1.service.subDemand.SubDemandService;
import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import com.dong.demo.v1.web.validate.InValidInputMessageWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubDemandsApiController {

    // 성공 및 실패 테스트 write 작업만 실패 테스트까지 하고, read only 는 성공 테스트만 해도 된다.
    // BindingResult 받아서, 그거로

    private final SubDemandService subDemandService;
    private final InValidInputMessageWriter writer;

    /*
        [addSubscribeDemand]
        성공 테스트
        키 중복 예외, 참조 무결성, verify invalid -> Bad Request
        verify 실패 -> unAuthorized
    */

    @PostMapping("/api/v1/subscribe-demands/add")
    public ResponseEntity<String> addSubScribeDemand(
            @Valid @RequestBody SubscribeDemandsAddRequestDto dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>(writer.write(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);
        }
        else {
            try {
                subDemandService.addSubscribeDemand(dto);
                return new ResponseEntity<String>(HttpStatus.OK);
            }
            catch (DuplicatePrimaryKeyException | NoMatchParentRowException | VerifyInvalidInputException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (VerifyFailedException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    // 성공 실패 테스트 다 짜면 밑에 꺼 하기
    // todo : 그 만약에 subscribe 중간에 없어지면(버튼 같은거 눌렀는데, delete 되어서 if 절에서 걸리면) -> else 에서 커스텀 예외 던진다.
    // todo : DataAccess Exception 은 핸들링되면 안된다. 무결성만 처리한다.

    /*
        [allowSubscribeDemand]
        성공 테스트
        키 중복 예외, 참조 무결성, verify invalid -> Bad Request
        verify 실패 -> unAuthorized
    */

    @PostMapping("/api/v1/subscribe-demands/allow")
    public ResponseEntity<String> allowSubscribe(
            @Valid @RequestBody SubscribeDemandsAllowRequestDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>(writer.write(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);
        }
        else {
            try {
                subDemandService.allowSubscribe(dto);
                return new ResponseEntity<String>(HttpStatus.OK);
            }
            catch (DuplicatePrimaryKeyException | NoMatchParentRowException | VerifyInvalidInputException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (VerifyFailedException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            catch (NoSuchSubscribeDemandException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }


    // 성공 테스트.
    @GetMapping("/api/v1/subscribe-demands")
    public ResponseEntity<List<String>> getSubscribeDemands(@RequestParam("folderCP") String folderCP) {
        return new ResponseEntity<List<String>>(subDemandService.getSubscribeDemand(folderCP), HttpStatus.OK);
    }
}
