package com.dong.demo.v1.util.search;

import com.dong.demo.v1.service.folder.search.LevenshteinSearchEngine;

import java.util.ArrayList;
import java.util.List;

class LevenshteinSearchEngineTest {

    void similarity() {
        LevenshteinSearchEngine levenshteinSearchEngine = new LevenshteinSearchEngine();

        String input = "i am hungri";

        List<String> targets = new ArrayList<>();
        targets.add("so hungry cry");
        targets.add("Is there reporter hungry");
        targets.add("hungryyyyyyyyyyyy");
        targets.add("hungry");
        targets.add("hun");
        targets.add("gry");
        targets.add("hungry hunfy");
        targets.add("i was hungri");

        for (String target : targets) {
            System.out.print("target = " + target);
            float dis = levenshteinSearchEngine.similarity(input, target);
            System.out.println("      dis = " + dis);
        }
    }
}