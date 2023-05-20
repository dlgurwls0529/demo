package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SubDemandsApiController {

    // todo : validation test 하기 !

    @PostMapping("/api/v1/subscribe-demands/add")
    public ResponseEntity<Void> addSubScribeDemand(@Valid @RequestBody SubscribeDemandsAddRequestDto dto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/v1/subscribe-demands/allow")
    public ResponseEntity<Void> allowSubscribe(@Valid @RequestBody SubscribeDemandsAllowRequestDto dto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/v1/subscribe-demands")
    public ResponseEntity<List<String>> getSubscribeDemands(@RequestParam("folderCP") String folderCP) {
        List<String> res = new ArrayList<>();
        res.add("a");
        res.add("b");
        res.add("c");

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
