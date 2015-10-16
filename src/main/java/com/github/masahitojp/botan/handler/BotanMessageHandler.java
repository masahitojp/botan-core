package com.github.masahitojp.botan.handler;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.message.BotanMessage;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.regex.Pattern;


public final class BotanMessageHandler implements Comparable {

    private final Botan botan;

    private String description;
    private String patternString;
    private Pattern pattern;
    private Consumer<BotanMessage> handle;
    private boolean allReceived = false;

    public BotanMessageHandler(final Botan botan) {
        this.botan = botan;
    }

    public final Pattern getPattern() {
        return this.pattern;
    }
    public final String getDescription() {return this.description;}
    public final void setPattern(final String str) {
        this.patternString = str;
        final String replyPattern;
        if (allReceived) {
            replyPattern = String.format("^(?!@?%s:?\\s+)%s", botan.getName(), str);
        } else {
            replyPattern = String.format("^@?%s:?\\s+%s", botan.getName(), str);
        }
        this.pattern = Pattern.compile(replyPattern, Pattern.DOTALL);
    }

    public final void setHandle(final Consumer<BotanMessage> handle) {
        this.handle = handle;
    }

    public final void apply(final BotanMessage message) {
        this.handle.accept(message);
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
        return String.format("> %s%s - %s", prefix, this.patternString, this.description);
    }

    @Override
    public int compareTo(@Nonnull Object o) {
        BotanMessageHandler other = (BotanMessageHandler) o;
        if (other.allReceived) return 1;
        else return -1;
    }
}
