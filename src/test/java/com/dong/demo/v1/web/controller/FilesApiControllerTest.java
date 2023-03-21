package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilesApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void generateFileStubTest() {
        // given
        FilesGenerateRequestDto requestDto = FilesGenerateRequestDto
                .builder()
                .byteSign(new byte[]{1, 2, 4})
                .subhead("subhead_TEST")
                .build();

        String folderPublicKey = "folderPublicKey_TEST";

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";

        // when
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(url, requestDto, String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals("uuid_TEST", responseEntity.getBody());
    }

    @Test
    public void modifyFileStubTest() {
        // given
        FilesModifyRequestDto dto = FilesModifyRequestDto
                .builder()
                .byteSign(new byte[]{1, 2, 3})
                .subhead("subhead_TEST")
                .contents("contents_TEST")
                .build();

        String folderPublicKey = "folderPublicKey_TEST";
        String fileId = "fileId_TEST";

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;

        // when
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(fileId, responseEntity.getBody());
    }

    @Test
    public void getFileByFolderCPStubTest() {
        // given
        String folderCP = "folderCP_TEST";
        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP + "/files";

        // when
        ResponseEntity<List> responseEntity =
                restTemplate.getForEntity(url, List.class, String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(2, responseEntity.getBody().size());
        Assertions.assertEquals(responseEntity.getBody().get(0), responseEntity.getBody().get(1));
    }

}