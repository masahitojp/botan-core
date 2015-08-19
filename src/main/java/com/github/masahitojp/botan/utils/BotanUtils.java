package com.github.masahitojp.botan.utils;

import com.github.masahitojp.botan.BotanMessage;
import com.github.masahitojp.botan.listener.BotanMessageListener;
import com.github.masahitojp.botan.listener.BotanMessageListenerSetter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@UtilityClass
public final class BotanUtils {
    private static List<BotanMessageListenerSetter> actions = new ArrayList<>();

    public static List<BotanMessageListenerSetter> getActions() {
        return new ArrayList<>(actions);
    }

    public static void hear(final String pattern, final String description, final Consumer<BotanMessage> action) {
        actions.add(new BotanMessageListenerSetter() {
            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getPatterString() {
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
            public String getPatterString() {
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