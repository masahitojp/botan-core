package com.github.masahitojp.botan.handler;


import java.util.function.Consumer;

public interface BotanMessageHandlerSetter extends Consumer<BotanMessageHandler> {

    @SuppressWarnings("unused")
    String getDescription();

    @SuppressWarnings("unused")
    String getPatternString();
}