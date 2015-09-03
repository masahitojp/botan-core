package com.github.masahitojp.botan.message;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.brain.BotanBrain;

import java.util.regex.Matcher;

public final class BotanMessage {
    private final Botan botan;
    private final Matcher matcher;
    private final BotanMessageSimple messageSimple;

    public BotanMessage(final Botan botan, final Matcher matcher, final BotanMessageSimple messageSimple) {
        this.botan = botan;
        this.matcher = matcher;
        this.messageSimple = messageSimple;
    }


    @SuppressWarnings("unused")
    public final String getRobotName() {
        return this.botan.getName();
    }

    @SuppressWarnings("unused")
    public final void reply(final String body) {
        if (body == null) {
            throw new IllegalArgumentException();
        }
        botan.say(new BotanMessage(this.botan, this.matcher, new BotanMessageSimple(body, messageSimple.getFrom(), messageSimple.getFromName(), messageSimple.getTo(), messageSimple.getType())));
    }

    @SuppressWarnings("unused")
    public final Matcher getMatcher() {
        return this.matcher;
    }

    @SuppressWarnings("unused")
    public final String getBody() {
        return messageSimple.getBody();
    }

    @SuppressWarnings("unused")
    public final String getFrom() {
        return messageSimple.getFrom();
    }

    @SuppressWarnings("unused")
    public final String getFromName() {
        return messageSimple.getFromName();
    }

    @SuppressWarnings("unused")
    public final BotanBrain getBrain() {
        return this.botan.brain;
    }

    public int getType() {
        return messageSimple.getType();
    }

    public String getTo() {
        return messageSimple.getTo();
    }
}
