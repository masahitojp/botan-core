package com.github.masahitojp.botan.adapter;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import com.github.masahitojp.botan.exception.BotanException;
import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Adapter for Slack.com
 */
@Slf4j
public final class SlackAdapter implements BotanAdapter {

    private final String team;
    private final String user;
    private final String pswd;
    private final String room;
    private final AtomicBoolean flag = new AtomicBoolean(true);
    private AbstractXMPPConnection connection = null;
    private MultiUserChat muc = null;
    private Botan botan;

    public SlackAdapter(String team, String user, String pswd, String room) {
        this.team = team;
        this.user = user;
        this.pswd = pswd;
        this.room = room;
    }

    public String getNickName() {
        return this.user;
    }

    public String getPassword() {
        return this.pswd;
    }

    public String getRoomJabberId() {
        return room + getRoomHost();
    }

    public String getHost() {
        return team + ".xmpp.slack.com";
    }

    public String getRoomHost() {
        return "@conference." + this.getHost();
    }

    @Override
    public void run() throws BotanException {
        final XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration
                .builder()
                .setServiceName(this.getHost())
                .setCompressionEnabled(false)
                .build();

        connection = new XMPPTCPConnection(connConfig);
        try {
            connection.connect();
            connection.login(this.getNickName(), this.getPassword());
            bind(connection);
            while (flag.get()) {
                Thread.sleep(1000);
            }
        } catch (final XMPPException | SmackException | IOException e) {
            throw new BotanException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (muc != null) {
                try {
                    muc.leave();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();
        }

    }

    @Override
    public void say(BotanMessage message) {
        if (message.getType() == Message.Type.groupchat.ordinal()) {
            if (muc != null) {
                try {
                    if (!message.getFromName().equals(botan.getName())) {
                        muc.sendMessage(new Message(message.getTo(), message.getBody()));
                    }
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

        } else {
            final ChatManager cm = ChatManager.getInstanceFor(connection);
            try {
                cm.createChat(message.getFrom()).sendMessage(new Message(message.getTo(), message.getBody()));
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(Botan botan) {
        this.botan = botan;
    }

    @Override
    public void beforeShutdown() {
        flag.compareAndSet(true, false);
    }

    private void bind(AbstractXMPPConnection connection) {

        try {
            final ChatManager cm = ChatManager.getInstanceFor(connection);

            // private message listener
            cm.addChatListener((chat, messageFlg) -> chat.addMessageListener((chat1, message) -> {
                log.debug(message.toString());
                botan.receive(new BotanMessageSimple(
                        message.getBody(),
                        message.getFrom(),
                        usernameOf(message),
                        message.getTo(),
                        message.getType().ordinal()

                ));
            }));


            final MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(connection);
            muc = mucm.getMultiUserChat(this.getRoomJabberId());
            muc.join(this.getNickName(), this.getPassword());
            muc.addMessageListener((message) -> {
                        log.debug(message.toString());
                        botan.receive(new BotanMessageSimple(
                                message.getBody(),
                                message.getFrom(),
                                usernameOf(message),
                                message.getTo(),
                                message.getType().ordinal()
                        ));
                    }
            );

        } catch (XMPPException.XMPPErrorException | SmackException e) {
            e.printStackTrace();
        }
    }

    private String usernameOf(Message message) {
        final String from = message.getFrom();
        final String[] elems = from.split("/");
        if (elems.length > 1) {
            return elems[elems.length - 1];
        } else {
            final String[] strs = from.split("@");
            return strs[0];
        }
    }

    public Optional<String> getFromAdapterName() {
        return Optional.of(user);
    }
}
