package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsGetResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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

    @AfterEach
    @BeforeEach
    public void cleanUp() {

    }

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
    public void addWriteAuthoritySuccessTest() throws NoSuchAlgorithmException {
        // given
        String url = "http://localhost:" + port + "/api/v1/write-auths";

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        String accountCP = KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()));

        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        String folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        String folderCP = KeyCompressor.compress(folderPublicKey);

        WriteAuthsAddRequestDto dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP(folderCP)
                .folderPublicKey(folderPublicKey)
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        ResponseEntity<Void> response =
                restTemplate.postForEntity(url, dto, null);

        System.out.println(response.getBody());

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addWriteAuthorityFailByInvalidAccountCPTest() throws NoSuchAlgorithmException {
        // given
        String url = "http://localhost:" + port + "/api/v1/write-auths";

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        String accountCP = KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()));

        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        String folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        String folderCP = KeyCompressor.compress(folderPublicKey);

        WriteAuthsAddRequestDto dto = WriteAuthsAddRequestDto.builder()
                .accountCP(null)
                .folderCP(folderCP)
                .folderPublicKey(folderPublicKey)
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        ResponseEntity<Void> response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());



        // ******************* TEST ISOLATION *******************

        this.cleanUp();

        // ******************************************************



        // given
        url = "http://localhost:" + port + "/api/v1/write-auths";

        accountKeyPair = CipherUtil.genRSAKeyPair();
        accountCP = KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()));

        folderKeyPair = CipherUtil.genRSAKeyPair();
        folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        folderCP = KeyCompressor.compress(folderPublicKey);

        dto = WriteAuthsAddRequestDto.builder()
                .accountCP("   ")
                .folderCP(folderCP)
                .folderPublicKey(folderPublicKey)
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addWriteAuthorityFailByInvalidFolderCPTest() throws NoSuchAlgorithmException {
        // given
        String url = "http://localhost:" + port + "/api/v1/write-auths";

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        String accountCP = KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()));

        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        String folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        String folderCP = KeyCompressor.compress(folderPublicKey);

        WriteAuthsAddRequestDto dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP(null)
                .folderPublicKey(folderPublicKey)
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        ResponseEntity<Void> response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());



        // ******************* TEST ISOLATION *******************

        this.cleanUp();

        // ******************************************************



        // given
        url = "http://localhost:" + port + "/api/v1/write-auths";

        accountKeyPair = CipherUtil.genRSAKeyPair();
        accountCP = KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()));

        folderKeyPair = CipherUtil.genRSAKeyPair();
        folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        folderCP = KeyCompressor.compress(folderPublicKey);

        dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP("   ")
                .folderPublicKey(folderPublicKey)
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addWriteAuthorityFailByInvalidFolderPublicKeyTest() throws NoSuchAlgorithmException {
        // given
        String url = "http://localhost:" + port + "/api/v1/write-auths";

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        String accountPublicKey = Base58.encode(accountKeyPair.getPublic().getEncoded());
        String accountCP = KeyCompressor.compress(accountPublicKey);

        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        String folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        String folderCP = KeyCompressor.compress(folderPublicKey);

        WriteAuthsAddRequestDto dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP(folderCP)
                .folderPublicKey(null)
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        ResponseEntity<Void> response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());



        // ******************* TEST ISOLATION *******************

        this.cleanUp();

        // ******************************************************



        // given
        url = "http://localhost:" + port + "/api/v1/write-auths";

        accountKeyPair = CipherUtil.genRSAKeyPair();
        accountPublicKey = Base58.encode(accountKeyPair.getPublic().getEncoded());
        accountCP = KeyCompressor.compress(accountPublicKey);

        folderKeyPair = CipherUtil.genRSAKeyPair();
        folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        folderCP = KeyCompressor.compress(folderPublicKey);

        dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP(folderCP)
                .folderPublicKey("   ")
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());



        // ******************* TEST ISOLATION *******************

        this.cleanUp();

        // ******************************************************



        // given
        url = "http://localhost:" + port + "/api/v1/write-auths";

        accountKeyPair = CipherUtil.genRSAKeyPair();
        accountPublicKey = Base58.encode(accountKeyPair.getPublic().getEncoded());
        accountCP = KeyCompressor.compress(accountPublicKey);

        folderKeyPair = CipherUtil.genRSAKeyPair();
        folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        folderCP = KeyCompressor.compress(folderPublicKey);

        dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP(folderCP)
                .folderPublicKey(accountPublicKey)
                .folderPrivateKeyEWA("folderPrivateKey_TEST")
                .build();

        // when
        response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addWriteAuthorityFailByInvalidEWATest() throws NoSuchAlgorithmException {
        // given
        String url = "http://localhost:" + port + "/api/v1/write-auths";

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        String accountCP = KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()));

        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        String folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        String folderCP = KeyCompressor.compress(folderPublicKey);

        WriteAuthsAddRequestDto dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP(folderCP)
                .folderPublicKey(folderPublicKey)
                .folderPrivateKeyEWA(null)
                .build();

        // when
        ResponseEntity<Void> response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());



        // ******************* TEST ISOLATION *******************

        this.cleanUp();

        // ******************************************************



        // given
        url = "http://localhost:" + port + "/api/v1/write-auths";

        accountKeyPair = CipherUtil.genRSAKeyPair();
        accountCP = KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()));

        folderKeyPair = CipherUtil.genRSAKeyPair();
        folderPublicKey = Base58.encode(folderKeyPair.getPublic().getEncoded());
        folderCP = KeyCompressor.compress(folderPublicKey);

        dto = WriteAuthsAddRequestDto.builder()
                .accountCP(accountCP)
                .folderCP(folderCP)
                .folderPublicKey(folderPublicKey)
                .folderPrivateKeyEWA("  ")
                .build();

        // when
        response =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}