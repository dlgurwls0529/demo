package com.dong.demo.v1.integration.auth;

import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class AuthControllerIntegration4XXTest extends IntegrationTestTemplate {

    // 키 형식은 올바르나, 다른 키를 압축해서 CP 로 보낸 경우.
    @Test
    public void addWriteAuth_BAD_REQUEST_BY_UNMATHED_CP() throws Exception {
        // given
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();

        // parent insertion
        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + KeyCompressor.compress(
                                Base58.encode(folderKeyPair.getPublic().getEncoded())
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        );
        //

        // child insertion
        WriteAuthsAddRequestDto writeAuthsAddRequestDto = WriteAuthsAddRequestDto.builder()
                .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .folderPublicKey(Base58.encode(accountKeyPair.getPublic().getEncoded()))
                .folderPrivateKeyEWA("EWA_TEST")
                .build();

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/v1/write-auths")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeAuthsAddRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // db test
        Assertions.assertEquals(0, writeAuthRepository.findByAccountCP(writeAuthsAddRequestDto.getAccountCP()).size());
    }

    @Test
    public void addWriteAuth_BAD_REQUEST_BY_DUPLICATE() throws Exception {
        // given
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();

        // parent insertion
        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + KeyCompressor.compress(
                                Base58.encode(folderKeyPair.getPublic().getEncoded())
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        );
        //

        // child insertion
        WriteAuthsAddRequestDto writeAuthsAddRequestDto = WriteAuthsAddRequestDto.builder()
                .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .folderPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                .folderPrivateKeyEWA("EWA_TEST")
                .build();

        // first
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/write-auths")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeAuthsAddRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        );

        // when, second : duplicate
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/v1/write-auths")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeAuthsAddRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // db test
        Assertions.assertEquals(1, writeAuthRepository.findByAccountCP(writeAuthsAddRequestDto.getAccountCP()).size());
    }

    @Test
    public void addWriteAuth_BAD_REQUEST_BY_NO_CHILD() throws Exception {
        // given
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();

        // child insertion
        WriteAuthsAddRequestDto writeAuthsAddRequestDto = WriteAuthsAddRequestDto.builder()
                .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .folderPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                .folderPrivateKeyEWA("EWA_TEST")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/write-auths")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeAuthsAddRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        );

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/v1/write-auths")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(writeAuthsAddRequestDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // db test
        Assertions.assertEquals(0, writeAuthRepository.findByAccountCP(writeAuthsAddRequestDto.getAccountCP()).size());
    }
}
