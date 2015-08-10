package com.github.masahitojp.botan.listener;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.BotanMessage;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BotanMessageListener implements MessageListener, ChatMessageListener {

    protected String description;
    protected Pattern pattern;
    protected Consumer<BotanMessage> action;

    private boolean allReceived = false;

    public Consumer<BotanMessage> getAction() {
        return action;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getDescription() {
        return description;
    }

    private final Botan botan;


    public BotanMessageListener(final Botan botan) {
        this.botan = botan;
    }

    public List<BotanMessageListener> getListeners() {
        return botan.getListeners();
    }
    public final void setAction(final Consumer<BotanMessage> action) {
        this.action = action;
    }
    public final void setDescription(final String description) {
        this.description = description;
    }
    public final void setPattern(final String str) {
        final String pattern;
        if (allReceived) {
            pattern = str;
        } else {
            pattern = String.format("^@?%s:?\\s+%s",botan.getName(), str);
        }
        this.pattern = Pattern.compile(pattern);
    }
    public void setAllReceived(final boolean allReceived) {
        this.allReceived = allReceived;
    }

    @Override
    public final void processMessage(final Chat chat, final Message message) {
        final String body = message.getBody();
        System.out.println(message);

        if (body != null) {
            final Matcher matcher = pattern.matcher(body);
            if(matcher.find()) {
                if (chat != null)
                    action.accept(new BotanMessage(chat, message));
            }
        }
    }

    @Override
    public final void processMessage(final Message message) {
        final String body = message.getBody();
        final String from = message.getFrom();
        System.out.println(message);
        System.out.println(botan.muc.get().getRoom());

        final MultiUserChat m = botan.muc.get();

        if (body != null) {
            final Matcher matcher = pattern.matcher(body);
            if(matcher.find()) {
                if (m != null && from.startsWith(m.getRoom()))
                action.accept(new BotanMessage(m, message));
            }
        }

    }

    @Override
    public String toString() {
        return pattern.toString() + " " + this.description;
    }
}
