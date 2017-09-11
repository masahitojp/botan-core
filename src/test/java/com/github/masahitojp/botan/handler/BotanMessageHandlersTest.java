package com.github.masahitojp.botan.handler;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.adapter.ComandLineAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BotanMessageHandlersTest {
    @Test
    public void toStringTest() {
        final Botan botan = new Botan.BotanBuilder().setAdapter(new ComandLineAdapter()).build();
        final BotanMessageHandler handler = new BotanMessageHandler(botan);
        handler.setAllReceived(true);
        handler.setDescription("test");
        handler.setPattern("pattern");
        assertEquals(handler.toString(), "> pattern - test");
    }

}