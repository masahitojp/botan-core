package com.github.masahitojp.botan;

import com.github.masahitojp.botan.exception.BotanException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Optional;

public final class BotanMessage {
    private Chat chat;
    private MultiUserChat muc;
    private final Message message;
    private final Botan botan;

    public BotanMessage(final Botan botan, final Chat chat, final Message message) {
        this.botan = botan;
        this.chat = chat;
        this.message = message;
    }
    public BotanMessage(final Botan botan, final MultiUserChat muc, final Message message) {
        this.botan = botan;
        this.muc = muc;
        this.message = message;
    }

    public final String getRobotName() {
        return this.botan.getName();
    }

    public final void reply(final String body) throws BotanException {
        try {
            // Todo : コンストラクタでnullが割り当てられないようにする
            if (chat != null)  {
                this.chat.sendMessage(body);
            }
            else if (muc!=null) {
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


    public final String getBody() {
        return message.getBody();
    }

    public final String getFrom() {
        return message.getFrom();
    }
}
