package com.github.masahitojp.botan.handler;

import com.github.masahitojp.botan.Botan;

public final class BotanMessageHandlerBuilder {

    public static BotanMessageHandler build(final Botan botan, final BotanMessageHandlerSetter setter) {
        final BotanMessageHandler listener = new BotanMessageHandler(botan);
        setter.accept(listener);
        return listener;
    }
}
