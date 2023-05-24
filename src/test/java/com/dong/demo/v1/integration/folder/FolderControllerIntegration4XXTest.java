package com.dong.demo.v1.integration.folder;

import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class FolderControllerIntegration4XXTest extends IntegrationTestTemplate {

    // cleanUp -> Super Class

    // 여기부터 통합테스트 작성하기. 컨트롤러도 맞게 구현해야 함.
    @Test
    public void integration_test_generate_invalid_input() throws Exception {
        // given
        String folderCP = Base58.encode(CipherUtil.genRSAKeyPair().getPublic().getEncoded());

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title(null)
                .build();

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        /* database check */
        Assertions.assertNull(folderRepository.find(folderCP));

    }

    // 중복 생성 예외
    @Test
    public void integration_test_generate_duplicate_folder() throws Exception {
        // given
        String folderCP = KeyCompressor.compress(
                Base58.encode(CipherUtil.genRSAKeyPair().getPublic().getEncoded())
        );

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
                .build();

        // when
        ResultActions resultActionsPre = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        ResultActions resultActionsPro = mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActionsPre
                .andExpect(MockMvcResultMatchers.status().isOk());

        resultActionsPro
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        /* database check */
        Assertions.assertEquals(1, folderRepository.findAllFolderCPAndTitle().size());

    }
}
