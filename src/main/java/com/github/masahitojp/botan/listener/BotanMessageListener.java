package com.github.masahitojp.botan.listener;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.message.BotanMessage;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;


public class BotanMessageListener {

    private final Botan botan;

    private String description;
    private Pattern pattern;
    private Consumer<BotanMessage> action;
    private boolean allReceived = false;

    public final Pattern getPattern() {
        return pattern;
    }

    public final String getDescription() {
        return description;
    }

    public BotanMessageListener(final Botan botan) {
        this.botan = botan;
    }

    public final List<BotanMessageListener> getListeners() {
        return botan.getListeners();
    }

    public final void setAction(final Consumer<BotanMessage> action) {
        this.action = action;
    }
    public final void apply(final BotanMessage message) {
        this.action.accept(message);
    }

    public final void setDescription(final String description) {
        this.description = description;
    }

    public final void setPattern(final String str) {
        final String pattern;
        if (allReceived) {
            pattern = String.format("^(?!@?%s:?\\s+)%s", botan.getName(), str);
        } else {
            pattern = String.format("^@?%s:?\\s+%s", botan.getName(), str);
        }
        this.pattern = Pattern.compile(pattern);
    }

    public final void setAllReceived(final boolean allReceived) {
        this.allReceived = allReceived;
    }


    @Override
    public String toString() {
        return pattern.toString() + " " + this.description;
    }
}
