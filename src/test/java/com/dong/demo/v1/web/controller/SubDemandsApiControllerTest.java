package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubDemandsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void addSubscribeDemandStubTest() {
        // given
        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/add";

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .folderCP("folderCP_TEST")
                .accountPublicKey("accountPublicKey_TEST")
                .byteSign(new byte[]{1, 2, 3, 4})
                .build();

        // when
        ResponseEntity<Void> responseEntity =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void allowSubscribeStubTest() {
        // given
        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/allow";

        SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                .folderPublicKey("folderPublicKey_TEST")
                .byteSign(new byte[]{1, 2, 3, 4})
                .accountCP("accountCP_TEST")
                .symmetricKeyEWA("symmetricKeyEWA_TEST")
                .build();

        // when
        ResponseEntity<Void> responseEntity =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getSubScribeDemandsStubTest() {
        // given
        String folderCP = "folderCP_TEST";
        String url = "http://localhost:" + port + "/api/v1/subscribe-demands?folderCP=" + folderCP;

        // when
        ResponseEntity<List> response = restTemplate.getForEntity(
                url, List.class, folderCP
        );

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(3, response.getBody().size());
    }
}