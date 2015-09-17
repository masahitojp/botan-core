package com.github.masahitojp.botan.brain;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Set;

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
        final byte[] key = "test".getBytes();
        final byte[] value = "test_abc".getBytes();

        assertThat(data.get(key), is(Optional.empty()));
        assertThat(data.put(key, value), is(Optional.empty()));
        assertThat(data.get("test".getBytes()), is(Optional.of(value)));
        assertThat(data.delete("test".getBytes()), is(Optional.of(value)));
        assertThat(data.get(key), is(Optional.empty()));
    }

    @Test
    public void search() throws UnsupportedEncodingException {
        final byte[] key = "test".getBytes("UTF-8");
        final byte[] value = "test_abc".getBytes("UTF-8");


        data.put(key, value);
        data.put("test2".getBytes("UTF-8"), "test2".getBytes("UTF-8"));
        data.put("key".getBytes("UTF-8"), "value".getBytes("UTF-8"));

        Set<byte[]> list = data.keys("test".getBytes("UTF-8"));
        assertThat(list.size(), is(2));
        assertThat(list.contains(key), is(true));

    }

}