package com.github.masahitojp.botan.listener;


import java.util.function.Consumer;

@FunctionalInterface
public interface BotanMessageListenerSetter extends Consumer<BotanMessageListener> {}