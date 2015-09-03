package com.github.masahitojp.botan;

import com.github.masahitojp.botan.adapter.BotanAdapter;
import com.github.masahitojp.botan.brain.BotanBrain;
import com.github.masahitojp.botan.brain.LocalBrain;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.listener.BotanMessageListener;
import com.github.masahitojp.botan.listener.BotanMessageListenerBuilder;
import com.github.masahitojp.botan.listener.BotanMessageListenerRegister;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import com.github.masahitojp.botan.utils.BotanUtils;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public final class Botan {
    private final String name;
    private final BotanAdapter adapter;
    private final List<BotanMessageListener> listeners = new ArrayList<>();
    public final BotanBrain brain;

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

    public void say(BotanMessage message) {
        this.adapter.say(message);
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


    public final void receive(BotanMessageSimple message) {
        this.getListeners().stream().filter(listener -> message.getBody() != null).forEach(listener -> {
            final Matcher matcher = listener.getPattern().matcher(message.getBody());
            if (matcher.find()) {
                listener.apply(
                        new BotanMessage(
                                this,
                                matcher,
                                message
                        ));
            }
        });
    }

    @SuppressWarnings("unused")
    public final void start() throws BotanException {
        adapter.initialize(this);
        adapter.run();
    }

    @SuppressWarnings("unused")
    public final void stop() {
        adapter.beforeShutdown();
        brain.beforeShutdown();
        BotanUtils.doFinalize();
    }


}
