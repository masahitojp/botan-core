package com.github.masahitojp.botan.listener;

import com.github.masahitojp.botan.BotanMessage;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class BotBaseMessageListener implements MessageListener, ChatMessageListener {

    private String description;
    private Pattern pattern;

    public AtomicReference<MultiUserChat> muc = new AtomicReference<>();

    public void doSomething(BotanMessage message) {
        // have to implement
    }


    @Override
    public final void processMessage(final Chat chat, final Message message) {
        final String body = message.getBody();
        System.out.println(message);

        if (body != null) {
            final Matcher matcher = pattern.matcher(body);
            if(matcher.find()) {
                if (chat != null)
                    doSomething(new BotanMessage(chat, message));
            }
        }
    }

    @Override
    public final void processMessage(final Message message) {
        final String body = message.getBody();
        final String from = message.getFrom();
        System.out.println(message);
        System.out.println(muc.get().getRoom());

        final MultiUserChat m = muc.get();

        if (body != null) {
            final Matcher matcher = pattern.matcher(body);
            if(matcher.find()) {
                if (m != null && from.startsWith(m.getRoom()))
                doSomething(new BotanMessage(m, message));
            }
        }

    }

    @Override
    public String toString() {
        return pattern.toString() + " " + this.description;
    }
}
