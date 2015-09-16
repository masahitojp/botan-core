package com.github.masahitojp.implementation.handlers;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;

@SuppressWarnings("unused")
public class TestHearHandlers implements BotanMessageHandlers {

    @Override
    public void register(Robot robot) {
        robot.hear(
                "yappari",
                "yappari neko ga suki",
                message -> message.reply("neko ga suki "));

    }
}
