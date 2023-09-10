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

    // *****************************************************************************
    // DB가 H2라서 문법 호환이 안되는 듯. 이미 통합 테스트에서 커버해서, 테스트 대상에서 제외했다.
    // *****************************************************************************

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

    // 이거 폴더 아예 없으면, 예외 떠도 바로 리턴해서, 테스트는 잘 돌아갈 수 있다.
    // 뭔 소리냐면, 빈 값 넣고, DB에 폴더 아무것도 없게 하면, DB 비어서 서비스에서 바로 리턴해버리니까
    // 빈 값이 키워드로 들어가도 그냥 잘 돌아가는 것처럼 보이는 것이다.
    // 폴더 넣어서도 테스트 해보든가, 그냥 막든가

    // *****************************************************************************
    // DB가 H2라서 문법 호환이 안되는 듯. 이미 통합 테스트에서 커버해서, 테스트 대상에서 제외했다.
    // *****************************************************************************

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