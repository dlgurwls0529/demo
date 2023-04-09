package com.dong.demo.v1.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NgramTest {

    public void ngram_test() {
        String given = "와플대학에서 사먹는 아이스 아메리카노는 진짜 쓰레기 맛이 난다.";
        
        String[] strings = Ngram.ngram(given, 2);

        for (String string : strings) {
            System.out.println("strings = " + string);
        }

    }

    @Test
    public void diff_ngram_test() {
        String a = "오늘 강남에서 맛있는 스파게티를 먹었다.";
        String b = "강남에서 먹었던 오늘의 스파게티는 맛있었다.";
        
        float r2 = Ngram.diff_ngram(a, b, 2);
        System.out.println("r2 = " + r2);
        
        float r3 = Ngram.diff_ngram(a, b, 3);
        System.out.println("r3 = " + r3);
    }
}