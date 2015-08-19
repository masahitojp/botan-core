package com.github.masahitojp.botan.listener;


import java.util.function.Consumer;

public interface BotanMessageListenerSetter extends Consumer<BotanMessageListener> {
    String getDescription();
    String getPatterString();
}