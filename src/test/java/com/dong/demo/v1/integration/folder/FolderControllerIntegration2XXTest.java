package com.dong.demo.v1.integration.folder;

import com.dong.demo.v1.integration.IntegrationTestTemplate;
import com.dong.demo.v1.util.Base58;
import com.dong.demo.v1.util.CipherUtil;
import com.dong.demo.v1.util.KeyCompressor;
import com.dong.demo.v1.web.dto.FoldersGenerateRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FolderControllerIntegration2XXTest extends IntegrationTestTemplate {

    // cleanUp -> Super Class

    @Test
    public void integration_test_generate_input_normal() throws Exception {
        // given
        String folderCP = KeyCompressor.compress(
                Base58.encode(CipherUtil.genRSAKeyPair().getPublic().getEncoded())
        );

        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(folderCP));

        /* database check */
        Assertions.assertNotNull(folderRepository.find(folderCP));

    }

    @Test
    public void integration_test_generate_input_maybe_conflict() throws Exception {
        // given
        String folderCP = "files";
        FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                .isTitleOpen(true)
                .symmetricKeyEWF("sym_TEST")
                .title("title_TEST")
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(folderCP));

        Assertions.assertNotNull(folderRepository.find(folderCP));
    }

    @Test
    public void integration_test_search_input_12() throws Exception {
        // given
        List<String> folder_title_list = Arrays.asList(
                    "김동욱씌는 마라탕은 안뭑으싀나요?",
                    "깅동욱씨는 마라탕 안먹으셨어요?",
                    "잇츠 베리 심플. 롸잇?",
                    "유 캔 두잇.",
                    "잇츠 베리 이지. 유 캔 두 잇.",
                    "동욱아.. 그거 이틀이면 만들지 않냐??",
                    "플뤄터?? 자봐는 안써?",
                    "렛츠 고 넥스트 챕터",
                    "디스 이즈 메모리. 기억해놓으세요.",
                    "디스 이즈 디스크. 굉장히 중요합니다.",
                    "아.. 씨. 얘두롸... 그뫈할꽈..?",
                    "학쌩! 그러씌면 안됩니다!! 일어나씝씨오.!!!"
                );

        for (String title : folder_title_list) {
            String folderCP = KeyCompressor.compress(
                    Base58.encode(CipherUtil.genRSAKeyPair().getPublic().getEncoded())
            );

            FoldersGenerateRequestDto dto = FoldersGenerateRequestDto.builder()
                    .isTitleOpen(true)
                    .symmetricKeyEWF("sym_TEST")
                    .title(title)
                    .build();

            ResultActions resultActions = mvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/folders/" + folderCP)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON)
            );
        }

        String keyword = "김동욱씨 디스크는 안쓰싀나요??";

        // when
        ResultActions resultActions = mvc.perform(
                MockMvcRequestBuilders.get("/api/v1/folders?keyword=" + keyword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
