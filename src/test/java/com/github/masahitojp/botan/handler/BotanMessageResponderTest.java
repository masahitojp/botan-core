package com.github.masahitojp.botan.handler;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.adapter.ComandLineAdapter;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class BotanMessageResponderTest {
    @Test
    public void toStringTest() {
        final Botan botan = new Botan.BotanBuilder().setAdapter(new ComandLineAdapter()).build();
        final BotanMessageHandler handler = new BotanMessageHandler(botan);
        handler.setAllReceived(true);
        handler.setDescription("test");
        handler.setPattern("pattern");
        assertThat(handler.toString(), is("| pattern - test\n"));
    }

}