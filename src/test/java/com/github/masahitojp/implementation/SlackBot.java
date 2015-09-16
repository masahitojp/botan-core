package com.github.masahitojp.implementation;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.exception.BotanException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackBot {

    static public void main(final String[] Args) {

        final Botan botan = new Botan.BotanBuilder()
                .addEnvironmentVariablesToGlobalProperties()
                .build();
        try {
            botan.start();
        } catch (final BotanException ex) {
            log.warn(ex.getMessage());
        }

    }
}
