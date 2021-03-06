package com.github.masahitojp.botan;

import com.github.masahitojp.botan.adapter.BotanAdapter;
import com.github.masahitojp.botan.adapter.ComandLineAdapter;
import com.github.masahitojp.botan.brain.BotanBrain;
import com.github.masahitojp.botan.brain.LocalBrain;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.handler.BotanMessageHandler;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;

import com.github.masahitojp.botan.utils.BotanUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.*;

@Slf4j
public final class Botan {
    public final BotanBrain brain;
    private String name;
    private final BotanAdapter adapter;
    @Getter
    private final Robot robot;

    private Botan(final BotanBuilder builder) {
        this.adapter = builder.adapter;
        this.name = builder.name;
        this.brain = builder.brain;
        this.robot = new Robot(this, builder.handlers);
    }

    static public void main(final String[] Args) {
        BotanUtils.readDotEnv();
        final Botan botan = new Botan.BotanBuilder()
                .build();

        java.lang.Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        botan.stop();
                    }
                }
        );
        try {
            botan.start();
        } catch (final BotanException e) {
            log.warn("[Botan] {}", e);
        }

    }

    public final String getName() {
        return name;
    }

    public void say(BotanMessage message) {
        this.adapter.say(message);
    }

    public final void receive(BotanMessageSimple message) {
        this.robot.receive(message);
    }

    @SuppressWarnings("unused")
    public final void start() throws BotanException {
        log.info("bot start");
        adapter.initialize(this);

        // adapterはRunしたあとじゃないと名前がとれないことがあるため
        this.adapter.getFromAdapterName().ifPresent(x -> this.name = x);
        log.info("bot name : {}", this.name);


        this.robot.run();
        adapter.run();

    }

    @SuppressWarnings("unused")
    public final void stop() {
        log.info("bot stop");
        adapter.beforeShutdown();
        brain.beforeShutdown();
        robot.beforeShutdown();
    }

    public static class BotanBuilder {
        private static String DEFAULT_NAME = "botan";
        private BotanAdapter adapter;
        private String name = DEFAULT_NAME;
        private BotanBrain brain;
        private BotanMessageHandlers handlers;
        public BotanBuilder() {
        }

        @SuppressWarnings("unused")
        public final BotanBuilder setAdapter(final BotanAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public final BotanBuilder setMessageHandlers(final BotanMessageHandlers handlers) {
            this.handlers = handlers;
            return this;
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
            setDefaultAdapter();
            setDefaultBrain();
            return new Botan(this);
        }

        private void setDefaultAdapter() {
            if (this.adapter == null) {
                final Optional<String> designatedClassName = Optional.ofNullable(System.getProperty("adapter"));
                final Reflections reflections = new Reflections();
                Set<Class<? extends BotanAdapter>> classes = reflections.getSubTypesOf(BotanAdapter.class);

                designatedClassName.ifPresent(x -> classes.stream().filter(clazz -> clazz.getName().equals(x)).forEach(
                        y -> {
                            try {
                                this.adapter = y.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                log.warn("{}", e);
                            }
                        }
                ));


                if (this.adapter == null) {
                    Optional<Class<? extends BotanAdapter>> last = classes.stream()
                            .filter(clazz -> !clazz.getName().equals(ComandLineAdapter.class.getName()))
                            .reduce((previous, current) -> current);

                    last.ifPresent(adapter -> {
                        try {
                            this.adapter = adapter.newInstance();
                            this.adapter.getFromAdapterName().ifPresent(x -> this.name = x);
                        } catch (InstantiationException | IllegalAccessException e) {
                            log.warn("{}", e);
                        }
                    });
                }
                if (this.adapter == null) {
                    this.adapter = new ComandLineAdapter();
                }
            }
            log.info("adapter: {}", this.adapter.getClass().getSimpleName());
        }

        private void setDefaultBrain() {
            if (this.brain == null) {
                final Optional<String> designatedClassName = Optional.ofNullable(System.getProperty("brain"));
                final Reflections reflections = new Reflections();
                Set<Class<? extends BotanBrain>> classes = reflections.getSubTypesOf(BotanBrain.class);

                designatedClassName.ifPresent(x -> classes.stream().filter(clazz -> clazz.getName().equals(x)).forEach(
                        clazz -> {
                            try {
                                this.brain = clazz.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                log.warn("{}", e);
                            }
                        }
                ));

                if (this.brain == null) {
                    Optional<Class<? extends BotanBrain>> last = classes.stream()
                            .filter(clazz -> !clazz.getName().equals(LocalBrain.class.getName()))
                            .reduce((previous, current) -> current);

                    last.ifPresent(x -> {
                        try {
                            this.brain = x.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            log.warn("{}", e);
                        }
                    });
                }

                if (this.brain == null) {
                    this.brain = new LocalBrain();
                }
            }
            this.brain.initialize();
            log.info("brain: {}", this.brain.getClass().getSimpleName());
        }
    }
    public final List<BotanMessageHandler> getHandlers() {
        return robot.getHandlers();
    }
}
