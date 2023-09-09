package com.dong.demo.v1.service.folder.search;

public interface SearchEngine {
    public float similarity(String input, String target);
}
