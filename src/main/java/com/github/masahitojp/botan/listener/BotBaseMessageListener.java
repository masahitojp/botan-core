package com.github.masahitojp.botan.listener;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.concurrent.atomic.AtomicReference;

public interface BotBaseMessageListener extends MessageListener {

    public AtomicReference<MultiUserChat> muc = new AtomicReference<>(null);

    default boolean executionCondition(String body) {
        return true;
    }

    void doSomething(String body);

    default public void processMessage(Message message) {
        System.out.println("Received message: " + message);
        final String body = message.getBody();
        if (body != null) {
            if(executionCondition(body)) {
                doSomething(body);
            }
        }

    }
}
