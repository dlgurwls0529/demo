package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.UUIDGenerator;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.*;
import java.util.Base64;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilesApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @AfterEach
    @BeforeEach
    public void cleanUp() {

    }

    @Test
    public void generateFileSuccessTest() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());
        byte[] byteSign = signature.sign();

        FilesGenerateRequestDto requestDto = FilesGenerateRequestDto
                .builder()
                .byteSign(byteSign)
                .subhead("test")
                .build();

        String folderPublicKey = Base58.encode(publicKey.getEncoded());

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";

        // when
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(url, requestDto, String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals("uuid_TEST", responseEntity.getBody());
    }

    @Test
    public void generateFileFailByInvalidByteSignTest() throws NoSuchAlgorithmException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        FilesGenerateRequestDto requestDto = FilesGenerateRequestDto
                .builder()
                .byteSign(null)
                .subhead("test")
                .build();

        String folderPublicKey = Base58.encode(publicKey.getEncoded());

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";

        // when
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(url, requestDto, String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
            this.cleanUp();
        //
        // ****************************************************

        requestDto = FilesGenerateRequestDto
                .builder()
                .byteSign(new byte[]{})
                .subhead("test")
                .build();

        folderPublicKey = Base58.encode(publicKey.getEncoded());

        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";

        // when
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void generateFileFailByInvalidSubHeadTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());
        byte[] byteSign = signature.sign();

        FilesGenerateRequestDto requestDto = FilesGenerateRequestDto
                .builder()
                .byteSign(byteSign)
                .subhead(null)
                .build();

        String folderPublicKey = Base58.encode(publicKey.getEncoded());

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";

        // when
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(url, requestDto, String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        // given
        requestDto = FilesGenerateRequestDto
                .builder()
                .byteSign(byteSign)
                .subhead("    ")
                .build();

        folderPublicKey = Base58.encode(publicKey.getEncoded());

        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";

        // when
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void generateFileFailByInvalidPublicKeyTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());
        byte[] byteSign = signature.sign();

        FilesGenerateRequestDto requestDto = FilesGenerateRequestDto
                .builder()
                .byteSign(byteSign)
                .subhead("tasfsdas")
                .build();

        // 아마 Base64 는 슬래쉬 / 이거 들어가서 경로 못찾은듯. 디폴트 값인 404로 처리하면 될 것 같다.
        String folderPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        // 이거는 folders/files 이렇게 되어서 folder 컨트롤러에서 처리해준다.
        folderPublicKey = "";
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        Assertions.assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        // 얘네는 validator 로 걸러낸다.
        // 얘는 아마 Base58 Validator
        folderPublicKey = " ";
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        folderPublicKey = null;
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        folderPublicKey = "  3  dsfads   fd";
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************

        folderPublicKey = Base58.encode(privateKey.getEncoded());
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files";
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void modifyFileSuccessTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());
        byte[] byteSign = signature.sign();

        FilesModifyRequestDto dto = FilesModifyRequestDto
                .builder()
                .byteSign(byteSign)
                .subhead("subhead_TEST")
                .contents("contents_TEST")
                .build();

        String folderPublicKey = Base58.encode(publicKey.getEncoded());
        String fileId = UUIDGenerator.createUUIDWithoutHyphen();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;

        // when
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(fileId, responseEntity.getBody());
    }

    @Test
    public void modifyFileFailByInvalidByteSignTest() throws NoSuchAlgorithmException {
        // null
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        FilesModifyRequestDto dto = FilesModifyRequestDto
                .builder()
                .byteSign(null)
                .subhead("subhead_TEST")
                .contents("contents_TEST")
                .build();

        String folderPublicKey = Base58.encode(publicKey.getEncoded());
        String fileId = UUIDGenerator.createUUIDWithoutHyphen();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;

        // when
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        // empty
        // given
        dto = FilesModifyRequestDto
                .builder()
                .byteSign(new byte[]{})
                .subhead("subhead_TEST")
                .contents("contents_TEST")
                .build();

        folderPublicKey = Base58.encode(publicKey.getEncoded());
        fileId = UUIDGenerator.createUUIDWithoutHyphen();

        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;

        // when
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void modifyFileFailByInvalidSubheadTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // null
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(publicKey.getEncoded());
        byte[] byteSign = signature.sign();

        FilesModifyRequestDto dto = FilesModifyRequestDto
                .builder()
                .byteSign(byteSign)
                .subhead(null)
                .contents("contents_TEST")
                .build();

        String folderPublicKey = Base58.encode(publicKey.getEncoded());
        String fileId = UUIDGenerator.createUUIDWithoutHyphen();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;

        // when
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        // blank
        // given
        dto = FilesModifyRequestDto
                .builder()
                .byteSign(byteSign)
                .subhead("    ")
                .contents("contents_TEST")
                .build();

        folderPublicKey = Base58.encode(publicKey.getEncoded());
        fileId = UUIDGenerator.createUUIDWithoutHyphen();

        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;

        // when
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
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

    @Test
    public void getContentsByFileIdAndFolderCP() {
        // given
        String folderCP = "folderCP_TEST";
        String fileId = "folderCP_TEST";
        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP + "/files/" + fileId;

        // when
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(url, String.class, String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

}