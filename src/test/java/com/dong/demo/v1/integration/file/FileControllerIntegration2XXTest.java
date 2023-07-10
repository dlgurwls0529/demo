package com.dong.demo.v1.integration.file;

import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.util.RSAVerifier;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileControllerIntegration2XXTest extends IntegrationTestTemplate {

    // todo : X509 format TEST
    public void X509_integration_test_generate_input_normal() throws Exception {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();

        String folderCP = KeyCompressor.compress(
                Base58.encode(keyPair.getPublic().getEncoded())
        );

        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(keyPair.getPublic().getEncoded());

        byte[] sign = signature.sign();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .byteSign(sign)
                .subhead("subhead_TEST")
                .build();

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + Base58.encode(keyPair.getPublic().getEncoded()) + "/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(1, fileRepository.findAllOrderByLastChangedDate().size());

    }

    @Test
    public void integration_test_generate_input_normal() throws Exception {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

        String folderCP = KeyCompressor.compress(
                rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent()
        );

        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(RSAVerifier.SIGN_MESSAGE);

        byte[] sign = signature.sign();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .byteSign(sign)
                .subhead("subhead_TEST")
                .build();

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(1, fileRepository.findAllOrderByLastChangedDate().size());

    }

    // todo : X509 format TEST
    public void X509_integration_test_modify_input_normal() throws Exception {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();

        String folderCP = KeyCompressor.compress(
                Base58.encode(keyPair.getPublic().getEncoded())
        );

        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(keyPair.getPublic().getEncoded());

        byte[] sign = signature.sign();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .byteSign(sign)
                .subhead("subhead_TEST")
                .build();

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .contents("contents_MODIFIED")
                .byteSign(sign)
                .subhead("subhead_MODIFIED")
                .build();

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        );

        String fileId = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + Base58.encode(keyPair.getPublic().getEncoded()) + "/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.put("/api/v1/folders/" +
                        Base58.encode(keyPair.getPublic().getEncoded()) +
                        "/files/" + fileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesModifyRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(1, fileRepository.findAllOrderByLastChangedDate().size());
        Assertions.assertEquals("contents_MODIFIED", fileRepository.findContentsByFolderCPAndFileId(folderCP, fileId));
        Assertions.assertEquals("subhead_MODIFIED", fileRepository.findByFolderCP(folderCP).get(0).getSubheadEWS());

    }

    @Test
    public void integration_test_modify_input_normal() throws Exception {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

        String folderCP = KeyCompressor.compress(
                rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent()
        );

        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(RSAVerifier.SIGN_MESSAGE);

        byte[] sign = signature.sign();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .byteSign(sign)
                .subhead("subhead_TEST")
                .build();

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .contents("contents_MODIFIED")
                .byteSign(sign)
                .subhead("subhead_MODIFIED")
                .build();

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        );

        String fileId = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.put("/api/v1/folders/" +
                                rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() +
                                "/files/" + fileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesModifyRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(1, fileRepository.findAllOrderByLastChangedDate().size());
        Assertions.assertEquals("contents_MODIFIED", fileRepository.findContentsByFolderCPAndFileId(folderCP, fileId));
        Assertions.assertEquals("subhead_MODIFIED", fileRepository.findByFolderCP(folderCP).get(0).getSubheadEWS());

    }

    // todo : X509 format TEST
    public void X509_integration_test_get_file_input_normal() throws Exception {
        // given
        List<String> folderCP_LIST = new ArrayList<String>();

        for (int i = 0; i < 5; i++) {
            KeyPair keyPair = CipherUtil.genRSAKeyPair();

            String folderCP = KeyCompressor.compress(
                    Base58.encode(keyPair.getPublic().getEncoded())
            );

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(keyPair.getPublic().getEncoded());
            byte[] byteSign = signature.sign();

            FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .title("title_TEST")
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                            .accept(MediaType.APPLICATION_JSON)
            );

            FilesGenerateRequestDto filesGenerateRequestDto1 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_1_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                                    Base58.encode(keyPair.getPublic().getEncoded()) + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto1))
                            .accept(MediaType.APPLICATION_JSON)
            );

            FilesGenerateRequestDto filesGenerateRequestDto2 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_2_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                                    Base58.encode(keyPair.getPublic().getEncoded()) + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto2))
                            .accept(MediaType.APPLICATION_JSON)
            );

            folderCP_LIST.add(folderCP);
        }

        // when, then
        for (String folderCP : folderCP_LIST) {
            ResultActions resultActions = mvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/folders/" + folderCP + "/files")
            );

            resultActions
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$[*].subheadEWS")
                            .value(Matchers.hasItem("subhead_1_" + folderCP)))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$[*].subheadEWS")
                            .value(Matchers.hasItem("subhead_2_" + folderCP)));
        }
    }

    @Test
    public void integration_test_get_file_input_normal() throws Exception {
        // given
        List<String> folderCP_LIST = new ArrayList<String>();

        for (int i = 0; i < 5; i++) {
            KeyPair keyPair = CipherUtil.genRSAKeyPair();
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

            String folderCP = KeyCompressor.compress(
                    rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent()
            );

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(RSAVerifier.SIGN_MESSAGE);
            byte[] byteSign = signature.sign();

            FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .title("title_TEST")
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                            .accept(MediaType.APPLICATION_JSON)
            );

            FilesGenerateRequestDto filesGenerateRequestDto1 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_1_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                            rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto1))
                            .accept(MediaType.APPLICATION_JSON)
            );

            FilesGenerateRequestDto filesGenerateRequestDto2 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_2_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                                    rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto2))
                            .accept(MediaType.APPLICATION_JSON)
            );

            folderCP_LIST.add(folderCP);
        }

        // when, then
        for (String folderCP : folderCP_LIST) {
            ResultActions resultActions = mvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/folders/" + folderCP + "/files")
            );

            resultActions
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$[*].subheadEWS")
                            .value(Matchers.hasItem("subhead_1_" + folderCP)))
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$[*].subheadEWS")
                            .value(Matchers.hasItem("subhead_2_" + folderCP)));
        }
    }

    // todo : X509 format TEST
    public void X509_integration_test_get_content_input_normal() throws Exception {
        // given
        String expect_folderCP = null;
        String expect_fileId = null;
        String expect_contents = "content_MODIFIED";
        Semaphore semaphore = new Semaphore(0);

        for (int i = 0; i < 5; i++) {
            KeyPair keyPair = CipherUtil.genRSAKeyPair();

            String folderCP = KeyCompressor.compress(
                    Base58.encode(keyPair.getPublic().getEncoded())
            );

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(keyPair.getPublic().getEncoded());
            byte[] byteSign = signature.sign();

            FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .title("title_TEST")
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(MockMvcResultHandlers.print());

            FilesGenerateRequestDto filesGenerateRequestDto1 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_1_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            String fileId = mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                                    Base58.encode(keyPair.getPublic().getEncoded()) + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto1))
                            .accept(MediaType.APPLICATION_JSON)
            ).andReturn().getResponse().getContentAsString();

            FilesGenerateRequestDto filesGenerateRequestDto2 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_2_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                                    Base58.encode(keyPair.getPublic().getEncoded()) + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto2))
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(MockMvcResultHandlers.print());;

            if (i == 3) {
                expect_folderCP = folderCP;
                expect_fileId = fileId;

                FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                        .subhead("subhead_MODIFIED")
                        .byteSign(byteSign)
                        .contents("content_MODIFIED")
                        .build();

                mvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/folders/" + Base58.encode(keyPair.getPublic().getEncoded()) + "/files/" + expect_fileId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filesModifyRequestDto))
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print());
            }
        }



        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.get("/api/v1/folders/" + expect_folderCP + "/files/" + expect_fileId)

        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expect_contents));

    }

    @Test
    public void integration_test_get_content_input_normal() throws Exception {
        // given
        String expect_folderCP = null;
        String expect_fileId = null;
        String expect_contents = "content_MODIFIED";
        Semaphore semaphore = new Semaphore(0);

        for (int i = 0; i < 5; i++) {
            KeyPair keyPair = CipherUtil.genRSAKeyPair();
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

            String folderCP = KeyCompressor.compress(
                    rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent()
            );

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(RSAVerifier.SIGN_MESSAGE);
            byte[] byteSign = signature.sign();

            FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .title("title_TEST")
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(MockMvcResultHandlers.print());

            FilesGenerateRequestDto filesGenerateRequestDto1 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_1_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            String fileId = mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                                    rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto1))
                            .accept(MediaType.APPLICATION_JSON)
            ).andReturn().getResponse().getContentAsString();

            FilesGenerateRequestDto filesGenerateRequestDto2 = FilesGenerateRequestDto.builder()
                    .subhead("subhead_2_" + folderCP)
                    .byteSign(byteSign)
                    .build();

            mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" +
                                    rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filesGenerateRequestDto2))
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(MockMvcResultHandlers.print());;

            if (i == 3) {
                expect_folderCP = folderCP;
                expect_fileId = fileId;

                FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                        .subhead("subhead_MODIFIED")
                        .byteSign(byteSign)
                        .contents("content_MODIFIED")
                        .build();

                mvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/folders/" + rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files/" + expect_fileId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filesModifyRequestDto))
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print());
            }
        }



        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.get("/api/v1/folders/" + expect_folderCP + "/files/" + expect_fileId)

        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expect_contents));

    }
}
