package com.github.masahitojp.botan.brain;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LocalBrainTest {

    private BotanBrain data;

    @BeforeEach
    public void startUp() {
        data = new LocalBrain();
    }

    @Test
    public void testSet() {
        val key = "test";
        val value = "test_abc";

        assertNull(data.getData().get(key));
        assertNull(data.getData().put(key, value));
        assertEquals(data.getData().get("test"), value);
        assertEquals(data.getData().remove("test"), value);
        assertNull(data.getData().get(key));
    }

    @Test
    public void search() {
        val key = "test";
        val value = "test_abc";

        data.getData().put(key, value);
        data.getData().put("test2", "test2");
        data.getData().put("key", "value");

        final List<String> list = data.getData().keySet().stream().filter(x -> x.startsWith("test")).collect(Collectors.toList());
        assertEquals(list.size(), 2);
        assertTrue(list.contains(key));
    }

}