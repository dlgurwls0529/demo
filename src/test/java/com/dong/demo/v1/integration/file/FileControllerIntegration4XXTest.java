package com.dong.demo.v1.integration.file;

import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.FilesGenerateRequestDto;
import com.dong.demo.v1.web.dto.FilesModifyRequestDto;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.*;
import java.security.interfaces.RSAPublicKey;

public class FileControllerIntegration4XXTest extends IntegrationTestTemplate {

    @Test
    public void generateFile_BAD_REQUEST_BY_NO_MATCH_PARENT() throws Exception {
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

        /*
        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foldersGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        );
        */

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + Base58.encode(keyPair.getPublic().getEncoded()) + "/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        /* database check */
        Assertions.assertEquals(0, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(0, fileRepository.findAllOrderByLastChangedDate().size());

    }

    @Test
    public void generateFile_BAD_REQUEST_BY_INVALID_BYTESIGN() throws Exception {
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

        byte[] sign = new byte[]{1, 3, 5, 3};

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
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(0, fileRepository.findAllOrderByLastChangedDate().size());

    }

    // todo : X509 format TEST
    public void X509_generateFile_UNAUTHORIZED_BY_VERIFY_FAIL() throws Exception {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        KeyPair falsePair = CipherUtil.genRSAKeyPair();

        String folderCP = KeyCompressor.compress(
                Base58.encode(keyPair.getPublic().getEncoded())
        );

        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(falsePair.getPrivate());
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
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(0, fileRepository.findAllOrderByLastChangedDate().size());

    }

    @Test
    public void generateFile_UNAUTHORIZED_BY_VERIFY_FAIL() throws Exception {
        // given
        KeyPair keyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

        KeyPair falsePair = CipherUtil.genRSAKeyPair();
        RSAPublicKey falseRsaPublicKey = (RSAPublicKey) keyPair.getPublic();

        String folderCP = KeyCompressor.compress(
                rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent()
        );

        FoldersGenerateRequestDto foldersGenerateRequestDto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(falsePair.getPrivate());
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
                MockMvcRequestBuilders.post("/api/v1/folders/" + rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(0, fileRepository.findAllOrderByLastChangedDate().size());

    }

    @Test
    public void modifyFile_NO_CONTENT_BY_NO_SUCH_FILE() throws Exception {
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
                MockMvcRequestBuilders.post("/api/v1/folders/" + rsaPublicKey.getModulus() + "and" + rsaPublicKey.getPublicExponent() + "/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filesGenerateRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();

        fileRepository.deleteAll();

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
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(0, fileRepository.findAllOrderByLastChangedDate().size());

    }

    @Test
    public void modifyFile_BAD_REQUEST_BY_INVALID_BYTESIGN() throws Exception {
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
        signature.update(keyPair.getPublic().getEncoded());

        byte[] sign = signature.sign();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .byteSign(sign)
                .subhead("subhead_TEST")
                .build();

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .contents("contents_MODIFIED")
                .byteSign(new byte[]{1 ,3 ,4})
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
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(1, fileRepository.findAllOrderByLastChangedDate().size());
        Assertions.assertNotEquals("contents_MODIFIED", fileRepository.findContentsByFolderCPAndFileId(folderCP, fileId));
        Assertions.assertNotEquals("subhead_MODIFIED", fileRepository.findByFolderCP(folderCP).get(0).getSubheadEWS());

    }

    @Test
    public void modifyFile_UNAUTHORIZED_BY_VERIFY_FAIL() throws Exception {
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
        signature.update(keyPair.getPublic().getEncoded());

        byte[] sign = signature.sign();

        FilesGenerateRequestDto filesGenerateRequestDto = FilesGenerateRequestDto.builder()
                .byteSign(sign)
                .subhead("subhead_TEST")
                .build();

        Signature falseSignature = Signature.getInstance("SHA256withRSA");
        falseSignature.initSign(CipherUtil.genRSAKeyPair().getPrivate());
        falseSignature.update(keyPair.getPublic().getEncoded());

        byte[] falseSign = falseSignature.sign();

        FilesModifyRequestDto filesModifyRequestDto = FilesModifyRequestDto.builder()
                .contents("contents_MODIFIED")
                .byteSign(falseSign)
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
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());
        Assertions.assertEquals(1, fileRepository.findAllOrderByLastChangedDate().size());
        Assertions.assertNotEquals("contents_MODIFIED", fileRepository.findContentsByFolderCPAndFileId(folderCP, fileId));
        Assertions.assertNotEquals("subhead_MODIFIED", fileRepository.findByFolderCP(folderCP).get(0).getSubheadEWS());

    }


}
