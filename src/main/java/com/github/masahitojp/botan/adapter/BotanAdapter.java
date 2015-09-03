package com.github.masahitojp.botan.adapter;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.exception.BotanException;
import com.github.masahitojp.botan.message.BotanMessage;


public interface BotanAdapter {

    void run() throws BotanException;

    void say(BotanMessage message);

    void initialize(Botan botan);

    void beforeShutdown();
}
