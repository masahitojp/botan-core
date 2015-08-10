package com.github.masahitojp.botan.listener;

import com.github.masahitojp.botan.Botan;
import java.util.function.Consumer;

public class BotanMessageListenerBuilder {

    public static BotanMessageListener build(final Botan botan ,final BotanMessageListenerSetter setter) {
        final BotanMessageListener listener = new BotanMessageListener(botan);
        setter.accept(listener);
        return listener;
    }
}
