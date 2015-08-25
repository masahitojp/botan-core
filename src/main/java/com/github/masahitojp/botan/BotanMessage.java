package com.github.masahitojp.botan;

import com.github.masahitojp.botan.exception.BotanException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Optional;
import java.util.regex.Matcher;

public final class BotanMessage {
    private Chat chat;
    private MultiUserChat muc;
    private final Message message;
    private final Botan botan;
    private final Matcher matcher;

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

    public final String getRobotName() {
        return this.botan.getName();
    }

    public final void reply(final String body) throws BotanException {
        try {
            // Todo : コンストラクタでnullが割り当てられないようにする
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

    public final Matcher getMatcher() {
        return this.matcher;
    }

    public final String getBody() {
        return message.getBody();
    }

    public final String getFrom() {
        final String from = message.getFrom();
        final String[] elems = from.split("/");
        if (elems.length > 0) {
            return elems[elems.length -1];
        } else {
            return from;
        }
    }
}
