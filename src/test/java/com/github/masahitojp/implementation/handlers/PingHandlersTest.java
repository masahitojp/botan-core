package com.github.masahitojp.implementation.handlers;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.brain.LocalBrain;
import com.github.masahitojp.botan.adapter.MockAdapter;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;

import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PingHandlersTest {
    private Botan botan;
    @BeforeEach
    public void startUp() throws BotanException {
        botan = new Botan.BotanBuilder()
                .setAdapter(new MockAdapter())
                .setBrain(new LocalBrain())
                .setMessageHandlers(new PingMessageHandlers())
                .build();
        botan.start();
    }

    @AfterEach
    public void tearDown() {
        botan.stop();
    }

    @Test
    public void handlersRegistrationTest() {
        assertEquals(botan.getHandlers().size(), 1);
    }
    @Test
    public void regexTest() {
        final AtomicInteger a = new AtomicInteger();
        MockUp<Consumer<BotanMessage>> spy = new MockUp<Consumer<BotanMessage>>(){
            @Mock
            @SuppressWarnings("unused")
            public void accept(BotanMessage message) {
                a.incrementAndGet();
            }
        };
        botan.getHandlers().stream().filter(handler -> handler.getDescription().equals("ping method")).forEach(handler -> handler.setHandle(spy.getMockInstance()));
        botan.receive(new BotanMessageSimple("botan ping"));
        assertEquals(a.get(), 1);
    }
    @Test
    public void MessageTest() {
        final AtomicReference<String> a = new AtomicReference<>();
        new MockUp<BotanMessage>(){
            @Mock
            @SuppressWarnings("unused")
            public void reply(String message) {
                a.set(message);
            }
        };
        botan.receive(new BotanMessageSimple("botan ping"));
        assertEquals(a.get(), "pong");
    }
}
