package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getWriteAuthByAccountCPStubTest() {
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

    @Test
    public void getReadAuthByAccountCPStubTest() {
        // given
        String accountCP = "accountCP_TEST";
        String url = "http://localhost:" + port + "/api/v1/read-auths/" + accountCP + "/folders";

        // when
        ResponseEntity<List> responseEntity =
                restTemplate.getForEntity(url, List.class, accountCP);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(2, responseEntity.getBody().size());
        Assertions.assertEquals(responseEntity.getBody().get(0), responseEntity.getBody().get(1));
    }

    @Test
    public void addWriteAuthorityStubTest() {
        // given
        String url = "http://localhost:" + port + "/api/v1/write-auths";

        WriteAuthsAddRequestDto dto = WriteAuthsAddRequestDto.builder()
                .accountCP("accountCP_TEST")
                .folderCP("folderCP_TEST")
                .folderPublicKey("folderPublicKey_TEST")
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        ResponseEntity<Void> response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}