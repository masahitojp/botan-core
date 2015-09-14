package com.github.masahitojp.botan.responder;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.adapter.ComandLineAdapter;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class BotanMessageResponderTest {
    @Test
    public void toStringTest() {
        final Botan botan = new Botan.BotanBuilder(new ComandLineAdapter()).build();
        final BotanMessageResponder listener = new BotanMessageResponder(botan);
        listener.setAllReceived(true);
        listener.setDescription("test");
        listener.setPattern("pattern");
        assertThat(listener.toString(), is("| pattern - test\n"));
    }

}