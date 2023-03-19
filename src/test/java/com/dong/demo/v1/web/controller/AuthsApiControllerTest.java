package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.WriteAuthsGetResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void generateWriteAuthByAccountCPStubTest() {
        // given
        String accountCP = "accountCP_TEST";
        String url = "http://localhost:" + port + "/api/v1/write-auths/" + accountCP + "/folders";

        // when
        ResponseEntity<List> responseEntity =
                restTemplate.getForEntity(url, List.class, accountCP);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(2, responseEntity.getBody().size());

        Assertions.assertEquals(responseEntity.getBody().get(0), responseEntity.getBody().get(1));
    }
}