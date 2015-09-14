package com.github.masahitojp.implementation.responder;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.responder.BotanMessageResponderRegister;

@SuppressWarnings("unused")
public class TestHearResponder implements BotanMessageResponderRegister {

    @Override
    public void register(Robot robot) {
        robot.hear(
                "yappari",
                "yappari neko ga suki",
                message -> message.reply("neko ga suki "));

    }
}
