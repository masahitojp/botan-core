package com.github.masahitojp.botan.handler;

import com.github.masahitojp.botan.Robot;

public interface BotanMessageHandlers {
    /**
     * register the bot method
     * use Robot#respond or Robot#hear
     *
     * @param robot Robot
     */
    void register(final Robot robot);

    @SuppressWarnings("unused")
    default void initialize(final Robot robot) {
    }

    default void beforeShutdown() {
    }
}
