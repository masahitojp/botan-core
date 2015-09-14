package com.github.masahitojp.botan.brain;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * like hubot brain(persistent data)
 */
public interface BotanBrain {
    Optional<byte[]> get(final byte[] key);
    Optional<byte[]> put(final byte[] key, final byte[] value);
    Optional<byte[]> delete(final byte[] key);
    Set<byte[]> keys(final byte[] startsWith);

    @SuppressWarnings("unused")
    default void initialize() {}

    default void beforeShutdown() {}
}
