package com.github.masahitojp.botan.brain;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * like hubot brain(persistent data)
 */
public interface BotanBrain {
    Optional<byte[]> get(final String key);
    Optional<byte[]> put(final String key, final byte[] value);
    Optional<byte[]> delete(final String key);
    int incr(final String key);
    int decr(final String key);
    Set<Map.Entry<String, byte[]>> search(final String startsWith);

    @SuppressWarnings("unused")
    default void initialize() {}

    default void beforeShutdown() {}
}
