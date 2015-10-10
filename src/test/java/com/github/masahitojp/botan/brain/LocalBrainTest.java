package com.github.masahitojp.botan.brain;

import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocalBrainTest {

    private BotanBrain data;

    @Before
    public void startUp() {
        data = new LocalBrain();
    }

    @Test
    public void testSet() {
        val key = "test";
        val value = "test_abc";

        assertThat(data.getData().get(key), is(nullValue()));
        assertThat(data.getData().put(key, value), is(nullValue()));
        assertThat(data.getData().get("test"), is(value));
        assertThat(data.getData().remove("test"), is(value));
        assertThat(data.getData().get(key), is(nullValue()));
    }

    @Test
    public void search() {
        val key = "test";
        val value = "test_abc";

        data.getData().put(key, value);
        data.getData().put("test2", "test2");
        data.getData().put("key", "value");

        final List<String> list = data.getData().keySet().stream().filter(x -> x.startsWith("test")).collect(Collectors.toList());
        assertThat(list.size(), is(2));
        assertThat(list.contains(key), is(true));

    }

}