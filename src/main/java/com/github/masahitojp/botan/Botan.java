package com.github.masahitojp.botan;

import com.github.masahitojp.botan.adapter.BotanAdapter;
import com.github.masahitojp.botan.brain.BotanBrain;
import com.github.masahitojp.botan.brain.LocalBrain;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.listener.BotanMessageListener;
import com.github.masahitojp.botan.listener.BotanMessageListenerBuilder;
import com.github.masahitojp.botan.listener.BotanMessageListenerRegister;
import com.github.masahitojp.botan.utils.BotanUtils;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class Botan {
    private final String name;
    private final BotanAdapter adapter;
    private final List<BotanMessageListener> listeners = new ArrayList<>();
    private final AtomicBoolean flag = new AtomicBoolean(true);
    public final BotanBrain brain;
    public final AtomicReference<MultiUserChat> muc = new AtomicReference<>();


    public static class BotanBuilder {
        private static String DEFAULT_NAME = "botan";
        private String name = DEFAULT_NAME;
        private final BotanAdapter adapter;
        private BotanBrain brain;

        public BotanBuilder(final BotanAdapter adapter) {
            this.adapter = adapter;
        }

        @SuppressWarnings("unused")
        public final BotanBuilder setName(final String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("unused")
        public final BotanBuilder setBrain(final BotanBrain brain) {
            this.brain = brain;
            return this;
        }

        @SuppressWarnings("unused")
        public final Botan build() {
            if (this.brain == null) {
                this.brain = new LocalBrain();
            }
            return new Botan(this).run();
        }
    }

    private Botan(final BotanBuilder builder)
    {
        this.adapter = builder.adapter;
        this.name = builder.name;
        this.brain = builder.brain;
    }


    public final String getName() {
        return name;
    }

    public final List<BotanMessageListener> getListeners() {
        return listeners;
    }


    private Botan run() {
        setActions();
        BotanUtils.getActions().forEach(x -> listeners.add(BotanMessageListenerBuilder.build(this, x)));
        return this;
    }

    private void setActions() {
        final Reflections reflections = new Reflections();
        Set<Class<? extends BotanMessageListenerRegister>> classes = reflections.getSubTypesOf(BotanMessageListenerRegister.class);
        classes.forEach(clazz -> {
            try {
                clazz.newInstance().register();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private void bind(AbstractXMPPConnection connection) {

        try {
            final ChatManager cm = ChatManager.getInstanceFor(connection);

            // private message listener
            cm.addChatListener((chat, message) -> this.listeners.forEach(chat::addMessageListener));


            final MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(connection);
            final MultiUserChat muc = mucm.getMultiUserChat(adapter.getRoomJabberId());

            muc.join(adapter.getNickName(), adapter.getPassword());


            // set listeners
            this.listeners.forEach(muc::addMessageListener);
            this.muc.set(muc);
        } catch (XMPPException.XMPPErrorException | SmackException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public final void start() throws BotanException {

        final XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration
                .builder()
                .setServiceName(adapter.getHost())
                .setCompressionEnabled(false)
                .build();

        final AbstractXMPPConnection connection = new XMPPTCPConnection(connConfig);
        try {
            connection.connect();
            connection.login(adapter.getNickName(), adapter.getPassword());
            bind(connection);

            while (flag.get()) {
                Thread.sleep(1000L);
            }
        } catch (final XMPPException | SmackException | IOException | InterruptedException e) {
            throw new BotanException(e);
        } finally {
            connection.disconnect();
        }
    }

    @SuppressWarnings("unused")
    public final void stop() {
        this.flag.set(false);
        BotanUtils.doFinalize();
    }


}
