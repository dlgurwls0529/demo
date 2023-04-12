/*
package com.dong.demo.example.domain.posts;

import com.dong.demo.example.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Posts extends BaseTimeEntity {

    // 이게 프라이머리 키 어노테이션
    @Id
    // 생성 규칙, 1씩 올리는 걸로 함
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 안 해도 되는데, 컬럼 옵션(제약조건) 넣는거
    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
*/
