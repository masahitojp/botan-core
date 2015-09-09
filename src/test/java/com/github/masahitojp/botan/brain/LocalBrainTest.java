package com.github.masahitojp.botan.brain;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

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
        final byte[] value = "test_abc".getBytes();

        assertThat(data.get("test"), is(Optional.empty()));
        assertThat(data.put("test", value), is(Optional.empty()));
        assertThat(data.get("test"), is(Optional.of(value)));
        assertThat(data.delete("test"), is(Optional.of(value)));
        assertThat(data.get("test"), is(Optional.empty()));
    }

    @Test
    public void testIncr() throws Exception {
        assertThat(data.incr("test"), is(1));
    }

    @Test
    public void testDecr() throws Exception {
        assertThat(data.decr("test"), is(0));
        assertThat(data.incr("test"), is(1));
        assertThat(data.incr("test"), is(2));
        assertThat(data.decr("test"), is(1));
    }
}