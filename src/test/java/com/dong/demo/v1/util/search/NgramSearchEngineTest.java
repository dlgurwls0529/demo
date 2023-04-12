package com.dong.demo.v1.util.search;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NgramSearchEngineTest {

    // integer casting is ok
    public void similarity_test() {
        NgramSearchEngine ngramSearchEngine = new NgramSearchEngine();

        String input = "i am hungri";

        List<String> targets = new ArrayList<>();
        targets.add("i a hungri");
        targets.add("i am hungry");
        targets.add("what is food");
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
            float dis = ngramSearchEngine.similarity(input, target);
            System.out.println("      dis = " + dis);
        }
    }
}