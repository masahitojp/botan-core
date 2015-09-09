package com.github.masahitojp.botan.listener;


import java.util.function.Consumer;

public interface BotanMessageListenerSetter extends Consumer<BotanMessageListener> {

    @SuppressWarnings("unused")
    String getDescription();

    @SuppressWarnings("unused")
    String getPatternString();
}