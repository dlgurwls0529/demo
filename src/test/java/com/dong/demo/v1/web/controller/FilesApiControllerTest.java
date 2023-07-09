package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.domain.file.FileRepository;
import com.dong.demo.v1.domain.folder.FolderRepository;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.util.UUIDGenerator;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
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
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.security.*;
import java.util.Base64;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilesApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FileRepository fileRepository;

    @AfterEach
    @BeforeEach
    public void cleanUp() {

    }

    // 성공 테스트는 어차피 통합 테스트에서 하니까 뻈다. 이거 환경 바꾸면 다 성공하기는 하는데,
    // 그러면 컨트롤러 테스트가 아니게 되니까. 검증 등에 의한 실패 테스트는 다 잘 돌아간다.

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

        // Validation 하면서 DTO 에 대한 검사도 수행하다보니 Bad Request 가 뜨는 모양
        // 그러니까, folders/files 가 folder 컨트롤러에 매핑되는 건 맞는데, Validation 과정에서 걸러진다.
        // 근데 혹시라도 files 로 접근했으면 세밀하게 응답 해주는 게 좋으니까
        // files 이고 Validation 까지 fail 했어야 CONFLICT 가 뜨게 했다. 그냥 Validation 만 실패하면 Bad Request 가 뜬다.

        // * 우선 FileGenDTO 의 JSON 과 FolderGenDTO 의 JSON 필드 이름이 겹치는 게 없다.
        // * 그래서 folder 컨트롤러에 전달되는 FolderGenDTO 는 전부 null 혹은 기본 값이 세팅되어있다.
        // * 이런 식으로 세팅 된 후 Validation 을 수행하면, Validation Fail 로 인해 Bad Request 가 된다.
        // * 아무튼 Validation 덕에 folder 로 매핑되는 문제는 생각 안해도 되지만
        // * 혹시라도 Valid 를 빼면 위험하니 검사 로직은 넣었다.

        // Validation Fail By api/v1/folders/files : generateFolder()
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
    public void modifyFileFailByInvalidContentsTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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
                .subhead("subhead_TEST")
                .contents(null)
                .build();

        String folderPublicKey = Base58.encode(publicKey.getEncoded());
        String fileId = UUIDGenerator.createUUIDWithoutHyphen();

        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;

        // when
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void modifyFileFailByInvalidPublicKeyTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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

        String folderPublicKey = Base58.encode(privateKey.getEncoded());
        String fileId = UUIDGenerator.createUUIDWithoutHyphen();
        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        // 얘도 위에꺼랑 마찬가지로 PUT : api/v1/folders/files 이거라서 NOT FOUND 뜬다.
        folderPublicKey = "";
        fileId = UUIDGenerator.createUUIDWithoutHyphen();
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        folderPublicKey = "fdsaf sfasdf fdsaf";
        fileId = UUIDGenerator.createUUIDWithoutHyphen();
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        folderPublicKey = "         ";
        fileId = UUIDGenerator.createUUIDWithoutHyphen();
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        folderPublicKey = " fdsafsf/ fdsf/sd/f";
        fileId = UUIDGenerator.createUUIDWithoutHyphen();
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        folderPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        fileId = UUIDGenerator.createUUIDWithoutHyphen();
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void modifyFileFailByInvalidUUIDTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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
        String fileId = "------------------";
        String url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        // url 경로에 아무것도 없으면 그냥 404로 처리되나보다.
        folderPublicKey = Base58.encode(publicKey.getEncoded());
        fileId = "";
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        folderPublicKey = Base58.encode(publicKey.getEncoded());
        fileId = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaf8979aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());


        // ****************** TEST ISOLATION ******************
        //
        this.cleanUp();
        //
        // ****************************************************


        folderPublicKey = Base58.encode(publicKey.getEncoded());
        fileId = "aaaaaaaaa/aaaa/aaaaaa";
        url = "http://localhost:" + port + "/api/v1/folders/" + folderPublicKey + "/files/" + fileId;
        responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


    public void getFileByFolderCPStubTest() throws NoSuchAlgorithmException {
        // given
        String folderCP = KeyCompressor.compress(Base58.encode(CipherUtil.genRSAKeyPair().getPublic().getEncoded()));
        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP + "/files";

        // when
        ResponseEntity<List> responseEntity =
                restTemplate.getForEntity(url, List.class, String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    public void getContentsByFileIdAndFolderCPSuccessTest() throws NoSuchAlgorithmException {
        // given
        String folderCP = KeyCompressor.compress(Base58.encode(CipherUtil.genRSAKeyPair().getPublic().getEncoded()));
        String fileId = UUIDGenerator.createUUIDWithoutHyphen();
        String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP + "/files/" + fileId;

        // when
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(url, String.class, String.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

}