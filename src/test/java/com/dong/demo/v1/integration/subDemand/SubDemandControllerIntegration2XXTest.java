package com.dong.demo.v1.integration.subDemand;

import com.dong.demo.v1.domain.folder.Folder;
import com.dong.demo.v1.domain.subDemand.SubDemand;
import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.util.LocalDateTime6Digit;
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

public class SubDemandControllerIntegration2XXTest extends IntegrationTestTemplate {

    @Test
    public void addSubscribeDemand2XXTest() throws Exception {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(accountKeyPair.getPrivate());
        signature.update(accountKeyPair.getPublic().getEncoded());
        byte[] byteSign = signature.sign();

        SubscribeDemandsAddRequestDto subscribeDemandsAddRequestDto = SubscribeDemandsAddRequestDto.builder()
                .accountPublicKey(Base58.encode(accountKeyPair.getPublic().getEncoded()))
                .byteSign(byteSign)
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .build();

        // insert parent
        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
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
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(folderKeyPair.getPrivate());
        signature.update(folderKeyPair.getPublic().getEncoded());
        byte[] byteSign = signature.sign();

        SubscribeDemandsAllowRequestDto subscribeDemandsAllowRequestDto = SubscribeDemandsAllowRequestDto.builder()
                .accountCP(KeyCompressor.compress(
                        Base58.encode(accountKeyPair.getPublic().getEncoded())
                ))
                .folderPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                .byteSign(byteSign)
                .symmetricKeyEWA("sym_TEST")
                .build();

        // insert parent
        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build());

        subDemandRepository.save(SubDemand.builder()
                .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                .accountPublicKey(Base58.encode(accountKeyPair.getPublic().getEncoded()))
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
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
                        KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded()))).size()
        );

        Assertions.assertEquals(1,
                readAuthRepository.findByAccountCP(
                        KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded()))).size()
        );
    }

    @Test
    public void getSubscribeDemand2XXTest() throws Exception {
        // given
        KeyPair folderKeyPair = CipherUtil.genRSAKeyPair();
        KeyPair accountKeyPair = CipherUtil.genRSAKeyPair();

        // insert parent
        folderRepository.save(Folder.builder()
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .title("title_TEST")
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .lastChangedDate(LocalDateTime6Digit.now())
                .build()
        );

        subDemandRepository.save(SubDemand.builder()
                .folderCP(KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded())))
                .accountCP(KeyCompressor.compress(Base58.encode(accountKeyPair.getPublic().getEncoded())))
                .accountPublicKey(Base58.encode(folderKeyPair.getPublic().getEncoded()))
                .build()
        );

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.get("/api/v1/subscribe-demands?folderCP=" +
                        KeyCompressor.compress(Base58.encode(folderKeyPair.getPublic().getEncoded()))))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*]", Matchers.hasSize(1)));
    }
}
