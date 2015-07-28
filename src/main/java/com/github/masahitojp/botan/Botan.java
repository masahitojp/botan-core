package com.github.masahitojp.botan;

import com.github.masahitojp.botan.adapter.BotanAdapter;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.listener.BotBaseMessageListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Botan {

    private final BotanAdapter adapter;
    private final List<BotBaseMessageListener> listeners;
    private final AtomicBoolean flag = new AtomicBoolean(true);

    static class Builder {
        private BotanAdapter adapter;
        private List<BotBaseMessageListener> listeners = new ArrayList<>();

        Builder(final BotanAdapter adapter) {
            this.adapter = adapter;
        }

        @SuppressWarnings("unused")
        Builder listeners(final List<BotBaseMessageListener> listeners) {
            this.listeners = listeners;
            return this;
        }
        Botan build() {
            return new Botan(this);
        }
    }

    public Botan(final Builder builder) {
        this.adapter = builder.adapter;
        this.listeners = builder.listeners;
    }


    public void start() throws BotanException {

        final XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration
                .builder()
                .setServiceName(adapter.getHost())
                .setCompressionEnabled(false)
                .build();

        final AbstractXMPPConnection connection = new XMPPTCPConnection(connConfig);
        try {
            connection.connect();
            connection.login(adapter.getNickName(), adapter.getPassword());
            final MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(connection);
            final MultiUserChat muc = mucm.getMultiUserChat(adapter.getRoomJabberId());

            // set listeners
            BotBaseMessageListener.muc.set(muc);
            this.listeners.forEach(muc::addMessageListener);

            muc.join(adapter.getNickName(), adapter.getPassword());
            muc.sendMessage("connected at " + System.currentTimeMillis());

            while(flag.get()) {
                Thread.sleep(1000L);
            }
        } catch (final XMPPException | SmackException | IOException | InterruptedException e) {
            throw new BotanException(e);
        } finally {
            connection.disconnect();
        }
    }

    @SuppressWarnings("unused")
    public void stop() {
        this.flag.set(false);
    }
}
