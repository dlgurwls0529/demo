package com.dong.demo.v1.util.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LevenshteinSearchEngine implements SearchEngine {
    // https://renuevo.github.io/data-science/levenshtein-distance/
    @Override
    public float similarity(String input, String target) {
        String s1 = input;
        String s2 = target;
        List<Integer> costList = new ArrayList<>();
        List<Integer> newCostList = new ArrayList<>();

        //행렬 첫줄 초기화
        for (int i = 0; i <= s1.length(); i++) {
            costList.add(i);
        }

        int matchCost;
        for (int i = 1; i <= s2.length(); i++) {
            newCostList.add(0, i);
            for (int j = 1; j < costList.size(); j++) {

                if (s1.charAt(j - 1) != s2.charAt(i - 1)) matchCost = 1;
                else matchCost = 0;

                // 대체, 삽입, 삭제의 비용을 계산한다
                int replace = costList.get(j - 1) + matchCost;      //변경 비용
                int insert = costList.get(j) + 1;                  //삽입 비용
                int delete = newCostList.get(j - 1) + 1;           //삭제 비용

                int[] arr = new int[]{ replace, insert, delete};
                Arrays.sort(arr);
                newCostList.add(j, arr[0]);  //최소 비용 계산
            }

            Collections.copy(costList, newCostList);    //행렬 줄바꿈
            newCostList.clear();
        }

        // 0으로 나누면 안되니까 부동소수점 최소 간격으로 다음 숫자(숫자마다 간격 다름)
        float next = Math.nextUp((float)costList.get(costList.size() - 1));

        return 1f / next;
    }
}
