package com.github.masahitojp.botan;

import com.github.masahitojp.botan.listener.BotanMessageListener;
import com.github.masahitojp.botan.listener.BotanMessageListenerBuilder;
import com.github.masahitojp.botan.listener.BotanMessageListenerRegister;
import com.github.masahitojp.botan.listener.BotanMessageListenerSetter;
import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.message.BotanMessageSimple;
import lombok.Getter;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class Robot {
    private final Botan botan;
    @Getter
    private final List<BotanMessageListener> listeners = new ArrayList<>();
    private final List<BotanMessageListenerSetter> actions = new ArrayList<>();
    private final List<BotanMessageListenerRegister> registers = new ArrayList<>();
    public Robot(final Botan botan) {
        this.botan = botan;
    }

    private void setActions() {
        final Reflections reflections = new Reflections();
        Set<Class<? extends BotanMessageListenerRegister>> classes = reflections.getSubTypesOf(BotanMessageListenerRegister.class);
        classes.forEach(clazz -> {
            try {
                final BotanMessageListenerRegister register = clazz.newInstance();
                register.initialize();
                register.register(this);
                registers.add(register);
            } catch (final InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }


    final void run() {
        setActions();
        this.actions.forEach(x -> listeners.add(BotanMessageListenerBuilder.build(this.botan, x)));
    }

    public final void receive(BotanMessageSimple message) {
        this.listeners.stream().filter(listener -> message.getBody() != null).forEach(listener -> {
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
        this.registers.forEach(BotanMessageListenerRegister::beforeShutdown);
    }

    public final void hear(final String pattern, final String description, final Consumer<BotanMessage> action) {
        actions.add(new BotanMessageListenerSetter() {
            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getPatternString() {
                return pattern;
            }

            @Override
            public void accept(BotanMessageListener botanMessageListener) {
                botanMessageListener.setAllReceived(true);
                botanMessageListener.setDescription(description);
                botanMessageListener.setPattern(pattern);
                botanMessageListener.setAction(action);
            }
        });
    }

    public final void respond(final String pattern, final String description, final Consumer<BotanMessage> action) {
        actions.add(new BotanMessageListenerSetter() {
            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getPatternString() {
                return pattern;
            }

            @Override
            public void accept(BotanMessageListener botanMessageListener) {
                botanMessageListener.setAllReceived(false);
                botanMessageListener.setDescription(description);
                botanMessageListener.setPattern(pattern);
                botanMessageListener.setAction(action);
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
}
