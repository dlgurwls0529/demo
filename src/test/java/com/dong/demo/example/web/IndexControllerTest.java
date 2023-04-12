/*
package com.dong.demo.example.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IndexControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void main_page_loading() {
        String body = this.testRestTemplate.getForObject("/", String.class);

        org.assertj.core.api.Assertions.assertThat(body).contains("스프링 부트로 시작하는 웹서비스");
    }
}*/
