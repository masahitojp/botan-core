package com.github.masahitojp.botan.brain;

import lombok.Data;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LocalBrain implements BotanBrain {
    private final ConcurrentHashMap<KeyContainer, byte[]> data;

    @Data
    private class KeyContainer {
        private final byte[] byteArray;
    }

    public LocalBrain() {
        data = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<byte[]> get(byte[] key) {
        return Optional.ofNullable(data.get(new KeyContainer(key)));
    }

    @Override
    public Optional<byte[]> put(byte[] key, byte[] value) {
        return Optional.ofNullable(data.put(new KeyContainer(key), value));
    }

    @Override
    public Optional<byte[]> delete(byte[] key) {
        return Optional.ofNullable(data.remove(new KeyContainer(key)));
    }

    @Override
    public Set<byte[]> keys(final byte[] startsWith) {
        return this.data.keySet()
                .stream()
                .filter(key -> indexOf(key.byteArray, startsWith) == 0)
                .map(key -> key.byteArray)
                .collect(Collectors.toSet());
    }

    private int indexOf(byte[] outerArray, byte[] smallerArray) {
        for(int i = 0; i < outerArray.length - smallerArray.length+1; i++) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; j++) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }
}
