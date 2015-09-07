package com.github.masahitojp.implementation.listeners;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.listener.BotanMessageListenerRegister;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import com.github.masahitojp.botan.utils.BotanUtils;

@SuppressWarnings("unused")
public class PingPongMessageListener implements BotanMessageListenerRegister {

    @Override
    public void register(final Botan botan) {
        BotanUtils.respond(
                "ping",
                "ping method",
                message -> botan.receive(new BotanMessageSimple("echo ping"))
        );
    }
}
