package com.github.masahitojp.botan.brain;

import java.util.Optional;

public interface BotanBrain {
    Optional<byte[]> get(final String key);
    Optional<byte[]> set(final String key, final byte[] value);
    int incr(final String key);
    int decr(final String key);
}
