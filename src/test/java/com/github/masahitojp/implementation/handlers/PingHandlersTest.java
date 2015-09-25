package com.github.masahitojp.implementation.handlers;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.brain.LocalBrain;
import com.github.masahitojp.botan.brain.MockAdapter;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class PingHandlersTest {
    Botan botan;
    @Before
    public void startUp() {
        botan = new Botan.BotanBuilder()
                .setAdapter(new MockAdapter())
                .setBrain(new LocalBrain())
                .setMessageHandlers(new PingMessageHandlers())
                .build();
    }

    @Test
    public void handlersRegistrationTest() {
        assertThat(botan.getHandlers().size(), is(1));
    }
    @Test
    public void regexTest() {
        final AtomicInteger a = new AtomicInteger();
        MockUp<Consumer<BotanMessage>> spy = new MockUp<Consumer<BotanMessage>>(){
            @Mock
            public void accept(BotanMessage message) {
                a.incrementAndGet();
            }
        };
        botan.getHandlers().stream().filter(handler -> handler.getDescription().equals("ping method")).forEach(handler -> handler.setHandle(spy.getMockInstance()));
        botan.receive(new BotanMessageSimple("botan ping"));
        assertThat(a.get(), is(1));
    }
    @Test
    public void MessageTest() {
        final AtomicReference<String> a = new AtomicReference<>();
        new MockUp<BotanMessage>(){
            @Mock
            public void reply(String message) {
                a.set(message);
            }
        };
        botan.receive(new BotanMessageSimple("botan ping"));
        assertThat(a.get(), is("pong"));
    }
}
