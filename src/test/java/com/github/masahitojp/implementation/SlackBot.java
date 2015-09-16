package com.github.masahitojp.implementation;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.adapter.SlackAdapter;
import com.github.masahitojp.botan.exception.BotanException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SlackBot {

    static public String envToOpt(final String envName) {
        Optional<String> javaDirectory = Optional.ofNullable(System.getenv(envName));
        return javaDirectory.orElse("");
    }

    static public void main(String[] Args) {

        final Botan botan = new Botan.BotanBuilder()
                .build();
        try {
            botan.start();
        } catch (final BotanException ex) {
            log.warn(ex.getMessage());
        }

    }
}
