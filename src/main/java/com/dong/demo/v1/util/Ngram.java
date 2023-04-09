package com.dong.demo.v1.util;

public class Ngram {
    // s is string to split
    // m is unit of spit
    public static String[] ngram(String s, int num) {
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

    public static float diff_ngram(String sa, String sb, int num) {
        String[] a = ngram(sa, num);
        String[] b = ngram(sb, num);

        int cnt = 0;
        for (String s_a : a) {
            for (String s_b : b) {
                if (s_a.equals(s_b)) {
                    cnt++;
                }
            }
        }

        return (float)cnt / a.length;
    }
}
