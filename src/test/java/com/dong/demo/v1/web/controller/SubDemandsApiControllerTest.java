package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;
import java.awt.*;
import java.security.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubDemandsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @AfterEach
    @BeforeEach
    public void cleanUp() {

    }

    public void addSubscribeDemandSuccessTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());
        byte[] byteSign = signature.sign();

        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/add";

        SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .accountPublicKey(Base58.encode(accountKeyPair.getPublic().getEncoded()))
                .byteSign(byteSign)
                .build();

        // when
        ResponseEntity<Void> responseEntity =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void addSubscribeDemandFailByInvalidFolderCPTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());
        byte[] byteSign = signature.sign();

        List<String> ivFolderCPList = new ArrayList<String>(
                Arrays.asList(
                        null,
                        "   ",
                        "asjfklahsdjklahalfhsjaklfhsjkfjsdfal;fjksd;alfjksl;fskht3uihatu3wilhalhfiafh3uiwa3fhuwialhf3uiwlafuia"
                )
        );

        for (String folderCP : ivFolderCPList) {

            String url = "http://localhost:" + port + "/api/v1/subscribe-demands/add";

            SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                    .folderCP(folderCP)
                    .accountPublicKey(Base58.encode(accountKeyPair.getPublic().getEncoded()))
                    .byteSign(byteSign)
                    .build();

            // when
            ResponseEntity<Void> responseEntity =
                    restTemplate.postForEntity(url, dto, null);

            // then
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            // Test Isolation
            this.cleanUp();
        }
    }

    @Test
    public void addSubscribeDemandFailByInvalidAccountPublicKeyTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());
        byte[] byteSign = signature.sign();

        List<String> ivAccountPublicKeyList = new ArrayList<String>(
                Arrays.asList(
                        null,
                        "   ",
                        "jfdsklafjsafdhsgfhagsdfhasghfdfs",
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        Base64.getEncoder().encodeToString(CipherUtil.genRSAKeyPair().getPublic().getEncoded()),
                        Base64.getEncoder().encodeToString(CipherUtil.genRSAKeyPair().getPrivate().getEncoded())
                )
        );

        for (String ivAP : ivAccountPublicKeyList) {

            String url = "http://localhost:" + port + "/api/v1/subscribe-demands/add";

            SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                    .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                    .accountPublicKey(ivAP)
                    .byteSign(byteSign)
                    .build();

            // when
            ResponseEntity<Void> responseEntity =
                    restTemplate.postForEntity(url, dto, null);

            // then
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            // Test Isolation
            this.cleanUp();
        }
    }

    @Test
    public void addSubscribeDemandFailByInvalidByteSignTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());
        byte[] byteSign = signature.sign();

        List<byte[]> ivByteSignList = new ArrayList<byte[]>(
                Arrays.asList(
                        null,
                        new byte[]{}
                )
        );

        for (byte[] ivBS : ivByteSignList) {

            String url = "http://localhost:" + port + "/api/v1/subscribe-demands/add";

            SubscribeDemandsAddRequestDto dto = SubscribeDemandsAddRequestDto.builder()
                    .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                    .accountPublicKey(Base58.encode(accountKeyPair.getPublic().getEncoded()))
                    .byteSign(ivBS)
                    .build();

            // when
            ResponseEntity<Void> responseEntity =
                    restTemplate.postForEntity(url, dto, null);

            // then
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            // Test Isolation
            this.cleanUp();
        }
    }

    @Test
    public void allowSubscribeSuccessTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());

        byte[] byteSign = signature.sign();

        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/allow";

        SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                .folderPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                .byteSign(byteSign)
                .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                .symmetricKeyEWA("symmetricKeyEWA_TEST")
                .build();

        // when
        ResponseEntity<Void> responseEntity =
                restTemplate.postForEntity(url, dto, null);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void allowSubscribeFailByInvalidFolderPublicKeyTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());

        byte[] byteSign = signature.sign();

        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/allow";

        List<String> ivFolderPublicKeyList = new ArrayList<String>(
                Arrays.asList(
                        null,
                        "   ",
                        "jfdsklafjsafdhsgfhagsdfhasghfdfs",
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        Base64.getEncoder().encodeToString(folderKeyPair.getPublic().getEncoded()),
                        Base64.getEncoder().encodeToString(folderKeyPair.getPrivate().getEncoded())
                )
        );

        for (String inFP : ivFolderPublicKeyList) {
            SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                    .folderPublicKey(inFP)
                    .byteSign(byteSign)
                    .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                    .symmetricKeyEWA("symmetricKeyEWA_TEST")
                    .build();

            // when
            ResponseEntity<Void> responseEntity =
                    restTemplate.postForEntity(url, dto, null);

            // then
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        }
    }

    @Test
    public void allowSubscribeFailByInvalidByteSignTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());

        byte[] byteSign = signature.sign();

        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/allow";

        List<byte[]> ivByteSignList = new ArrayList<byte[]>(
                Arrays.asList(
                        null,
                        new byte[]{}
                )
        );

        for (byte[] ivBS : ivByteSignList) {
            SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                    .folderPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                    .byteSign(ivBS)
                    .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                    .symmetricKeyEWA("symmetricKeyEWA_TEST")
                    .build();

            // when
            ResponseEntity<Void> responseEntity =
                    restTemplate.postForEntity(url, dto, null);

            // then
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        }
    }

    @Test
    public void allowSubscribeFailByInvalidAccountCPTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());

        byte[] byteSign = signature.sign();

        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/allow";

        List<String> ivAccountCPList = new ArrayList<String>(
                Arrays.asList(
                        null,
                        "   ",
                        "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij"
                )
        );

        for (String ivAccountCP : ivAccountCPList) {
            SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                    .folderPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                    .byteSign(byteSign)
                    .accountCP(ivAccountCP)
                    .symmetricKeyEWA("symmetricKeyEWA_TEST")
                    .build();

            // when
            ResponseEntity<Void> responseEntity =
                    restTemplate.postForEntity(url, dto, null);

            // then
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        }
    }

    @Test
    public void allowSubscribeFailByInvalidSymTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());

        byte[] byteSign = signature.sign();

        String url = "http://localhost:" + port + "/api/v1/subscribe-demands/allow";

        List<String> ivSymEWAList = new ArrayList<String>(
                Arrays.asList(
                        null,
                        "   "
                )
        );

        for (String symEWA : ivSymEWAList) {
            SubscribeDemandsAllowRequestDto dto = SubscribeDemandsAllowRequestDto.builder()
                    .folderPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                    .byteSign(byteSign)
                    .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                    .symmetricKeyEWA(symEWA)
                    .build();

            // when
            ResponseEntity<Void> responseEntity =
                    restTemplate.postForEntity(url, dto, null);

            // then
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        }
    }

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