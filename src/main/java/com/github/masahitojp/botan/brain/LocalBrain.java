package com.github.masahitojp.botan.brain;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LocalBrain implements BotanBrain {
    private final ConcurrentHashMap<String, String> data;
    public final ConcurrentHashMap<String, String> getData() {
        return data;
    }

    public LocalBrain() {
        data = new ConcurrentHashMap<>();
    }
}
