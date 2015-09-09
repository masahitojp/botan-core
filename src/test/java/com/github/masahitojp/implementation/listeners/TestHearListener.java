package com.github.masahitojp.implementation.listeners;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.listener.BotanMessageListenerRegister;

@SuppressWarnings("unused")
public class TestHearListener implements BotanMessageListenerRegister {

    @Override
    public void register(Robot robot) {
        robot.hear(
                "yappari",
                "yappari neko ga suki",
                message -> message.reply("neko ga suki "));

    }
}
