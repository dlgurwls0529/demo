package com.dong.demo.v1.integration.auth;

import com.dong.demo.v1.domain.readAuth.ReadAuth;
import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.dong.demo.v1.web.dto.WriteAuthsAddRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

public class AuthControllerIntegration2XXTest extends IntegrationTestTemplate {

    @Test
    public void getWriteAuthByAccountCP_OK_test() throws Exception {
        // given
        KeyPair targetAccountKeyPair = CipherUtil.genRSAKeyPair();
        String encodedTargetAccountPublicKey =
                        ((RSAPublicKey)targetAccountKeyPair.getPublic()).getModulus()
                        + "and"
                        + ((RSAPublicKey)targetAccountKeyPair.getPublic()).getPublicExponent();
        KeyPair nonTargetAccountKeyPair = CipherUtil.genRSAKeyPair();
        String encodedNonTargetAccountPublicKey =
                        ((RSAPublicKey)nonTargetAccountKeyPair.getPublic()).getModulus()
                        + "and"
                        + ((RSAPublicKey)nonTargetAccountKeyPair.getPublic()).getPublicExponent();

        for (int i = 0; i < 5; i++) {
            // no duplicate
            KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
            String encodedFolderPublicKey =
                            ((RSAPublicKey)folderKeyPair.getPublic()).getModulus() +
                            "and" +
                            ((RSAPublicKey)folderKeyPair.getPublic()).getPublicExponent();

            // gen parent folder
            FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .title("title_TEST")
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" + KeyCompressor.compress(
                                    encodedFolderPublicKey
                            ))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                            .accept(MediaType.APPLICATION_JSON)
            );
            //

            // insert child
            WriteAuthsAddRequestDto writeAuthsAddRequestDto = WriteAuthsAddRequestDto.builder()
                    .accountCP(
                            i % 2 == 0
                                    ? KeyCompressor.compress(encodedTargetAccountPublicKey)
                                    : KeyCompressor.compress(encodedNonTargetAccountPublicKey)
                    )
                    .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                    .folderPublicKey(encodedFolderPublicKey)
                    .folderPrivateKeyEWA("EWA_TEST")
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/write-auths")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(writeAuthsAddRequestDto))
                            .accept(MediaType.APPLICATION_JSON)
            );
        }

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.get("/api/v1/write-auths/" +
                        KeyCompressor.compress(encodedTargetAccountPublicKey)
                        + "/folders")
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    public void getReadAuthByAccountCP_OK_test() throws Exception {
        // given
        KeyPair targetAccountKeyPair = CipherUtil.genRSAKeyPair();
        String encodedTargetAccountPublicKey =
                ((RSAPublicKey)targetAccountKeyPair.getPublic()).getModulus()
                        + "and"
                        + ((RSAPublicKey)targetAccountKeyPair.getPublic()).getPublicExponent();
        KeyPair nonTargetAccountKeyPair = CipherUtil.genRSAKeyPair();
        String encodedNonTargetAccountPublicKey =
                ((RSAPublicKey)nonTargetAccountKeyPair.getPublic()).getModulus()
                        + "and"
                        + ((RSAPublicKey)nonTargetAccountKeyPair.getPublic()).getPublicExponent();

        for (int i = 0; i < 5; i++) {
            // no duplicate
            KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
            String encodedFolderPublicKey =
                    ((RSAPublicKey)folderKeyPair.getPublic()).getModulus() +
                            "and" +
                            ((RSAPublicKey)folderKeyPair.getPublic()).getPublicExponent();

            // gen parent folder
            FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .title("title_TEST")
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" + KeyCompressor.compress(
                                    encodedFolderPublicKey
                            ))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                            .accept(MediaType.APPLICATION_JSON)
            );
            //

            // insert child
            ReadAuth readAuth = ReadAuth.builder()
                    .accountCP(
                            i % 2 == 0
                                    ? KeyCompressor.compress(encodedTargetAccountPublicKey)
                                    : KeyCompressor.compress(encodedNonTargetAccountPublicKey)
                    )
                    .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                    .symmetricKeyEWA("EWA_TEST")
                    .build();

            readAuthRepository.save(readAuth);
        }

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.get("/api/v1/read-auths/" +
                        KeyCompressor.compress(encodedTargetAccountPublicKey)
                        + "/folders")
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));
    }

    // 위에서 테스트 함.
    public void addWriteAuthority_OK_test() {

    }
}
