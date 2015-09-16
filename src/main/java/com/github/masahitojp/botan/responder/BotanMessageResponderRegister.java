package com.github.masahitojp.botan.responder;

import com.github.masahitojp.botan.Robot;

public interface BotanMessageResponderRegister {
    /**
     * register the bot method
     * use Robot#respond or Robot#hear
     *
     * @param robot Robot
     */
    void register(final Robot robot);

    default void initialize(final Robot robot) {
    }

    default void beforeShutdown() {
    }
}
