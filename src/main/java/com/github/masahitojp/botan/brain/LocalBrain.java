package com.github.masahitojp.botan.brain;


import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LocalBrain implements BotanBrain {
    private final ConcurrentHashMap<String, byte[]> brain;
    private final StampedLock lock = new StampedLock();

    public LocalBrain() {
        brain = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<byte[]> get(String key) {
        return Optional.ofNullable(brain.get(key));
    }

    @Override
    public Optional<byte[]> set(String key, byte[] value) {
        return Optional.ofNullable(brain.put(key, value));
    }

    private int getInteger(String key, Function<Integer,Integer> func) {
        final long stamp = lock.writeLock();
        final byte[] value = brain.get(key);
        final int before;
        final ByteBuffer buffer;
        if (value != null) {
            buffer = ByteBuffer.wrap(value);
            before = buffer.getInt();
        } else {
            before = 0;
            buffer= ByteBuffer.allocate(4);
        }
        final int result = func.apply(before);
        buffer.clear();
        buffer.putInt(result);
        brain.put(key, buffer.array());
        lock.unlock(stamp);
        return result;
    }

    @Override
    public int incr(String key) {
        return getInteger(key, t -> t + 1);
    }

    @Override
    public int decr(String key) {
        return getInteger(key, t -> (t - 1) > 0 ? (t - 1) : 0);
    }

    @Override
    public Set<Map.Entry<String, byte[]>> search(final String startsWith) {
        return this.brain.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(startsWith))
                .collect(Collectors.toSet());
    }
}
