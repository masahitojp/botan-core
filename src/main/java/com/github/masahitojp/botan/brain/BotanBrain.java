package com.github.masahitojp.botan.brain;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * like hubot brain(persistent data)
 */
public interface BotanBrain {
    ConcurrentHashMap<String, String> getData();

    @SuppressWarnings("unused")
    default void initialize() {}

    default void beforeShutdown() {}
}
