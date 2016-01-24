package com.github.masahitojp.botan;

import com.github.masahitojp.botan.brain.BotanBrain;
import com.github.masahitojp.botan.handler.BotanMessageHandler;
import com.github.masahitojp.botan.handler.BotanMessageHandlerBuilder;
import com.github.masahitojp.botan.handler.BotanMessageHandlerSetter;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;

import com.github.masahitojp.botan.router.BotanRouter;
import org.reflections.Reflections;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class Robot {
    private final Botan botan;

    public List<BotanMessageHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    private final List<BotanMessageHandler> handlers = new ArrayList<>();
    private final List<BotanMessageHandlerSetter> actions = new ArrayList<>();
    private final List<BotanMessageHandlers> registers = new ArrayList<>();
    private BotanMessageHandlers registersForTest;
    public BotanRouter router = new BotanRouter();

    public Robot(final Botan botan) {
        this.botan = botan;
    }

    public Robot(final Botan botan, final BotanMessageHandlers registersForTest) {
        this.botan = botan;
        if (registersForTest != null) {
            this.registersForTest = registersForTest;
        }
    }

    private void setActions() {
        final Reflections reflections = new Reflections();
        Set<Class<? extends BotanMessageHandlers>> classes = reflections.getSubTypesOf(BotanMessageHandlers.class);
        classes.forEach(clazz -> {
            try {
                final BotanMessageHandlers register = clazz.newInstance();
                register.initialize(this);
                register.register(this);
                registers.add(register);
            } catch (final InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }


    final void run() {
        if (registersForTest == null) {
            setActions();
        } else {
            registersForTest.initialize(this);
            registersForTest.register(this);
            registers.add(registersForTest);

        }
        this.actions.forEach(x -> handlers.add(BotanMessageHandlerBuilder.build(this.botan, x)));
        router.startWeb();
    }

    public final void receive(BotanMessageSimple message) {
        this.handlers.parallelStream().filter(listener -> message.getBody() != null).forEach(listener -> {
            // 自分の発言ははじく
            if (!message.getFromName().equals(botan.getName())) {
                final Matcher matcher = listener.getPattern().matcher(message.getBody());
                if (matcher.find()) {
                    listener.apply(
                            new BotanMessage(
                                    this.botan,
                                    matcher,
                                    message
                            ));
                }
            }
        });
    }


    final void beforeShutdown() {
        this.router.beforeShutdown();
        this.registers.forEach(BotanMessageHandlers::beforeShutdown);
    }


    public final void hear(final String pattern, final String description, final Consumer<BotanMessage> action) {
        this.hear(pattern, description, action, false);
    }

    public final void hear(final String pattern, final String description, final Consumer<BotanMessage> action, final boolean hidden) {
        actions.add(new BotanMessageHandlerSetter() {
            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getPatternString() {
                return pattern;
            }

            @Override
            public void accept(BotanMessageHandler botanMessageResponder) {
                botanMessageResponder.setAllReceived(true);
                botanMessageResponder.setDescription(description);
                botanMessageResponder.setPattern(pattern);
                botanMessageResponder.setHandle(action);
                botanMessageResponder.setHidden(hidden);
            }
        });
    }

    public final void respond(final String pattern, final String description, final Consumer<BotanMessage> action) {
        actions.add(new BotanMessageHandlerSetter() {
            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getPatternString() {
                return pattern;
            }

            @Override
            public void accept(BotanMessageHandler botanMessageResponder) {
                botanMessageResponder.setAllReceived(false);
                botanMessageResponder.setDescription(description);
                botanMessageResponder.setPattern(pattern);
                botanMessageResponder.setHandle(action);
            }
        });
    }




    @SuppressWarnings("unused")
    public final String getName() {
        return this.botan.getName();
    }

    @SuppressWarnings("unused")
    public final void send(final BotanMessageSimple message) {
        this.botan.say(new BotanMessage(this.botan, null, message));
    }

    @SuppressWarnings("unused")
    public final BotanBrain getBrain() {
        return this.botan.brain;
    }
}
