package com.github.masahitojp.botan.utils;

import com.github.masahitojp.botan.BotanMessage;
import com.github.masahitojp.botan.listener.BotanMessageListener;
import com.github.masahitojp.botan.listener.BotanMessageListenerSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BotanUtils {
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
                botanMessageListener.setDescription(description);
                botanMessageListener.setPattern(pattern);
                botanMessageListener.setAction(action);
                botanMessageListener.setAllReceived(false);
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
                botanMessageListener.setDescription(description);
                botanMessageListener.setPattern(pattern);
                botanMessageListener.setAction(action);
                botanMessageListener.setAllReceived(true);
            }
        });
    }
}
