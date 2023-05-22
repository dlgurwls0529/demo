package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.dong.demo.v1.web.dto.FoldersSearchResponseDto;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// @WebMvcTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FoldersApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @AfterEach
    @BeforeEach
    public void cleanUp() {

    }

    @Test
    public void generateFolderSuccessTest() {
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

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        folderCP = "files";

        url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

        responseEntity = restTemplate.
                postForEntity(url, dto, String.class);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void generateFolderFailByInvalidSymTest() {

        String folderCP = "fsafdsafsas";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF(null)
                .title("test_TITLE")
                .build();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

        ResponseEntity<String> responseEntity = restTemplate.
                postForEntity(url, dto, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void generateFolderFailByInvalidTitleTest() {

        String folderCP = "fsafdsafsas";

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title(null)
                .build();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

        ResponseEntity<String> responseEntity = restTemplate.
                postForEntity(url, dto, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("   ")
                .build();

        url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

        responseEntity = restTemplate.
                postForEntity(url, dto, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void generateFolderFailByInvalidFolderCPTest() {

        String folderCP = null;

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

        ResponseEntity<String> responseEntity = restTemplate.
                postForEntity(url, dto, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        folderCP = "    ";

        url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

        responseEntity = restTemplate.
                postForEntity(url, dto, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void searchSuccessTest() {
        // given
        String keyword = "keyword_TEST";
        String url = "http://localhost:" + port + "/api/v1/folders?keyword=" + keyword;

        // when
        ResponseEntity<List> response = restTemplate
                .getForEntity(url, List.class, keyword);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }
}