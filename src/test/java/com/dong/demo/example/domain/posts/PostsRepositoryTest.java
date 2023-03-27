package com.dong.demo.example.domain.posts;

import com.dong.demo.example.domain.posts.Posts;
import com.dong.demo.example.domain.posts.PostsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
// 이거 하면 자동으로 H2 디비 생성해줌
class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    /* 테스트의 롤백은 테스트 메소드 단위가 아니라, 테스트 할 때마다 수행됨
    // 그래서 두 번 테스트하면 밑에 테스트가 올바르게 (postsList 사이즈 1) 되지만
    // 한 번에 여러 테스트를 진행하면, 실행 중에 롤백되는게 아니므로, 테스트 격리가 안된다.
    // 그래서 save_load_2 .. 이거는 postList 사이즈가 2가 나온다.
    // 그래서 런타임에도 테스트 메소드 끝날 때마다 정리를 해 줄 콜백을 넘기는 것이다. (AfterEach)
    */

    @AfterEach
    public void cleanup() {
        postsRepository.deleteAll();
    }


    @Test
    public void post_save_load() {
        String title = "테스트 게시글";
        String content = "테스트 본문";
        String author = "테스트 작성자";

        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();

        //

        postsRepository.save(posts);
        List<Posts> postsList = postsRepository.findAll();

        //

        Posts actualPosts = postsList.get(0);
        Assertions.assertEquals(1, postsList.size());
        Assertions.assertEquals(title, actualPosts.getTitle());
        Assertions.assertEquals(content, actualPosts.getContent());
        Assertions.assertEquals(author, actualPosts.getAuthor());
    }

    /*@Test
    public void post_save_load_2() {
        String title = "테스트 게시글";
        String content = "테스트 본문";
        String author = "테스트 작성자";

        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();

        //

        postsRepository.save(posts);
        List<Posts> postsList = postsRepository.findAll();

        //

        Posts actualPosts = postsList.get(0);
        Assertions.assertEquals(1, postsList.size());
        Assertions.assertEquals(title, actualPosts.getTitle());
        Assertions.assertEquals(content, actualPosts.getContent());
        Assertions.assertEquals(author, actualPosts.getAuthor());
    }*/

    @Test
    public void BaseTimeEntity_register() {
        LocalDateTime now = LocalDateTime.of(2019, 6, 4, 0, 0, 0);
        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        List<Posts> postsList = postsRepository.findAll();

        Posts posts = postsList.get(0);

        Assertions.assertNotEquals(now, posts.getCreatedDate());
        Assertions.assertNotEquals(now, posts.getModifiedDate());
    }

}