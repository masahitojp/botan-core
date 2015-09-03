package com.github.masahitojp.botan.utils;

import com.github.masahitojp.botan.message.BotanMessage;
import com.github.masahitojp.botan.listener.BotanMessageListener;
import com.github.masahitojp.botan.listener.BotanMessageListenerSetter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public final class BotanUtils {
    private static List<BotanMessageListenerSetter> actions = new ArrayList<>();
    private static List<Supplier<Boolean>> beforeShutdowns = new ArrayList<>();
    public static List<BotanMessageListenerSetter> getActions() {
        return new ArrayList<>(actions);
    }
    public static void doFinalize() {
        beforeShutdowns.stream().forEach(Supplier::get);
    }

    public static void beforeShutdown(final Supplier<Boolean> finalizeProps) {
        beforeShutdowns.add(finalizeProps);
    }

    public static void hear(final String pattern, final String description, final Consumer<BotanMessage> action) {
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

    public static void respond(final String pattern, final String description, final Consumer<BotanMessage> action) {
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

    public static <T> T getRandom(final List<T> list) {
        int index = new Random().nextInt(list.size());
        return list.get(index);
    }
}