package com.github.masahitojp.botan.responder;

import com.github.masahitojp.botan.Botan;

public class BotanMessageResponderBuilder {

    public static BotanMessageResponder build(final Botan botan, final BotanMessageResponderSetter setter) {
        final BotanMessageResponder listener = new BotanMessageResponder(botan);
        setter.accept(listener);
        return listener;
    }
}
