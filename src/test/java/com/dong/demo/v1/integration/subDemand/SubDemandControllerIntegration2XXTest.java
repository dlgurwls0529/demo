package com.dong.demo.v1.integration.subDemand;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.subDemand.SubDemand;
import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.*;
import com.dong.demo.v1.web.dto.SubscribeDemandsAddRequestDto;
import com.dong.demo.v1.web.dto.SubscribeDemandsAllowRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.swing.text.AbstractDocument;
import java.security.*;
import java.security.interfaces.RSAPublicKey;

public class SubDemandControllerIntegration2XXTest extends IntegrationTestTemplate {

    @Test
    public void addSubscribeDemand2XXTest() throws Exception {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaFolderPublicKey = (RSAPublicKey) folderKeyPair.getPublic();
        String encodedFolderPublicKey = rsaFolderPublicKey.getModulus() + "and" + rsaFolderPublicKey.getPublicExponent();

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaAccountPublicKey = (RSAPublicKey) accountKeyPair.getPublic();
        String encodedAccountPublicKey = rsaAccountPublicKey.getModulus() + "and" + rsaAccountPublicKey.getPublicExponent();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(accountKeyPair.getPrivate());
        signature.update(RSAVerifier.SIGN_MESSAGE);
        byte[] byteSign = signature.sign();

        SubscribeDemandsAddRequestDto subscribeDemandsAddRequestDto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(encodedAccountPublicKey)
                .byteSign(byteSign)
                .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                .build();

        // insert parent
        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/subscribe-demands/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscribeDemandsAddRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        // db test
        Assertions.assertEquals(1,
                subDemandRepository.findAccountPublicKeyByFolderCP(
                subscribeDemandsAddRequestDto.getFolderCP()).size()
        );
    }

    @Test
    public void allowSubscribeDemand2XXTest() throws Exception {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaFolderPublicKey = (RSAPublicKey) folderKeyPair.getPublic();
        String encodedFolderPublicKey = rsaFolderPublicKey.getModulus() + "and" + rsaFolderPublicKey.getPublicExponent();

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaAccountPublicKey = (RSAPublicKey) accountKeyPair.getPublic();
        String encodedAccountPublicKey = rsaAccountPublicKey.getModulus() + "and" + rsaAccountPublicKey.getPublicExponent();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(RSAVerifier.SIGN_MESSAGE);
        byte[] byteSign = signature.sign();

        SubscribeDemandsAllowRequestDto subscribeDemandsAllowRequestDto = SubscribeDemandsAllowRequestDto.builder()
                .accountCP(KeyCompressor.compress(
                        encodedAccountPublicKey
                ))
                .folderPublicKey(encodedFolderPublicKey)
                .byteSign(byteSign)
                .symmetricKeyEWA("sym_TEST")
                .build();

        // insert parent
        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        subDemandRepository.save(SubDemand.builder()
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .accountPublicKey(encodedAccountPublicKey)
                .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                .build()
        );

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/subscribe-demands/allow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscribeDemandsAllowRequestDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        // db test
        Assertions.assertEquals(0,
                subDemandRepository.findAccountPublicKeyByFolderCP(
                        KeyCompressor.compress(encodedFolderPublicKey)).size()
        );

        Assertions.assertEquals(1,
                readAuthRepository.findByAccountCP(
                        KeyCompressor.compress(encodedAccountPublicKey)).size()
        );
    }

    @Test
    public void getSubscribeDemand2XXTest() throws Exception {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaFolderPublicKey = (RSAPublicKey) folderKeyPair.getPublic();
        String encodedFolderPublicKey = rsaFolderPublicKey.getModulus() + "and" + rsaFolderPublicKey.getPublicExponent();

        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();
        RSAPublicKey rsaAccountPublicKey = (RSAPublicKey) accountKeyPair.getPublic();
        String encodedAccountPublicKey = rsaAccountPublicKey.getModulus() + "and" + rsaAccountPublicKey.getPublicExponent();

        // insert parent
        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build()
        );

        subDemandRepository.save(SubDemand.builder()
                .folderCP(KeyCompressor.compress(encodedFolderPublicKey))
                .accountCP(KeyCompressor.compress(encodedAccountPublicKey))
                .accountPublicKey(encodedAccountPublicKey)
                .build()
        );

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.get("/api/v1/subscribe-demands?folderCP=" +
                        KeyCompressor.compress(encodedFolderPublicKey)))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*]", Matchers.hasSize(1)));
    }
}
