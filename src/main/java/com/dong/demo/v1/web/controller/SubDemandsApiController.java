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

    // todo : 성공 및 실패 테스트 write 작업만 실패 테스트까지 하고, read only 는 성공 테스트만 해도 된다.
    // todo : BindingResult 받아서, 그거로

    private final SubDemandService subDemandService;
    private final InValidInputMessageWriter writer;

    /*
        todo : [addSubscribeDemand]
        todo : 성공 테스트
        todo : 키 중복 예외, 참조 무결성, verify invalid -> Bad Request
        todo : verify 실패 -> unAuthorized
    */

    @PostMapping("/api/v1/subscribe-demands/add")
    public ResponseEntity<?> addSubScribeDemand(
            @Valid @RequestBody SubscribeDemandsAddRequestDto dto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>(writer.write(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);
        }
        else {
            try {
                subDemandService.addSubscribeDemand(dto);
                return new ResponseEntity<Void>(HttpStatus.OK);
            }
            catch (DuplicatePrimaryKeyException | NoMatchParentRowException | VerifyInvalidInputException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (DataAccessException e) {
                return new ResponseEntity<String>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (VerifyFailedException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    // todo : 성공 실패 테스트 다 짜면 밑에 꺼 하기
    // todo : 그 만약에 subscribe 중간에 없어지면(버튼 같은거 눌렀는데, delete 되어서 if 절에서 걸리면) -> else 에서 커스텀 예외 던진다.
    // todo : DataAccess Exception 은 핸들링되면 안된다. 무결성만 처리한다.

    /*
        todo : [allowSubscribeDemand]
        todo : 성공 테스트
        todo : 키 중복 예외, 참조 무결성, verify invalid -> Bad Request
        todo : verify 실패 -> unAuthorized
    */

    @PostMapping("/api/v1/subscribe-demands/allow")
    public ResponseEntity<?> allowSubscribe(
            @Valid @RequestBody SubscribeDemandsAllowRequestDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<String>(writer.write(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);
        }
        else {
            try {
                subDemandService.allowSubscribe(dto);
                return new ResponseEntity<Void>(HttpStatus.OK);
            }
            catch (DuplicatePrimaryKeyException | NoMatchParentRowException | VerifyInvalidInputException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (DataAccessException e) {
                return new ResponseEntity<String>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (VerifyFailedException e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
    }


    // todo : 성공 테스트.
    @GetMapping("/api/v1/subscribe-demands")
    public ResponseEntity<?> getSubscribeDemands(@RequestParam("folderCP") String folderCP) {
        try {
            return new ResponseEntity<List<String>>(subDemandService.getSubscribeDemand(folderCP), HttpStatus.OK);
        }
        catch (DataAccessException e) {
            return new ResponseEntity<String>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
