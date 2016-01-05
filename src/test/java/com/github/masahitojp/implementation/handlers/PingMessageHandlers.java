package com.github.masahitojp.implementation.handlers;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;
import com.github.masahitojp.botan.message.BotanMessageSimple;

@SuppressWarnings("unused")
public class PingMessageHandlers implements BotanMessageHandlers {

    @Override
    public void register(final Robot robot) {
        robot.respond(
                "ping$",
                "ping method",
                message -> message.reply("pong")
        );

        robot.routerGet("/ping/", (req, res) -> 200);
        robot.routerGet("/ping/:id", (req, res) -> {
                    robot.send(new BotanMessageSimple("pong"));
                    return res.content(String.format("{ \"response\": \"pong\", \"id\": %s}", req.params("id").orElse("000"))).type("application/json; charset=utf-8");
                });
    }
}
