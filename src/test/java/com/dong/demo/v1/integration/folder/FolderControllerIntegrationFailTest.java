package com.dong.demo.v1.integration.folder;

import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class FolderControllerIntegrationFailTest extends IntegrationTestTemplate {

    // cleanUp -> Super Class

    // todo : 여기부터 통합테스트 작성하기. 컨트롤러도 맞게 구현해야 함.
    @Test
    public void integrationSuccessTest_invalid_input() throws Exception {
        // given
        String folderCP = Base58.encode(CipherUtil.genRSAKeyPair().getPublic().getEncoded());

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title(null)
                .build();

        // String url = "http://localhost:" + port + "/api/v1/folders/" + folderCP;

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

}
