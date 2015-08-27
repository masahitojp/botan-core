package com.github.masahitojp.botan;

import com.github.masahitojp.botan.brain.BotanBrain;
import com.github.masahitojp.botan.exception.BotanException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Optional;
import java.util.regex.Matcher;

public final class BotanMessage {
    private final Message message;
    private final Botan botan;
    private final Matcher matcher;
    private Chat chat;
    private MultiUserChat muc;

    public BotanMessage(final Botan botan, final Chat chat, final Message message, final Matcher matcher) {
        this.botan = botan;
        this.chat = chat;
        this.message = message;
        this.matcher = matcher;
    }

    public BotanMessage(final Botan botan, final MultiUserChat muc, final Message message, final Matcher matcher) {
        this.botan = botan;
        this.muc = muc;
        this.message = message;
        this.matcher = matcher;
    }

    @SuppressWarnings("unused")
    public final String getRobotName() {
        return this.botan.getName();
    }

    @SuppressWarnings("unused")
    public final void reply(final String body) throws BotanException {
        if (body == null) {
            throw new IllegalArgumentException();
        }
        try {
            if (chat != null) {
                this.chat.sendMessage(body);
            } else if (muc != null) {
                // 自分からのメッセージは受け取らないように変更
                final Optional<String> messageFromOpt = Optional.of(message.getFrom());
                final String[] s = messageFromOpt.orElse("").split("/");
                if (s.length == 2 && !s[1].equals(botan.getName())) {
                    this.muc.sendMessage(body);
                }
            }

        } catch (SmackException.NotConnectedException e) {
            throw new BotanException(e);
        }
    }

    @SuppressWarnings("unused")
    public final Matcher getMatcher() {
        return this.matcher;
    }

    @SuppressWarnings("unused")
    public final String getBody() {
        return message.getBody();
    }

    @SuppressWarnings("unused")
    public final String getFrom() {
        final String from = message.getFrom();
        final String[] elems = from.split("/");
        if (elems.length > 1) {
            return elems[elems.length - 1];
        } else {
            final String[] strs = from.split("@");
            return strs[0];
        }
    }

    @SuppressWarnings("unused")
    public final BotanBrain getBrain() {
        return this.botan.brain;
    }
}
