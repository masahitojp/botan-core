package com.github.masahitojp.implementation.listeners;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.listener.BotanMessageListenerRegister;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import com.github.masahitojp.botan.utils.BotanUtils;

@SuppressWarnings("unused")
public class PingPongMessageListener implements BotanMessageListenerRegister {

    @Override
    public void register(final Robot robot) {
        robot.respond(
                "ping",
                "ping method",
                message -> message.reply("pong")
        );
    }
}
