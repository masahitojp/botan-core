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
import java.util.function.Supplier;
import java.util.regex.Matcher;

public class Robot {
    private final Botan botan;

    public Robot(final Botan botan) {
        this.botan = botan;
    }

    private void setActions() {
        final Reflections reflections = new Reflections();
        Set<Class<? extends BotanMessageListenerRegister>> classes = reflections.getSubTypesOf(BotanMessageListenerRegister.class);
        classes.forEach(clazz -> {
            try {
                clazz.newInstance().register(this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }


    final void run() {
        setActions();
        this.actions.forEach(x -> listeners.add(BotanMessageListenerBuilder.build(this.botan, x)));
    }

    @Getter
    private final List<BotanMessageListener> listeners = new ArrayList<>();

    public final void receive(BotanMessageSimple message) {
        this.getListeners().stream().filter(listener -> message.getBody() != null).forEach(listener -> {
            final Matcher matcher = listener.getPattern().matcher(message.getBody());
            if (matcher.find()) {
                listener.apply(
                        new BotanMessage(
                                this.botan,
                                matcher,
                                message
                        ));
            }
        });
    }

    private List<BotanMessageListenerSetter> actions = new ArrayList<>();
    private List<Supplier<Boolean>> beforeShutdowns = new ArrayList<>();

    final void doFinalize() {
        beforeShutdowns.stream().forEach(Supplier::get);
    }

    public final void beforeShutdown(final Supplier<Boolean> finalizeProps) {
        beforeShutdowns.add(finalizeProps);
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
}
