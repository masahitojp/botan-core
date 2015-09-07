package com.github.masahitojp.botan.listener;

import com.github.masahitojp.botan.Botan;

public interface BotanMessageListenerRegister  {
    /**
     * register the bot method
     * use BotanUtis.hear or BotanUtis.respond
     */
    void register(final Botan botan);
}
