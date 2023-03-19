package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// @WebMvcTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FoldersApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void generateFolderStubTest() {
        // given
        String folderCP = "MIIBIjANBgkqhki";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("DFSJHKERHU")
                .title("test_TITLE")
                .build();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

        // when
        ResponseEntity<String> responseEntity = restTemplate.
                postForEntity(url, dto, String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(folderCP, responseEntity.getBody());
    }

}