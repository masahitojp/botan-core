package com.github.masahitojp.botan.listener;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.adapter.ComandLineAdapter;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class BotanMessageListenerTest {
    @Test
    public void toStringTest() {
        final Botan botan = new Botan.BotanBuilder(new ComandLineAdapter()).build();
        final BotanMessageListener listener = new BotanMessageListener(botan);
        listener.setAllReceived(true);
        listener.setDescription("test");
        listener.setPattern("pattern");
        assertThat(listener.toString(), is("| pattern - test\n"));
    }

}