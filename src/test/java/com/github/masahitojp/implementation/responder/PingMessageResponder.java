package com.github.masahitojp.implementation.responder;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.responder.BotanMessageResponderRegister;

@SuppressWarnings("unused")
public class PingMessageResponder implements BotanMessageResponderRegister {

    @Override
    public void register(final Robot robot) {
        robot.respond(
                "ping",
                "ping method",
                message -> message.reply("pong")
        );
    }
}
