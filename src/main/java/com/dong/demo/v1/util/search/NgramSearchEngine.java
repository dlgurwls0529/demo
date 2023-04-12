package com.dong.demo.v1.util.search;

import org.springframework.stereotype.Component;

@Component
public class NgramSearchEngine implements SearchEngine {
    // s is string to split
    // m is unit of spit
    private String[] ngram(String s, int num) {
        int res_len = s.length()-num+1;
        String[] ngram = new String[res_len];

        for (int i = 0; i < res_len; i++) {
            StringBuilder ngram_one = new StringBuilder();

            for (int j = 0; j < num; j++) {
                ngram_one.append(s.charAt(i + j));
            }
            ngram[i] = String.valueOf(ngram_one);
        }

        return ngram;
    }

    @Override
    public float similarity(String input, String target) {
        String[] input_ngram = ngram(input, 2);
        String[] target_ngram = ngram(target, 2);

        int cnt = 0;
        for (String i : input_ngram) {
            for (String t : target_ngram) {
                if (i.equals(t)) {
                    cnt+=1;
                }
            }
        }

        // float next = Math.nextUp(target_ngram.length);

        return (float)cnt; // / next;
    }
}
