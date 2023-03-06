package com.dong.demo.web;

import com.dong.demo.domain.posts.Posts;
import com.dong.demo.domain.posts.PostsRepository;
import com.dong.demo.web.dto.PostsSaveRequestDto;
import com.dong.demo.web.dto.PostsUpdateRequestDto;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// web mvc test 안하는 이유는, JPA 까지 활성화를 해야 하기 때문, 저거 하면 컨트롤러까지만 테스트 가능
// 그래서 @SpringBootTest 하고 TestRestTemplate 쓴다.
class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @AfterEach
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_save() throws Exception {
        String title = "테스트 제목";
        String content = "테스트 내용";
        String author = "테스트 저자";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";


        ResponseEntity<Long> responseEntity = restTemplate.
                postForEntity(url, requestDto, Long.class);


        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotEquals(0L, responseEntity.getBody());
        List<Posts> all = postsRepository.findAll();
        Assertions.assertEquals(title, all.get(0).getTitle());
        Assertions.assertEquals(content, all.get(0).getContent());
        Assertions.assertEquals(author, all.get(0).getAuthor());
    }

    @Test
    public void Posts_update() throws Exception {
        Posts savePosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savePosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);


        ResponseEntity<Long> responseEntity = restTemplate.
                exchange(url, HttpMethod.PUT, requestEntity, Long.class);


        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotEquals(0L, responseEntity.getBody());
        List<Posts> all = postsRepository.findAll();
        Assertions.assertEquals(expectedTitle, all.get(0).getTitle());
        Assertions.assertEquals(expectedContent, all.get(0).getContent());
    }

}