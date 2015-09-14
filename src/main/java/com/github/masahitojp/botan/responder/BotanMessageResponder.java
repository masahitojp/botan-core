package com.github.masahitojp.botan.responder;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.message.BotanMessage;

import java.util.function.Consumer;
import java.util.regex.Pattern;


public class BotanMessageResponder {

    private final Botan botan;

    private String description;
    private String patternString;
    private Pattern pattern;
    private Consumer<BotanMessage> action;
    private boolean allReceived = false;

    public BotanMessageResponder(final Botan botan) {
        this.botan = botan;
    }

    public final Pattern getPattern() {
        return pattern;
    }

    public final void setPattern(final String str) {
        this.patternString = str;
        final String replyPattern;
        if (allReceived) {
            replyPattern = String.format("^(?!@?%s:?\\s+)%s", botan.getName(), str);
        } else {
            replyPattern = String.format("^@?%s:?\\s+%s", botan.getName(), str);
        }
        this.pattern = Pattern.compile(replyPattern);
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

    public final void setAllReceived(final boolean allReceived) {
        this.allReceived = allReceived;
    }

    @Override
    public String toString() {
        final String prefix = this.allReceived ? "" : botan.getName() + " ";
        return String.format("| %s%s - %s\n", prefix, this.patternString, this.description);
    }
}
